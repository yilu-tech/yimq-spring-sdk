package com.common.transaction.entity;

import com.alibaba.fastjson.JSONObject;

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
    private Integer message_id;
    private String topic;
    private Integer type;
    private JSONObject data;
    private String reservedAt;
    private Integer attempts;
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

    public String getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(String reservedAt) {
        this.reservedAt = reservedAt;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", message_id=" + message_id +
                ", topic='" + topic + '\'' +
                ", type=" + type +
                ", data=" + data +
                ", reservedAt='" + reservedAt + '\'' +
                ", attempts=" + attempts +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
