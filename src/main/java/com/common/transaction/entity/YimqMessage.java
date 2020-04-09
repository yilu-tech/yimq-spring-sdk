package com.common.transaction.entity;

import com.common.transaction.client.YIMQClient;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * create by gaotiedun ON 2020/3/31 16:28
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class YimqMessage implements Serializable {
    private static final long serialVersionUID = -3048610133154675667L;
    @Resource
    protected YIMQClient client;
    public String topic;
    public MessageEntity messageEntity;
    public Integer id;
    public Integer delay = 2000;
    public String data;

    public YimqMessage(){}

    public YimqMessage(YIMQClient client, String topic){
        this.client = client;
        this.topic = topic;
    }

    public YimqMessage delay(Integer millisecond) {
        this.delay = millisecond;
        return this;
    }

    public YimqMessage data(String data) {
        this.data = data;
        return this;
    }

    public String getTopic() throws Exception {
        if (StringUtils.isEmpty(this.topic)) {
            throw new Exception(" topic not set.");
        }
        return this.topic;
    }

}
