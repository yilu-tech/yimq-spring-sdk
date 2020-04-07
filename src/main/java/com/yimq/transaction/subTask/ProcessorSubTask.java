package com.yimq.transaction.subTask;

import com.yimq.transaction.client.YIMQClient;
import com.yimq.transaction.message.TransactionYimqMessage;

/**
 * create by gaotiedun ON 2020/3/31 17:27
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public abstract class ProcessorSubTask extends SubTask {

    protected String processor;

    public ProcessorSubTask(){}

    public ProcessorSubTask(YIMQClient client, TransactionYimqMessage message, String processor) {
        super(client, message);
        this.processor = processor;
    }
}
