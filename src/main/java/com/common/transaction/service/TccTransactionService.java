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
import org.springframework.stereotype.Service;
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

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Object runTry(ProcessesEntity processesEntity) {
        Object result ;
        yimqCommonUtils.checkType(YimqConstants.TCC,processesEntity.getType());
        processesEntity.setStatus(SubTaskStatusConstants.PREPARING);
        this.saveProcessRecord(SubTaskStatusConstants.PREPARING);
        this.setAndLockProcessModel(processesEntity.getId());
        try {
            result = this.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据执行中发生异常");
        }
        JSONObject data  = ((JSONObject) JSONObject.toJSON(result)).getJSONObject("data");
        int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
        if (code != 0) {
            throw new RuntimeException("数据执行状态不正确，事务回滚");
        }
        this.getProcessesEntity().setTryResult(data);
        this.saveProcessRecord(SubTaskStatusConstants.PREPARED);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object runConfirm(ProcessesEntity processes) {
        Object result = new Object();
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
        try {
            result = this.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("在TCC事务确认时发生异常,messageId:"+processesEntity.getMessage_id());
        }
        int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
        if (code != 0) {
            throw new RuntimeException("确认操作返回状态不正确，事务回滚");
        }
        processesEntity.setStatus(SubTaskStatusConstants.DONE);
        processDao.saveOrUpdateProcess(processesEntity);
        return result;
    }

    @Override
    @Transactional
    public Object runCancel(ProcessesEntity processes) {
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
        }
        return result;
    }

    @Override
    public Object prepare()  throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        return super.prepare();
    }

}
