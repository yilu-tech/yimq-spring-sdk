package com.yimq.transaction.service;

import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.http.WrapResponse;

/**
 * create by gaotiedun ON 2020/3/30 20:51
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface EcTransactionService {

    public WrapResponse runConfirm(YimqContextEntity yimqContextEntity);

}
