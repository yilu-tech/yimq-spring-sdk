package com.common.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.ProcessesStatusConstants;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.utils.YimqCommonUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

/**
 * create by gtd ON 2020/3/24 17:22
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
    @Transactional(propagation = Propagation.NOT_SUPPORTED,rollbackFor = Exception.class)
    public synchronized Object runTry(ProcessesEntity processesEntity,String action) {
        yimqCommonUtils.checkType(YimqConstants.TCC,processesEntity.getType());
        processesEntity.setStatus(ProcessesStatusConstants.PREPARING);
        try{
            insertProcessRecord(processesEntity); //insert完成  后面的报错不会回滚，除非有事务。log-info.log 日志 说明了被回滚了。“process不存在,默认任务未创建，返回成功”
        }catch (DuplicateKeyException de){ //第一个try 超时10s 会触发此异常
            log.error("TCC事务try请求重复,processId:" + processesEntity.getId());
            return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS,YimqResponseMessageConstants.SUCCESS);
        }catch (Exception e){//意外如数据库中断
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL,"TCC try 发生异常,processId : " + processesEntity.getId());
        }
        return handleServiceCode(processesEntity,action);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public synchronized Object handleServiceCode(ProcessesEntity processesEntity, String action) {
        Object result = null;
        try {
            this.setAndLockProcessModel(processesEntity.getId()); //锁在事务中生效
            result = this.prepare(processesEntity,action); //执行业务代码 再开启新事务===10s
            if (null == result) {
                throw new RuntimeException("TCC异步事务TRY执行时发生异常正确，事务回滚");
            }
            JSONObject data = ((JSONObject) JSONObject.toJSON(result)).getJSONObject("data");
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                log.error("在TCC事务try时prepare异常,messageId:"+processesEntity.getMessage_id());
                return result;
            }
            processesEntity.setTryResult(data);
            processesEntity.setStatus(ProcessesStatusConstants.PREPARED);
            updateProcess(processesEntity);
        } catch (Exception e) {
            log.error("在TCC事务try时发生异常,messageId:"+processesEntity.getMessage_id());
        }
        return result;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized Object runConfirm(ProcessesEntity processesEntity,String action) {
        Object result = null;
        processesEntity = this.setAndLockProcessModel(processesEntity.getId());//confirm 可以等待锁
        //判断状态
        if (processesEntity.getStatus() == ProcessesStatusConstants.DONE) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
        }
        // 判断状态
        if (processesEntity.getStatus() != ProcessesStatusConstants.PREPARED) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String status = YimqCommonUtils.getStatusMap().get(processesEntity.getStatus());
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
            processesEntity.setStatus(ProcessesStatusConstants.DONE);
            processDao.updateProcess(processesEntity);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("在TCC事务confirm时发生异常,messageId:"+processesEntity.getMessage_id());
        }
        return result;
    }

    /**
     * 方法未被synchronized 修饰。 只会在 AndLock 处等待。只要有一个占用，就会影响当前容器（原资金服务，一部分请求被拒绝的原因）|| 路由到另一个服务,或者报错，才会解锁。
     * @param processesEntity
     * @param action
     * @return
     */
    @Override
    @Transactional
    public synchronized Object runCancel(ProcessesEntity processesEntity,String action) {
        Object result = null;
        try {
            processesEntity = this.setAndLockProcessModelSkipLocked(processesEntity.getId());
            if (null == processesEntity) {
                log.info("process不存在,默认任务未创建，返回成功"); //try 事务only-rollback 才会查不到，否则cancel不会无故发来
                return new YimqWrapResponse(YimqResponseCodeConstants.FAIL,YimqResponseMessageConstants.DATA_QUERY_EXCEPTION);
            }
            if (processesEntity.getStatus() == ProcessesStatusConstants.CANCELED ) {
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
            }
            //判断事务逻辑是否已经执行 现在cancel只会执行一次,必须回滚业务
            if (processesEntity.getStatus() == ProcessesStatusConstants.PREPARING) { //只要不是10就得回滚
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS,"tcc try 未执行成功,不用回滚");
            }
            result = this.prepare(processesEntity,action);
            if (null == result) {
                throw new RuntimeException("取消操作执行过程中发生异常，事务回滚");
            }
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                throw new RuntimeException("取消操作返回状态不正确，事务回滚");
            }
            processesEntity.setStatus(ProcessesStatusConstants.CANCELED);
            processDao.updateProcess(processesEntity);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }


    /*@Override
    public Object runTry(ProcessesEntity processesEntity,String action) {
        Object result = null;
        yimqCommonUtils.checkType(YimqConstants.TCC,processesEntity.getType());
        processesEntity.setStatus(ProcessesStatusConstants.PREPARING);
        this.insertProcessRecord(processesEntity);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

        try {
            //开启事务
            this.setAndLockProcessModel(processesEntity.getId());
            result = this.prepare(processesEntity,action);
            if (null == result) {
                //dataSourceTransactionManager.rollback(transactionStatus);
                throw new RuntimeException("TCC异步事务TRY执行时发生异常正确，事务回滚");
            }
            JSONObject data = ((JSONObject) JSONObject.toJSON(result)).getJSONObject("data");
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                //dataSourceTransactionManager.rollback(transactionStatus);
                throw new RuntimeException("TCC异步事务TRY执行时数据返回状态不正确，事务回滚");
            }
            processesEntity.setTryResult(data);
            processesEntity.setStatus(ProcessesStatusConstants.PREPARED);
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
        if (processesEntity.getStatus() == ProcessesStatusConstants.DONE) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
        }
        // 判断状态
        if (processesEntity.getStatus() != ProcessesStatusConstants.PREPARED) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            String status = YimqCommonUtils.getStatusMap().get(processesEntity.getStatus());
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
            processesEntity.setStatus(ProcessesStatusConstants.DONE);
            processDao.updateProcess(processesEntity);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("在TCC事务confirm时发生异常,messageId:"+processesEntity.getMessage_id());
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
            if (processesEntity.getStatus() == ProcessesStatusConstants.CANCELED) {
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
            }
            //判断事务逻辑是否已经执行
            if (processesEntity.getStatus() != ProcessesStatusConstants.PREPARED) {
                String status = YimqCommonUtils.getStatusMap().get(processesEntity.getStatus());
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
            processesEntity.setStatus(ProcessesStatusConstants.CANCELED);
            processDao.updateProcess(processesEntity);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }*/

    @Override
    public Object prepare(ProcessesEntity processesEntity,String action) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return super.prepare(processesEntity, action);
    }

}
