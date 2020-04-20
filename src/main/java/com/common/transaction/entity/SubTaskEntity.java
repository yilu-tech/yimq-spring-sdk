package com.common.transaction.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/4/1 15:10
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class SubTaskEntity implements Serializable {
    private static final long serialVersionUID = 6111473372301104688L;

    private Integer id;
    private Integer subTaskId;
    private Integer messageId;
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

    public Integer getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Integer subTaskId) {
        this.subTaskId = subTaskId;
    }
}
