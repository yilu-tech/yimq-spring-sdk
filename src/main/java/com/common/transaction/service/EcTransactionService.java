package com.common.transaction.service;

import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;

/**
 * create by gaotiedun ON 2020/3/30 20:51
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface EcTransactionService {

    public YimqWrapResponse runConfirm(ProcessesEntity processesEntity);

}
