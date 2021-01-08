package com.common.transaction.subTask;

import com.common.transaction.client.YIMQClient;
import com.common.transaction.constants.ProcessesStatusConstants;
import com.common.transaction.constants.SubTaskTypeConstants;
import com.common.transaction.dao.SubTaskDao;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.SubTaskEntity;
import com.common.transaction.message.YimqTransactionMessage;
import com.common.transaction.utils.YimqFrameDateUtils;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 20:20
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class EcSubTask extends ProcessorSubTask {
    private static final Logger log = Logger.getLogger(EcSubTask.class);

    private String serverType = "EC";
    public Integer type = SubTaskTypeConstants.EC;

    public EcSubTask(){}

    public EcSubTask(YIMQClient client, YimqTransactionMessage transactionMessage, String processor){
        super(client,transactionMessage,processor);
    }

    @Override
    public List<SubTask> join(MessageEntity messageEntity) {
        this.client = client;
        List<SubTask> ecSubTaskList = new ArrayList<>();
        ecSubTaskList.add(this);
        return ecSubTaskList;
    }

    public void save(SubTaskDao subTaskDao, BigInteger messageId, BigInteger subTaskId) {
        subTaskEntity = new SubTaskEntity();
        subTaskEntity.setSubTaskId(subTaskId);
        subTaskEntity.setMessageId(messageId);
        subTaskEntity.setStatus(ProcessesStatusConstants.PREPARED);
        subTaskEntity.setCreateTime(YimqFrameDateUtils.currentFormatDate());
        subTaskEntity.setUpdateTime(YimqFrameDateUtils.currentFormatDate());
        subTaskEntity.setType(type);
        subTaskDao.saveOrUpdateSubTask(subTaskEntity);
    }

    @Override
    public Map<String,Object> getContext(MessageEntity messageEntity) {
        Map<String, Object> context = new HashMap<>();
        context.put("type",this.serverType);
        context.put("processor",this.processor);
        context.put("data",this.getData());
        return context;
    }
}
