package com.common.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
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
@Service("tccTransactionService")
@Transactional
public class TccTransactionService extends TransactionService {

    private static final Logger log = Logger.getLogger(TccTransactionService.class);

    @Resource
    private ProcessDao processDao;

    @Resource
    DataSourceTransactionManager dataSourceTransactionManager;

    @Resource
    TransactionDefinition transactionDefinition;


    @Override
    public Object runTry(ProcessesEntity processesEntity,String action) {
        Object result = null;
        yimqCommonUtils.checkType(YimqConstants.TCC,processesEntity.getType());
        processesEntity.setStatus(SubTaskStatusConstants.PREPARING);
        this.insertProcessRecord(processesEntity);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);;
        try {
            //开启事务
            this.setAndLockProcessModel(processesEntity.getId());
            result = this.prepare(processesEntity,action);
            if (null == result) {
                dataSourceTransactionManager.rollback(transactionStatus);
                throw new RuntimeException("TCC异步事务TRY执行时发生异常正确，事务回滚");
            }
            JSONObject data = ((JSONObject) JSONObject.toJSON(result)).getJSONObject("data");
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                dataSourceTransactionManager.rollback(transactionStatus);
                throw new RuntimeException("TCC异步事务TRY执行时数据返回状态不正确，事务回滚");
            }
            processesEntity.setTryResult(data);
            processesEntity.setStatus(SubTaskStatusConstants.PREPARED);
            this.updateProcess(processesEntity);
            dataSourceTransactionManager.commit(transactionStatus);
        }catch (Exception e) {
            dataSourceTransactionManager.rollback(transactionStatus);
            log.error("在TCC事务try时发生异常,messageId:"+processesEntity.getMessage_id());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object runConfirm(ProcessesEntity processesEntity,String action) {
        Object result = null;
        processesEntity = this.setAndLockProcessModel(processesEntity.getId());
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
        try {
            result = this.prepare(processesEntity,action);
            if (null == result) {
                throw new RuntimeException("确认操作执行过程中发生异常，事务回滚");
            }
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                throw new RuntimeException("确认操作返回状态不正确，事务回滚");
            }
            processesEntity.setStatus(SubTaskStatusConstants.DONE);
            processDao.updateProcess(processesEntity);
        } catch (Exception e) {
            log.error("在TCC事务confirm时发生异常,messageId:"+processesEntity.getMessage_id());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    @Override
    @Transactional
    public Object runCancel(ProcessesEntity processesEntity,String action) {
        Object result = null;
        try {
            processesEntity = this.setAndLockProcessModel(processesEntity.getId());
            if (null == processesEntity) {
                log.info("process不存在,默认任务未创建，返回成功");
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS,YimqResponseMessageConstants.SUCCESS);
            }
            if (processesEntity.getStatus() == SubTaskStatusConstants.CANCELED) {
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
            }
            //判断事务逻辑是否已经执行
            if (processesEntity.getStatus() != SubTaskStatusConstants.PREPARED) {
                String status = yimqCommonUtils.getStatusMap().get(processesEntity.getStatus());
                return new YimqWrapResponse(YimqResponseCodeConstants.FAIL," tcc transaction handler happen exception. status is "+status);
            }
            result = this.prepare(processesEntity,action);
            if (null == result) {
                throw new RuntimeException("取消操作执行过程中发生异常，事务回滚");
            }
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                throw new RuntimeException("取消操作返回状态不正确，事务回滚");
            }
            processesEntity.setStatus(SubTaskStatusConstants.CANCELED);
            processDao.updateProcess(processesEntity);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("执行TCC事务的cancel时发生异常！messageId:"+processesEntity.getMessage_id());
        }
        return result;
    }

    @Override
    public Object prepare(ProcessesEntity processesEntity,String action) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return super.prepare(processesEntity, action);
    }

}
