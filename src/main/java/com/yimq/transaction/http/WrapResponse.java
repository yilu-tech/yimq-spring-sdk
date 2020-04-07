package com.yimq.transaction.http;

import com.yimq.transaction.constants.ResponseCodeConstants;
import com.yimq.transaction.constants.ResponseMessageConstants;
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
public class WrapResponse implements Serializable {

    private static final long serialVersionUID = -2365651283326236455L;

    private int code;                               //返回状态码
    private String message;                         //提示信息
    private Object data = new HashMap<>() ;         //返回数据结果集
    private int  prompt = 0;                        //是否在前端展示

    public WrapResponse() {
    }

    public WrapResponse(List<FieldError> errorList){
        this.code = ResponseCodeConstants.HTTP_PARAM_ERROR;
        this.message = ResponseMessageConstants.PARAMS_ERROR;
        this.prompt = 1;
        Map<String,Object> errorMap = new HashMap<>();
        for (FieldError error: errorList){
            errorMap.put(error.getField(),new String[]{error.getDefaultMessage()});
        }
        this.data = errorMap;
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

    public WrapResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public WrapResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public WrapResponse(int code, String message, Object data, int prompt) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.prompt = prompt;
    }
}
