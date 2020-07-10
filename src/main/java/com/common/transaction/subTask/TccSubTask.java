package com.common.transaction.subTask;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.client.YIMQClient;
import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.SubTaskTypeConstants;
import com.common.transaction.dao.SubTaskDao;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.SubTaskEntity;
import com.common.transaction.message.YimqTransactionMessage;
import com.common.transaction.utils.YimqFrameDateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public TccSubTask(YIMQClient client, YimqTransactionMessage message, String processor) {
        super(client, message, processor);
    }

    @Override
    public List<SubTask> join(MessageEntity messageEntity) {
        JSONObject result = this.client.callServer("subTask",this.getContext(messageEntity));
        int subTaskId = result.getInteger("id");
        this.prepareResult = result.getInteger("prepareResult");
        this.subTaskEntity = new SubTaskEntity();
        subTaskEntity.setSubTaskId(subTaskId);
        subTaskEntity.setMessageId(messageEntity.getMessage_id());
        subTaskEntity.setStatus(SubTaskStatusConstants.PREPARED);
        subTaskEntity.setType(this.type);
        subTaskEntity.setCreateTime(YimqFrameDateUtils.currentFormatDate());
        subTaskDao.saveOrUpdateSubTask(subTaskEntity);
        List<SubTask> tccSubTaskList = new ArrayList<>();
        tccSubTaskList.add(this);
        return tccSubTaskList;
    }

    @Override
    public Map<String,Object> getContext(MessageEntity messageEntity) {
        Map<String,Object> context = new HashMap<>();
        context.put("message_id",messageEntity.getMessage_id());
        context.put("type",this.serverType);
        context.put("processor",this.processor);
        context.put("data",this.data);
        return context;
    }
}
