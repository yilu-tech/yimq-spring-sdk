package com.common.transaction.service;

import com.common.transaction.entity.ProcessesEntity;

import java.lang.reflect.InvocationTargetException;

/**
 * create by gaotiedun ON 2020/3/24 17:20
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface XaTransactionService {

    public Object runTry(ProcessesEntity processesEntity);

    public Object runConfirm(ProcessesEntity processesEntity);

    public Object runCancel(ProcessesEntity processesEntity);

    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

}
