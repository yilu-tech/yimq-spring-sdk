package com.yimq.transaction.factory;

import com.yimq.transaction.constants.Constants;
import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.exception.MyTransactionException;
import com.yimq.transaction.service.EcTransactionService;
import com.yimq.transaction.service.XaTransactionService;
import com.yimq.transaction.service.impl.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * create by gaotiedun ON 2020/3/26 9:48
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class TransactionFactory {

    public TransactionService createProcessService(YimqContextEntity yimqContextEntity){
        TransactionService transactionService;
        String type = yimqContextEntity.getType();
        if (StringUtils.isEmpty(type) ) {
            throw new MyTransactionException(" this context type is null");
        }
        switch (type) {
            case Constants.XA:
                transactionService = new XaTransactionServiceImpl();
                break;
            case Constants.TCC:
                transactionService = new TccTransactionServiceImpl();
                break;
            case Constants.EC:
                transactionService = new EcTransactionServiceImpl();
                break;
            case Constants.BCST:
                transactionService = new BcstTransactionServiceImpl();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        transactionService.setProcessesEntity(yimqContextEntity);
        transactionService.setData(yimqContextEntity.getData());
        return transactionService;
    }

}
