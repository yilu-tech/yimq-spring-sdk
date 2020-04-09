package com.common.transaction.entity;

import java.io.Serializable;

/**
 * create by gaotiedun ON 2020/3/25 17:15
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 8822680502391909658L;

    private Integer id;
    private String username;
    private Integer status;
    private String createdAt;
    private String updatedAt;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

