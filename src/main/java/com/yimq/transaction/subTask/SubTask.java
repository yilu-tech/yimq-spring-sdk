package com.yimq.transaction.subTask;

import com.yimq.transaction.client.YIMQClient;
import com.yimq.transaction.entity.SubTaskEntity;
import com.yimq.transaction.message.TransactionYimqMessage;

import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 16:51
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public abstract class SubTask {

    public String serverType;
    public String type;
    public Integer id;
    protected YIMQClient client;
    protected TransactionYimqMessage message;
    protected String data;
    public SubTaskEntity subTaskEntity;

    public SubTask() {
    }

    public SubTask(YIMQClient client, TransactionYimqMessage message) {
        this.client = client;
        this.message = message;
    }

    public SubTask data(String data) {
        this.data = data;
        return this;
    }

    public String getData() {
        return this.data;
    }

    public abstract Object join();

    public abstract Map<String,Object> getContext();


}
