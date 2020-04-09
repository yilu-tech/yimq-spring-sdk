package com.common.transaction.message;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.client.YIMQClient;
import com.common.transaction.constants.MessageServerTypeConstants;
import com.common.transaction.constants.MessageStatusConstants;
import com.common.transaction.constants.MessageTypeConstants;
import com.common.transaction.dao.MessageDao;
import com.common.transaction.dao.SubTaskDao;
import com.common.transaction.subTask.EcSubTask;
import com.common.transaction.subTask.TccSubTask;
import com.common.transaction.utils.YimqFrameDateUtils;
import com.common.transaction.entity.YimqMessage;
import com.common.transaction.entity.MessageEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 17:13
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class TransactionYimqMessage extends YimqMessage {

    public List<TccSubTask> tccSubTaskList = new ArrayList<>();

    public List<EcSubTask> ecSubTaskList = new ArrayList<>();

    public boolean prepared = false;

    @Resource
    private MessageDao messageDao;

    @Resource
    private SubTaskDao subTaskDao;

    public TransactionYimqMessage(){}

    public TransactionYimqMessage(YIMQClient client, String topic){
        super(client,topic);
    }

    public TransactionYimqMessage begin() throws Exception {
        if (this.client.hasTransactionMessage()) {
            throw new Exception(" MicroApi transaction message already exists.");
        }
        create();
        client.setTransactionMessage(this);
        return this;
    }

    private void create() throws Exception {
        //创建远程事务记录
        JSONObject messageInfo = createRemoteTransactionRecord();
        id = messageInfo.getInteger("id");
        //本地数据库记录事务
        this.createLocalTransactionRecord(messageInfo);
        this.messageEntity = messageDao.selectMessageByIdForUpdate(id);
    }

    //
    public TccSubTask tcc(String processor) throws Exception {
        if (!this.client.hasTransactionMessage()) {
            throw new Exception(" Not begin a yimq transaction ");
        }
        return new TccSubTask(this.client,this.client.getTransactionMessage(),processor);
    }


    //开启远程事务
    private JSONObject createRemoteTransactionRecord() throws Exception {
        Map<String,Object> context = new HashMap<>();
        context.put("topic",this.getTopic());
        context.put("type", MessageServerTypeConstants.TRANSACTION);
        context.put("delay",this.delay);
        return this.client.callServer("create",context);
    }

    //开启本地事务
    private void createLocalTransactionRecord(JSONObject messageInfo) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(messageInfo.getInteger("id"));
        messageEntity.setStatus(MessageStatusConstants.PENDING);
        messageEntity.setType(MessageTypeConstants.TRANSACTION);
        messageEntity.setTopic(this.topic);
        messageEntity.setCreatedAt(YimqFrameDateUtils.currentFormatDate());
        messageEntity.setUpdatedAt(YimqFrameDateUtils.currentFormatDate());
        messageDao.saveOrUpdateMessage(messageEntity);
    }

    public void addTccSubTask(TccSubTask tccSubTask){
        tccSubTaskList.add(tccSubTask);
    }

    public void addEcSubTask(EcSubTask subTask) {
        ecSubTaskList.add(subTask);
    }

    private TransactionYimqMessage prepare() {
        Map<String,Object> context = new HashMap<>();
        List<Map<String,Object>> prepareSubTaskList = new ArrayList<Map<String,Object>>();
        context.put("message_id",this.id);
        context.put("prepare_subtasks",prepareSubTaskList);
        JSONObject result;
        //如果没有ecSubTask就不发起远程调用
        if (0 == ecSubTaskList.size()) {
            this.prepared = true;
            return this;
        }
        Map<String, Object> ecContextMap;
        for(EcSubTask ecSubTask:ecSubTaskList) {
            ecContextMap = ecSubTask.getContext();
            prepareSubTaskList.add(ecContextMap);
        }

        result = this.client.callServer("prepare",context);
        preparedSaveToJob(result);
        this.prepared = true;
        return this;
    }

    private void preparedSaveToJob(JSONObject result) {
        for (int i =0 ;i < ecSubTaskList.size();i++) {
            EcSubTask ecSubTask = ecSubTaskList.get(i);
            ecSubTask.id = Integer.valueOf(((Map)((List) result.get("prepare_subtasks")).get(i)).get("id").toString());
            ecSubTask.save();
        }
    }

    /**
     * 提交操作
     * @return
     */
    public TransactionYimqMessage commit() {
        this.prepare();

        this.localCommit();

        this.remoteCommit();

        return this;
    }

    /**
     * 本地提交
     */
    private void localCommit() {
        this.messageEntity.setStatus(MessageStatusConstants.DONE);
        messageDao.saveOrUpdateMessage(this.messageEntity);
    }

    /**
     * 远程提交
     */
    private void remoteCommit() {
        Map<String,Object> context = new HashMap<>();
        context.put("message_id",this.id);
        JSONObject result = this.client.callServer("confirm",context);
    }

    /**
     * 事务回滚
     * @return
     */
    public TransactionYimqMessage rollback() {
        //DB.rollBack
        this.messageEntity.setStatus(MessageStatusConstants.CANCELED);
        messageDao.saveOrUpdateMessage(messageEntity);
        this.remoteRollback();
        return this;
    }

    /**
     * 远程事务回滚
     */
    private void remoteRollback() {
        Map<String,Object> context = new HashMap<>();
        context.put("message_id",this.id);
        JSONObject result = this.client.callServer("cancel",context);
    }

}
