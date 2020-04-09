package com.common.transaction.entity;

import java.io.Serializable;

/**
 * create by gaotiedun ON 2020/3/31 13:55
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = -7371349121571143118L;

    private Integer id;
    private String topic;
    private Integer type;
    private Integer status;
    private String createdAt;
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
