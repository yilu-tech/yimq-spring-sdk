package com.common.transaction.message;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.client.YIMQClient;
import com.common.transaction.constants.MessageServerTypeConstants;
import com.common.transaction.constants.MessageStatusConstants;
import com.common.transaction.constants.MessageTypeConstants;
import com.common.transaction.dao.MessageDao;
import com.common.transaction.dao.SubTaskDao;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.YimqMessage;
import com.common.transaction.subTask.EcSubTask;
import com.common.transaction.subTask.SubTask;
import com.common.transaction.subTask.TccSubTask;
import com.common.transaction.utils.YimqFrameDateUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.math.BigInteger;
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
public class YimqTransactionMessage extends YimqMessage {

    private static final Logger log = Logger.getLogger(YimqTransactionMessage.class);

    public boolean prepared = false;

    @Resource
    private MessageDao messageDao;

    @Resource
    private SubTaskDao subTaskDao;

    @Resource
    private YIMQClient client;

    public YimqTransactionMessage(){}

    public MessageEntity start(String topic,JSONObject data) throws Exception {
        return this.begin(topic,data);
    }

    public MessageEntity begin(String topic,JSONObject data) throws Exception{
        MessageEntity messageEntity = this.create(topic,data);
        client.setTransactionMessage(this);
        return messageEntity;
    }

    private MessageEntity create(String topic,JSONObject data) throws Exception {
        //创建远程事务记录
        JSONObject messageInfo = createRemoteTransactionRecord(data,topic);
        //本地数据库记录事务
        createLocalTransactionRecord(messageInfo,topic);
        return messageDao.selectMessageById(messageInfo.getBigInteger("id"));
    }

    public TccSubTask tcc(String processor){
        return new TccSubTask(this.client,this.client.getTransactionMessage(),processor);
    }

    //开启远程事务
    public JSONObject createRemoteTransactionRecord(JSONObject data,String topic) {
        Map<String,Object> context = new HashMap<>();
        context.put("topic",topic);
        context.put("type", MessageServerTypeConstants.TRANSACTION);
        context.put("delay",this.delay);
        context.put("data",data);
        return this.client.callServer("create",context);
    }

    //开启本地事务
    public void createLocalTransactionRecord(JSONObject messageInfo,String topic) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessage_id(messageInfo.getBigInteger("id"));
        messageEntity.setStatus(MessageStatusConstants.PENDING);
        messageEntity.setType(MessageTypeConstants.TRANSACTION);
        messageEntity.setTopic(topic);
        messageEntity.setCreatedAt(YimqFrameDateUtils.currentFormatDate());
        messageEntity.setUpdatedAt(YimqFrameDateUtils.currentFormatDate());
        messageDao.saveOrUpdateMessage(messageEntity);
    }

    public void prepare(MessageEntity messageEntity,List<SubTask> subTaskList) {
        Map<String,Object> context = new HashMap<>();
        List<Map<String,Object>> prepareSubTaskList = new ArrayList<Map<String,Object>>();
        context.put("message_id",messageEntity.getMessage_id());
        context.put("prepare_subtasks",prepareSubTaskList);
        //如果没有ecSubTask就不发起远程调用并抛出异常
        if (0 == subTaskList.size()) {
            this.prepared = true;
        }
        Map<String, Object> ecContextMap;
        for(SubTask ecSubTask:subTaskList) {
            ecContextMap = ecSubTask.getContext(messageEntity);
            prepareSubTaskList.add(ecContextMap);
        }
        JSONObject result = this.client.callServer("prepare",context);
        if (null == result) {
            throw new RuntimeException(" the subtask prepare return is null ! ");
        }
        preparedSaveToJob(result,messageEntity.getMessage_id(),subTaskList);
        this.prepared = true;
    }

    public void preparedSaveToJob(JSONObject result, BigInteger messageId, List<SubTask> ecSubTaskList) {
        for (int i =0 ;i < ecSubTaskList.size();i++) {
            EcSubTask ecSubTask = (EcSubTask) ecSubTaskList.get(i);
            BigInteger subTaskId = BigInteger.valueOf(Long.parseLong(((Map)((List) result.get("prepare_subtasks")).get(i)).get("id").toString()));
            ecSubTask.save(subTaskDao,messageId,subTaskId);
        }
    }

    /**
     * 提交操作
     * @return
     */
    public YimqTransactionMessage commit(DataSourceTransactionManager dataSourceTransactionManager,
                                         TransactionStatus transactionStatus, MessageEntity messageEntityCommit, List<SubTask> subTaskList) {
        MessageEntity messageEntity = messageDao.selectMessageByIdForUpdate(messageEntityCommit.getMessage_id());
        this.prepare(messageEntity,subTaskList);
        messageEntity.setStatus(MessageStatusConstants.DONE);
        messageDao.saveOrUpdateMessage(messageEntity);
        dataSourceTransactionManager.commit(transactionStatus);
        this.remoteCommit(messageEntity);
        return this;
    }

    /**
     * 远程提交
     */
    public void remoteCommit(MessageEntity messageEntity) {
        Map<String,Object> context = new HashMap<>();
        context.put("message_id",messageEntity.getMessage_id());
        this.client.callServer("confirm",context);
    }

    /**
     * 事务回滚
     * @return
     */
    public YimqTransactionMessage rollback(MessageEntity messageEntity) {
        //DB.rollBack
        messageEntity.setStatus(MessageStatusConstants.CANCELED);
        messageDao.saveOrUpdateMessage(messageEntity);
        this.remoteRollback(messageEntity);
        return this;
    }

    /**
     * 远程事务回滚
     */
    public void remoteRollback(MessageEntity messageEntity) {
        Map<String,Object> context = new HashMap<>();
        context.put("message_id",messageEntity.getMessage_id());
        this.client.callServer("cancel",context);
    }

}
