package com.yimq.transaction.service;

import com.yimq.transaction.entity.YimqContextEntity;

import java.lang.reflect.InvocationTargetException;

/**
 * create by gaotiedun ON 2020/3/24 17:20
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface XaTransactionService {

    public Object runTry(YimqContextEntity context);

    public Object runConfirm(YimqContextEntity context);

    public Object runCancel(YimqContextEntity context);

    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

}
