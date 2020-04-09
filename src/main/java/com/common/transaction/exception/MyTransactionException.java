package com.common.transaction.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * create by gaotiedun ON 2020/3/24 17:38
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class MyTransactionException extends RuntimeException {
    private static final long serialVersionUID = 1882083796116806757L;

    protected int code = 500;
    protected HttpServletResponse response;

    public MyTransactionException() {

    }

    public MyTransactionException(String message) {
      /*  Map<String,Object> data = new HashMap<>();
        data.put("message",message);
        data.put("data",data);
        data.put("stack",super.getStackTrace());
        */
        super(message);
    }

    public MyTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
