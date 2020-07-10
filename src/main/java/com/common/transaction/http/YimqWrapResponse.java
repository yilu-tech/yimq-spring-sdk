package com.common.transaction.http;

import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaotiedun
 * Created Date 2019/05/05 14:27
 * Updated Date      by
 * @version v2.0
 * Description  积分微币服务 返回实体类
 */
public class YimqWrapResponse implements Serializable {

    private static final long serialVersionUID = -2365651283326236455L;

    private int code;                               //返回状态码
    private String message;                         //提示信息
    private Object data = new HashMap<>() ;         //返回数据结果集
    private int  prompt = 0;                        //
    private String status;



    public YimqWrapResponse() {
    }

    public YimqWrapResponse(List<FieldError> errorList){
        this.code = YimqResponseCodeConstants.HTTP_PARAM_ERROR;
        this.message = YimqResponseMessageConstants.PARAMS_ERROR;
        this.prompt = 1;
        Map<String,Object> errorMap = new HashMap<>();
        for (FieldError error: errorList){
            errorMap.put(error.getField(),new String[]{error.getDefaultMessage()});
        }
        this.data = errorMap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getPrompt() {
        return prompt;
    }

    public void setPrompt(int prompt) {
        this.prompt = prompt;
    }

    public YimqWrapResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public YimqWrapResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public YimqWrapResponse(int code, String message, Object data,String status) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.status = status;
    }


    public YimqWrapResponse(int code, String message, Object data, int prompt) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.prompt = prompt;
    }
}
