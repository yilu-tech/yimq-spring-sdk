package com.common.transaction.entity;

import com.alibaba.fastjson.JSONArray;
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
    private Integer message_id;
    private Integer typeCode;
    private String type;
    private String processor;
    private Object data;
    private JSONObject tryResult;
    private Integer status;
    private String createTime;
    private String updateTime;
    private JSONArray messageIds;
    private JSONArray processIds;

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

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public JSONObject getTryResult() {
        return tryResult;
    }

    public void setTryResult(JSONObject tryResult) {
        this.tryResult = tryResult;
    }

    public JSONArray getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(JSONArray messageIds) {
        this.messageIds = messageIds;
    }

    public JSONArray getProcessIds() {
        return processIds;
    }

    public void setProcessIds(JSONArray processIds) {
        this.processIds = processIds;
    }
}
