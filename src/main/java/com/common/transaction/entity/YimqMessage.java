package com.common.transaction.entity;

import com.common.transaction.client.YIMQClient;
import org.springframework.util.StringUtils;

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
    //protected YIMQClient client;
    public String topic;
    //public MessageEntity messageEntity;
    //public Integer id;
    public Integer delay = 2000;
    public String data;

    public YimqMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

   /* public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }*/

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getData() {
        return data;
    }

    public YimqMessage setData(String data) {
        this.data = data;
        return this;
    }

    public YimqMessage(){}

    public YimqMessage(YIMQClient client, String topic){
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
