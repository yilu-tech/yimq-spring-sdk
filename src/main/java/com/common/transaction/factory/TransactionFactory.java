package com.common.transaction.factory;

import com.common.transaction.constants.YimqConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.service.*;
import org.springframework.stereotype.Component;

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

    public TransactionService createProcessService(ProcessesEntity processesEntity, String action){
        TransactionService transactionService = null;
        String type = processesEntity.getType();
        if (action .equals(YimqConstants.ACTOR_CLEAR) || action.equals(YimqConstants.MESSAGE_CHECK)) {
            transactionService = bcstTransactionService;
        }else{
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
            }
        }
        return transactionService;
    }

}
