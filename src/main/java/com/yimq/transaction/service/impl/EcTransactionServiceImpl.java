package com.yimq.transaction.service.impl;

import com.yimq.transaction.constants.ResponseCodeConstants;
import com.yimq.transaction.constants.ResponseMessageConstants;
import com.yimq.transaction.constants.SubTaskStatusConstants;
import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.entity.ProcessesEntity;
import com.yimq.transaction.http.WrapResponse;
import com.yimq.transaction.service.EcTransactionService;
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
@Component("ecTransactionService")
public class EcTransactionServiceImpl extends TransactionService implements EcTransactionService {
    private static final Logger log = Logger.getLogger(EcTransactionServiceImpl.class);

    @Override
    public WrapResponse runTry(YimqContextEntity yimqContextEntity) {
        return null;
    }

    @Override
    @Transactional
    public WrapResponse runConfirm(YimqContextEntity yimqContextEntity) {
        //获取Process
        ProcessesEntity subTaskProcess = this.selectSubTaskById(yimqContextEntity.getId());
        //对子任务状态进行判断
        if (null != subTaskProcess && subTaskProcess.getStatus() == SubTaskStatusConstants.DONE) {
            log.info(" this tcc transaction is done ");
            return new WrapResponse(ResponseCodeConstants.SUCCESS, ResponseMessageConstants.SUCCESS);
        }
        //如果不存在，则创建
        if (null == subTaskProcess) {
            this.saveProcessRecord(SubTaskStatusConstants.DOING);
        }
        Object result = null;
        try {
            //查询并锁住记录
            subTaskProcess = this.setAndLockProcessModel(yimqContextEntity.getId());
            result = this.prepare();
            subTaskProcess.setStatus(SubTaskStatusConstants.DONE);
            this.saveOrUpdateSubTask(subTaskProcess);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new WrapResponse(ResponseCodeConstants.FAIL,ResponseMessageConstants.EXCEPTION);
        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS, result);
    }

    @Override
    public WrapResponse runCancel(YimqContextEntity yimqContextEntity) {
        return null;
    }

}
