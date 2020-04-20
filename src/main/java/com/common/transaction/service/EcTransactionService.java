package com.common.transaction.service;

import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * create by gaotiedun ON 2020/3/30 20:52
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Service("ecTransactionService")
public class EcTransactionService extends TransactionService {
    private static final Logger log = Logger.getLogger(EcTransactionService.class);

    @Override
    public YimqWrapResponse runTry(ProcessesEntity processesEntity) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YimqWrapResponse runConfirm(ProcessesEntity processesEntity) {
        //获取Process
        ProcessesEntity subTaskProcess = this.selectSubTaskById(processesEntity.getId());
        //对子任务状态进行判断
        if (null != subTaskProcess && subTaskProcess.getStatus().intValue() == SubTaskStatusConstants.DONE) {
            log.info(" this tcc transaction is done ");
            return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS);
        }
        //如果不存在，则创建
        if (null == subTaskProcess) {
            this.saveProcessRecord(SubTaskStatusConstants.DOING);
        }
        Object result;
        //查询并锁住记录
        subTaskProcess = this.setAndLockProcessModel(processesEntity.getId());
        try {
            result = this.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("执行EC事务确认操作时发生异常，messageId:"+processesEntity.getMessage_id());
        }
        subTaskProcess.setStatus(SubTaskStatusConstants.DONE);
        this.saveOrUpdateSubTask(subTaskProcess);
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS, result);
    }

    @Override
    public YimqWrapResponse runCancel(ProcessesEntity processesEntity) {
        return null;
    }

}
