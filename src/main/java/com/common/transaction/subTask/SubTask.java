package com.common.transaction.subTask;

import com.common.transaction.client.YIMQClient;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.SubTaskEntity;
import com.common.transaction.message.YimqTransactionMessage;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 16:51
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Resource
public abstract class SubTask {

    public String serverType;
    public String type;
    protected YIMQClient client;
    protected YimqTransactionMessage message;
    protected Object data;
    public SubTaskEntity subTaskEntity;

    public SubTask() {
    }

    public SubTask(YIMQClient client, YimqTransactionMessage message) {
        this.client = client;
        this.message = message;
    }

    public SubTask data(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }

    public abstract List<SubTask> join(MessageEntity messageEntity);

    public abstract Map<String,Object> getContext(MessageEntity messageEntity);


}
