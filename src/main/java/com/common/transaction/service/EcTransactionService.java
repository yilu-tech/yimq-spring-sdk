package com.common.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;


/**
 * create by gaotiedun ON 2020/3/30 20:52
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Service
public class EcTransactionService extends TransactionService {
    private static final Logger log = Logger.getLogger(EcTransactionService.class);

    @Resource
    DataSourceTransactionManager dataSourceTransactionManager;

    @Resource
    TransactionDefinition transactionDefinition;


    @Override
    public YimqWrapResponse runTry(ProcessesEntity processesEntity,String action) {
        return null;
    }

    @Override
    public Object runConfirm(ProcessesEntity processesEntity,String action) {
        TransactionStatus transactionStatus ;
        Object result;
        //获取Process
        ProcessesEntity subTaskEntity = this.selectSubTaskById(processesEntity.getId());
        //对子任务状态进行判断
        if (null != subTaskEntity && subTaskEntity.getStatus().intValue() == SubTaskStatusConstants.DONE) {
            log.info(" this tcc transaction is done ");
            return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS);
        }
        //如果不存在，则创建
        if (null == subTaskEntity) {
            synchronized (EcTransactionService.class) {
                if (null == subTaskEntity) {
                    processesEntity.setStatus(SubTaskStatusConstants.DOING);
                    this.insertProcessRecord(processesEntity);
                }
            }
        }
        transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            //查询并锁住记录
            processesEntity = this.setAndLockProcessModel(processesEntity.getId());
            result = this.prepare(processesEntity,action);
            if (null == result) {
                throw new RuntimeException("数据执行时发生异常，事务回滚");
            }
            int code = ((JSONObject) JSONObject.toJSON(result)).getInteger("code");
            if (code != 0) {
                log.error("数据执行状态不正确，事务回滚");
                dataSourceTransactionManager.rollback(transactionStatus);
                return result;
            }
            processesEntity.setStatus(SubTaskStatusConstants.DONE);
            saveProcessRecord(processesEntity);
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            dataSourceTransactionManager.rollback(transactionStatus);
            log.error("EcTransactionService runConfirm exception:",e);
            return new YimqWrapResponse(YimqResponseCodeConstants.SYSTEM_ERROR,e.getMessage());
        }
        return  result;
    }

    @Override
    public YimqWrapResponse runCancel(ProcessesEntity processesEntity,String action) {
        return null;
    }

}
