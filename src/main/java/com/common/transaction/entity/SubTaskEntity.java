package com.common.transaction.entity;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * create by gaotiedun ON 2020/4/1 15:10
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class SubTaskEntity implements Serializable {
    private static final long serialVersionUID = 6111473372301104688L;

    private BigInteger id;
    private BigInteger subTaskId;
    private BigInteger messageId;
    private Integer type;
    private Object data;
    private Integer status;
    private String createTime;
    private String updateTime;

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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setSubTaskId(BigInteger subTaskId) {
        this.subTaskId = subTaskId;
    }

    public BigInteger getMessageId() {
        return messageId;
    }

    public void setMessageId(BigInteger messageId) {
        this.messageId = messageId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
