package com.common.transaction.subTask;

import com.alibaba.fastjson.JSONObject;
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
 * create by gaotiedun ON 2020/3/31 20:30
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class TccSubTask extends ProcessorSubTask {

    public String serverType = "TCC";
    public Integer type = SubTaskTypeConstants.TCC;
    public Integer prepareResult;

    @Resource
    private SubTaskDao subTaskDao;

    public TccSubTask(){}

    public TccSubTask(YIMQClient client, TransactionYimqMessage message, String processor) {
        super(client, message, processor);
    }

    @Override
    public Object join() {
        JSONObject result = this.client.callServer("subTask",this.getContext());
        this.id = result.getInteger("id");
        this.prepareResult = result.getInteger("prepareResult");
        this.subTaskEntity = new SubTaskEntity();
        subTaskEntity.setId(this.id);
        subTaskEntity.setMessageId(this.message.id);
        subTaskEntity.setStatus(SubTaskStatusConstants.PREPARED);
        subTaskEntity.setType(this.type);
        subTaskDao.saveOrUpdateSubTask(subTaskEntity);

        this.message.addTccSubTask(this);
        return this;
    }

    @Override
    public Map<String,Object> getContext() {
        Map<String,Object> context = new HashMap<>();
        context.put("message_id",this.message.id);
        context.put("type",this.serverType);
        context.put("processor",this.processor);
        context.put("data",this.data);
        return context;
    }
}
