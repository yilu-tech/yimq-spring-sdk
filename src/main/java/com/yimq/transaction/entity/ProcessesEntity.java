package com.yimq.transaction.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * create by gaotiedun ON 2020/3/24 17:59
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class ProcessesEntity implements Serializable {

    private static final long serialVersionUID = 6111473372301104688L;

    private Integer id;
    private Integer messageId;
    private Integer type;
    private String processor;
    private Object data;
    private Object tryResult;
    private Integer status;
    private String createTime;
    private String updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getTryResult() {
        return tryResult;
    }

    public void setTryResult(Object tryResult) {
        this.tryResult = tryResult;
    }
}
