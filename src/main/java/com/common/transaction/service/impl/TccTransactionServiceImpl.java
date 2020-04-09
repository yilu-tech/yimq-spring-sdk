package com.common.transaction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.service.TccTransactionService;
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
@Component("tccTransactionServiceImpl")
public  class TccTransactionServiceImpl extends TransactionService implements TccTransactionService {

    private static final Logger log = Logger.getLogger(TccTransactionServiceImpl.class);

    @Resource
    private ProcessDao processDao;

    @Override
    @Transactional
    public YimqWrapResponse runTry(ProcessesEntity processesEntity) {
        Object result = null;
        try {
            yimqCommonUtils.checkType(YimqConstants.TCC,processesEntity.getType());
            this.saveProcessRecord(SubTaskStatusConstants.PREPARING);
            this.setAndLockProcessModel(processesEntity.getId());
            result = this.prepare();
            this.getProcessesEntity().setTryResult((JSONObject) JSONObject.toJSON(result));
            this.saveProcessRecord(SubTaskStatusConstants.PREPARED);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS,result);

    }

    @Override
    @Transactional
    public YimqWrapResponse runConfirm(ProcessesEntity processes) {

        Object result = null;
        try {
            ProcessesEntity processesEntity = this.setAndLockProcessModel(processes.getId());
            //判断状态
            if (processesEntity.getStatus() == SubTaskStatusConstants.DONE) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
            }
            // 判断状态
            if (processesEntity.getStatus() != SubTaskStatusConstants.PREPARED) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                String status = yimqCommonUtils.getStatusMap().get(processesEntity.getStatus());
                log.error(" tcc transaction handler happen exception. status is "+status);
                return new YimqWrapResponse(YimqResponseCodeConstants.FAIL," tcc transaction handler happen exception. status is "+status);
            }
            result = this.prepare();
            processesEntity.setStatus(SubTaskStatusConstants.DONE);
            processDao.saveOrUpdateProcess(processesEntity);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS,result);
    }

    @Override
    @Transactional
    public YimqWrapResponse runCancel(ProcessesEntity processes) {
        Object result = null;
        try {
            ProcessesEntity processesEntity = this.setAndLockProcessModel(processes.getId());
            if (processesEntity.getStatus() == SubTaskStatusConstants.CANCELED) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
            }
            //判断事务逻辑是否已经执行
            if (processesEntity.getStatus() != SubTaskStatusConstants.PREPARED) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                String status = yimqCommonUtils.getStatusMap().get(processesEntity.getStatus());
                log.error(" tcc transaction handler happen exception. status is "+status);
                return new YimqWrapResponse(YimqResponseCodeConstants.FAIL," tcc transaction handler happen exception. status is "+status);
            }
            result = this.prepare();
            processesEntity.setStatus(SubTaskStatusConstants.CANCELED);
            processDao.saveOrUpdateProcess(processesEntity);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS,result);
    }

    @Override
    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.prepare();
    }

}
