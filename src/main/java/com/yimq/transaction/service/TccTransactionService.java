package com.yimq.transaction.service;

import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.http.WrapResponse;

import java.lang.reflect.InvocationTargetException;

/**
 * create by gaotiedun ON 2020/3/24 17:20
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface TccTransactionService {

    public WrapResponse runTry(YimqContextEntity context);

    public WrapResponse runConfirm(YimqContextEntity context);

    public WrapResponse runCancel(YimqContextEntity context);

    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

}
