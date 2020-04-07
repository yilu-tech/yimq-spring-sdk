package com.yimq.transaction.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * create by gaotiedun ON 2020/3/24 17:27
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class YimqContextEntity implements Serializable {

    private static final long serialVersionUID = -7805050851374970917L;

    private String processor;
    private String processer;
    private String type;
    private Integer id;
    private Integer message_id;
    private JSONObject data;

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getProcesser() {
        return processer;
    }

    public void setProcesser(String processer) {
        this.processer = processer;
    }
}
