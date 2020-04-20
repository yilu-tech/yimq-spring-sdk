package com.common.transaction.factory;

import com.common.transaction.constants.YimqConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.exception.MyTransactionException;
import com.common.transaction.service.*;
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
    private XaTransactionService xaTransactionService;

    @Resource
    private TccTransactionService tccTransactionService;

    @Resource
    private EcTransactionService ecTransactionService;

    @Resource
    private BcstTransactionService bcstTransactionService;

    public TransactionService createProcessService(ProcessesEntity processesEntity){
        TransactionService transactionService;
        String type = processesEntity.getType();
        if (StringUtils.isEmpty(type) ) {
            throw new MyTransactionException(" this context type is null");
        }
        switch (type) {
            case YimqConstants.XA:
                transactionService = xaTransactionService;
                break;
            case YimqConstants.TCC:
                transactionService = tccTransactionService;
                break;
            case YimqConstants.EC:
                transactionService = ecTransactionService;
                break;
            case YimqConstants.BCST:
                transactionService = bcstTransactionService;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        transactionService.setProcessesEntity(processesEntity);
        transactionService.setData(processesEntity.getData());
        return transactionService;
    }

}
