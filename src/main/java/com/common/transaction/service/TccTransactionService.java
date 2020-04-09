package com.common.transaction.service;

import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.http.YimqWrapResponse;

import java.lang.reflect.InvocationTargetException;

/**
 * create by gaotiedun ON 2020/3/24 17:20
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface TccTransactionService {

    public YimqWrapResponse runTry(ProcessesEntity processesEntity);

    public YimqWrapResponse runConfirm(ProcessesEntity processesEntity);

    public YimqWrapResponse runCancel(ProcessesEntity processesEntity);

    public Object prepare() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

}
