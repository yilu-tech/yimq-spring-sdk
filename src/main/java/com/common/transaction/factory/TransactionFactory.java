package com.common.transaction.factory;

import com.common.transaction.constants.YimqConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.exception.MyTransactionException;
import com.common.transaction.service.impl.*;
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
    @Resource
    private XaTransactionServiceImpl xaTransactionServiceImpl;

    @Resource
    private TccTransactionServiceImpl tccTransactionServiceImpl;

    @Resource
    private EcTransactionServiceImpl ecTransactionServiceImpl;

    @Resource
    private BcstTransactionServiceImpl bcstTransactionServiceImpl;

    public TransactionService createProcessService(ProcessesEntity processesEntity){
        TransactionService transactionService;
        String type = processesEntity.getType();
        if (StringUtils.isEmpty(type) ) {
            throw new MyTransactionException(" this context type is null");
        }
        switch (type) {
            case YimqConstants.XA:
                transactionService = xaTransactionServiceImpl;
                break;
            case YimqConstants.TCC:
                transactionService = tccTransactionServiceImpl;
                break;
            case YimqConstants.EC:
                transactionService = ecTransactionServiceImpl;
                break;
            case YimqConstants.BCST:
                transactionService = bcstTransactionServiceImpl;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        transactionService.setProcessesEntity(processesEntity);
        transactionService.setData(processesEntity.getData());
        return transactionService;
    }

}
