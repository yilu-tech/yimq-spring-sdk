package com.common.transaction.service.impl;

import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.service.EcTransactionService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


/**
 * create by gaotiedun ON 2020/3/30 20:52
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component("ecTransactionServiceImpl")
public class EcTransactionServiceImpl extends TransactionService implements EcTransactionService {
    private static final Logger log = Logger.getLogger(EcTransactionServiceImpl.class);

    @Override
    public YimqWrapResponse runTry(ProcessesEntity processesEntity) {
        return null;
    }

    @Override
    @Transactional
    public YimqWrapResponse runConfirm(ProcessesEntity processesEntity) {
        //获取Process
        ProcessesEntity subTaskProcess = this.selectSubTaskById(processesEntity.getId());
        //对子任务状态进行判断
        if (null != subTaskProcess && subTaskProcess.getStatus() == SubTaskStatusConstants.DONE) {
            log.info(" this tcc transaction is done ");
            return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS);
        }
        //如果不存在，则创建
        if (null == subTaskProcess) {
            this.saveProcessRecord(SubTaskStatusConstants.DOING);
        }
        Object result = null;
        try {
            //查询并锁住记录
            subTaskProcess = this.setAndLockProcessModel(processesEntity.getId());
            result = this.prepare();
            subTaskProcess.setStatus(SubTaskStatusConstants.DONE);
            this.saveOrUpdateSubTask(subTaskProcess);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS, result);
    }

    @Override
    public YimqWrapResponse runCancel(ProcessesEntity processesEntity) {
        return null;
    }

}
