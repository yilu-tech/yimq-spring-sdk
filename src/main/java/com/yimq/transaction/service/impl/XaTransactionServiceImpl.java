package com.yimq.transaction.service.impl;

import com.yimq.transaction.constants.Constants;
import com.yimq.transaction.constants.ResponseCodeConstants;
import com.yimq.transaction.constants.ResponseMessageConstants;
import com.yimq.transaction.constants.SubTaskStatusConstants;
import com.yimq.transaction.entity.ProcessesEntity;
import com.yimq.transaction.http.WrapResponse;
import com.yimq.transaction.service.XaTransactionService;
import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.utils.CommonUtils;
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
@Component("xaTransactionService")
public  class XaTransactionServiceImpl extends TransactionService implements XaTransactionService {

    private static final Logger log = Logger.getLogger(XaTransactionServiceImpl.class);

    @Resource
    private CommonUtils commonUtils;
    @Resource
    private XAResource xaResource;

    @Override
    public WrapResponse runTry(YimqContextEntity context) {
        Object result = null;
        MysqlXid mysqlXid = commonUtils.getMysqlXid(context.getId().toString().getBytes());
        try {
            // 验证事务类型
            commonUtils.checkType(Constants.XA,context.getType());
            //本地记录记录subTask
            this.saveProcessRecord(SubTaskStatusConstants.PREPARING);
            //开启事务
            xaResource.start(mysqlXid,XAResource.TMNOFLAGS);
            //锁住process记录
            this.setAndLockProcessModel(context.getId());
            //执行业务操作
            Object obj = prepare();
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
            return new WrapResponse(ResponseCodeConstants.FAIL, ResponseMessageConstants.EXCEPTION);

        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS, ResponseMessageConstants.SUCCESS,result);
    }


    @Override
    public WrapResponse runConfirm(YimqContextEntity context) {
        MysqlXid mysqlXid = commonUtils.getMysqlXid(context.getId().toString().getBytes());
        try {
            xaResource.commit(mysqlXid,true);
        } catch (XAException e) {
            e.printStackTrace();
            ProcessesEntity processesEntity = this.setAndLockProcessModel(context.getId());
            if(processesEntity.getStatus()== SubTaskStatusConstants.DONE){
                return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS);
            }
            return new WrapResponse(ResponseCodeConstants.FAIL,ResponseMessageConstants.EXCEPTION);
        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS);
    }

    @Override
    public WrapResponse runCancel(YimqContextEntity context) {
        MysqlXid mysqlXid = commonUtils.getMysqlXid(context.getId().toString().getBytes());
        try {
            xaResource.rollback(mysqlXid);
        } catch (XAException e) {
            e.printStackTrace();
            log.info("xa transaction commit happens exception xaId:"+context.getId());
            return new WrapResponse(ResponseCodeConstants.FAIL,ResponseMessageConstants.EXCEPTION);
        }
        return new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.SUCCESS);
    }

    @Override
    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.prepare();
    }


}
