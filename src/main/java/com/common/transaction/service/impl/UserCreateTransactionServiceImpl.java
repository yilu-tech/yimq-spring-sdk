package com.common.transaction.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.dao.UserDao;
import com.common.transaction.exception.MyTransactionException;
import com.common.transaction.utils.YimqFrameDateUtils;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.entity.UserEntity;
import com.common.transaction.service.UserCreateService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * create by gaotiedun ON 2020/3/25 17:14
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component("userService")
public class UserCreateTransactionServiceImpl extends XaTransactionServiceImpl implements UserCreateService  {

    private static final Logger log = Logger.getLogger(UserCreateTransactionServiceImpl.class);

    @Resource
    private UserDao userDao;

    @Resource
    private ProcessDao processDao;

    @Override
    public Object prepare(){
        return null;
    }

    public Object xaTryCreate() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(super.data.get("username").toString());
        userEntity.setStatus(Integer.parseInt(this.data.get("status").toString()));
        int saveResult = userDao.saveOrUpdateUser(userEntity);
        if (1 != saveResult) {
            throw new MyTransactionException("save user info happens Exception");
        }
        return userEntity;
    }

    public Object tccTryCreate(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(super.data.get("username").toString());
        userEntity.setStatus(YimqConstants.PENDING);
        userEntity.setCreatedAt(YimqFrameDateUtils.currentFormatDate());
        userEntity.setUpdatedAt(YimqFrameDateUtils.currentFormatDate());
        int saveResult = userDao.saveOrUpdateUser(userEntity);
        if (1 != saveResult) {
            throw new MyTransactionException("save user info happens Exception");
        }
        return userEntity;
    }

    public Object tccTryConfirm(){
        ProcessesEntity processesEntity = processDao.selectProcessById(super.processesEntity.getId());
        if (null == processesEntity) {
            log.error(" query subTaskProcess is null ");
        }
        JSONObject tryResult = (JSONObject) JSON.toJSON(processesEntity.getTryResult());
        Integer userId = (Integer) tryResult.get("id");
        UserEntity userEntity = userDao.selectUserByIdForUpdate(userId);
        if (null == userEntity) {
            log.error(" query user info is null ");
        }
        userEntity.setStatus(YimqConstants.ACTIVE);
        userDao.saveOrUpdateUser(userEntity);
        return userEntity;
    }

    public Object tccTryCancel() {
        ProcessesEntity processesEntity = processDao.selectProcessById(super.processesEntity.getId());
        if (null == processesEntity) {
            log.error(" query subTaskProcess is null ");
        }
        JSONObject tryResult = (JSONObject) JSON.toJSON(processesEntity.getTryResult());
        Integer userId = (Integer) tryResult.get("id");
        int deleteResult = userDao.deleteUserById(userId);
        if (1 != deleteResult) {
            throw new MyTransactionException("save user info happens Exception");
        }
        return deleteResult;
    }

    public Object ecTryUpdate() {
        Integer userId = Integer.parseInt(this.data.get("id").toString());
        UserEntity userEntity = userDao.selectUserByIdForUpdate(userId);
        String username = this.data.get("username").toString();
        userEntity.setUsername(username);
        userDao.saveOrUpdateUser(userEntity);
        return userEntity;
    }

    public Object bcstTryUpdateListener(){
        Integer userId = Integer.parseInt(this.data.get("id").toString());
        UserEntity userEntity = userDao.selectUserByIdForUpdate(userId);
        String username = this.data.get("username").toString();
        userEntity.setUsername(username);
        userDao.saveOrUpdateUser(userEntity);
        return userEntity;
    }
}
