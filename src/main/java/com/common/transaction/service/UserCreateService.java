package com.common.transaction.service;

/**
 * create by gaotiedun ON 2020/3/25 17:14
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public interface UserCreateService {

    public Object prepare();

    public Object xaTryCreate();

    public Object tccTryCreate();

    public Object tccTryConfirm();

    public Object tccTryCancel();

    public Object ecTryUpdate();

    public Object bcstTryUpdateListener();


}
