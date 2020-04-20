package com.common.transaction.subTask;

import com.common.transaction.client.YIMQClient;
import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.SubTaskTypeConstants;
import com.common.transaction.dao.SubTaskDao;
import com.common.transaction.entity.SubTaskEntity;
import com.common.transaction.message.TransactionYimqMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 20:20
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class EcSubTask extends ProcessorSubTask {
    private String serverType = "EC";
    public Integer type = SubTaskTypeConstants.EC;

    @Resource
    private SubTaskDao subTaskDao;

    public EcSubTask(){}

    public EcSubTask(YIMQClient client, TransactionYimqMessage transactionMessage, String processor){
        super(client,transactionMessage,processor);
    }

    @Override
    public Object join() {
        this.message.addEcSubTask(this);
        return this;
    }

    public void save() {
        subTaskEntity = new SubTaskEntity();
        subTaskEntity.setSubTaskId(id);
        subTaskEntity.setMessageId(message.id);
        subTaskEntity.setStatus(SubTaskStatusConstants.PREPARED);
        subTaskEntity.setType(type);
        subTaskDao.saveOrUpdateSubTask(subTaskEntity);
    }

    @Override
    public Map<String,Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("type",this.serverType);
        context.put("processor",this.processor);
        context.put("data",this.getData());
        return context;
    }
}
