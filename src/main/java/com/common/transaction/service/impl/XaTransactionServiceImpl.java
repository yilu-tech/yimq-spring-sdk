package com.common.transaction.service.impl;

import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.service.XaTransactionService;
import com.mysql.cj.jdbc.MysqlXid;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import java.lang.reflect.InvocationTargetException;

/**
 * create by gaotiedun ON 2020/3/24 17:22
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component("xaTransactionServiceImpl")
public  class XaTransactionServiceImpl extends TransactionService implements XaTransactionService {

    private static final Logger log = Logger.getLogger(XaTransactionServiceImpl.class);

    @Resource
    private XAResource xaResource;

    @Override
    public YimqWrapResponse runTry(ProcessesEntity processesEntity) {
        Object result = null;
        MysqlXid mysqlXid = yimqCommonUtils.getMysqlXid(processesEntity.getId().toString().getBytes());
        try {
            // 验证事务类型
            yimqCommonUtils.checkType(YimqConstants.XA,processesEntity.getType());
            //本地记录记录subTask
            this.saveProcessRecord(SubTaskStatusConstants.PREPARING);
            //开启事务
            xaResource.start(mysqlXid,XAResource.TMNOFLAGS);
            //锁住process记录
            this.setAndLockProcessModel(processesEntity.getId());
            //执行业务操作
            result = prepare();
            //更新process状态
            this.saveProcessRecord(SubTaskStatusConstants.DONE);
            //结束XA事务
            xaResource.end(mysqlXid,XAResource.TMSUCCESS);
            xaResource.prepare(mysqlXid);
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                xaResource.end(mysqlXid,XAResource.TMSUCCESS);
                xaResource.rollback(mysqlXid);
            } catch (XAException ex) {
                ex.printStackTrace();
            }
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION,result);

        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS,result);
    }


    @Override
    public YimqWrapResponse runConfirm(ProcessesEntity processes) {
        MysqlXid mysqlXid = yimqCommonUtils.getMysqlXid(processes.getId().toString().getBytes());
        try {
            xaResource.commit(mysqlXid,true);
        } catch (XAException e) {
            e.printStackTrace();
            ProcessesEntity processesEntity = this.setAndLockProcessModel(processes.getId());
            if(processesEntity.getStatus()== SubTaskStatusConstants.DONE){
                return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS);
            }
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS);
    }

    @Override
    public YimqWrapResponse runCancel(ProcessesEntity processesEntity) {
        MysqlXid mysqlXid = yimqCommonUtils.getMysqlXid(processesEntity.getId().toString().getBytes());
        try {
            xaResource.rollback(mysqlXid);
        } catch (XAException e) {
            e.printStackTrace();
            log.info("xa transaction commit happens exception xaId:"+processesEntity.getId());
            return new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.SUCCESS);
    }

    @Override
    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.prepare();
    }


}
