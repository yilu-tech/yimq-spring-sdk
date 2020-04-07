package com.yimq.transaction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimq.transaction.constants.Constants;
import com.yimq.transaction.constants.ResponseCodeConstants;
import com.yimq.transaction.constants.ResponseMessageConstants;
import com.yimq.transaction.constants.SubTaskStatusConstants;
import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.entity.ProcessesEntity;
import com.yimq.transaction.http.WrapResponse;
import com.yimq.transaction.utils.CommonUtils;
import com.yimq.transaction.dao.ProcessDao;
import com.yimq.transaction.service.TccTransactionService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

/**
 * create by gaotiedun ON 2020/3/24 17:22
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component("tccTransactionService")
public  class TccTransactionServiceImpl extends TransactionService implements TccTransactionService {

    private static final Logger log = Logger.getLogger(TccTransactionServiceImpl.class);

    @Resource
    private CommonUtils commonUtils;
    @Resource
    private ProcessDao processDao;

    @Override
    @Transactional
    public WrapResponse runTry(YimqContextEntity context) {
        commonUtils.checkType(Constants.TCC,context.getType());
        this.saveProcessRecord(SubTaskStatusConstants.PREPARING);
        Object result = null;
        try {
            this.setAndLockProcessModel(context.getId());
            result = this.prepare();
            this.getProcessesEntity().setTryResult(JSONObject.toJSON(result));
            this.saveProcessRecord(SubTaskStatusConstants.PREPARED);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new WrapResponse(ResponseCodeConstants.FAIL, ResponseMessageConstants.EXCEPTION);
        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS,result);

    }

    @Override
    @Transactional
    public WrapResponse runConfirm(YimqContextEntity yimqContextEntity) {
        ProcessesEntity processesEntity = this.setAndLockProcessModel(yimqContextEntity.getId());
        //判断状态
        if (processesEntity.getStatus() == SubTaskStatusConstants.DONE) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new WrapResponse(ResponseCodeConstants.SUCCESS, ResponseMessageConstants.MESSAGE_IS_CONSUMED);
        }
        // 判断状态
        if (processesEntity.getStatus() != SubTaskStatusConstants.PREPARED) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String status = commonUtils.getStatusMap().get(processesEntity.getStatus());
            log.error(" tcc transaction handler happen exception. status is "+status);
            return new WrapResponse(ResponseCodeConstants.FAIL," tcc transaction handler happen exception. status is "+status);
        }
        Object result = null;
        try {
            result = this.prepare();
            processesEntity.setStatus(SubTaskStatusConstants.DONE);
            processDao.saveOrUpdateProcess(processesEntity);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new WrapResponse(ResponseCodeConstants.FAIL,ResponseMessageConstants.EXCEPTION);
        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS,result);
    }

    @Override
    @Transactional
    public WrapResponse runCancel(YimqContextEntity yimqContextEntity) {
        ProcessesEntity processesEntity = this.setAndLockProcessModel(yimqContextEntity.getId());
        if (processesEntity.getStatus() == SubTaskStatusConstants.CANCELED) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new WrapResponse(ResponseCodeConstants.SUCCESS, ResponseMessageConstants.MESSAGE_IS_CONSUMED);
        }
        //判断事务逻辑是否已经执行
        if (processesEntity.getStatus() != SubTaskStatusConstants.PREPARED) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String status = commonUtils.getStatusMap().get(processesEntity.getStatus());
            log.error(" tcc transaction handler happen exception. status is "+status);
            return new WrapResponse(ResponseCodeConstants.FAIL," tcc transaction handler happen exception. status is "+status);
        }
        Object result = null;

        try {
            result = this.prepare();
            processesEntity.setStatus(SubTaskStatusConstants.CANCELED);
            processDao.saveOrUpdateProcess(processesEntity);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new WrapResponse(ResponseCodeConstants.FAIL,ResponseMessageConstants.EXCEPTION);
        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS,result);
    }

    @Override
    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.prepare();
    }

}
