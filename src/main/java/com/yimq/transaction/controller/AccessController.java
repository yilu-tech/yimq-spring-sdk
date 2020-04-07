package com.yimq.transaction.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimq.transaction.constants.Constants;
import com.yimq.transaction.constants.ResponseCodeConstants;
import com.yimq.transaction.constants.ResponseMessageConstants;
import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.http.WrapResponse;
import com.yimq.transaction.service.impl.TransactionService;
import com.yimq.transaction.factory.TransactionFactory;
import com.yimq.transaction.utils.RequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/25 18:12
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@RestController
@RequestMapping("/transaction/access")
public class AccessController {

    @Resource
    private TransactionFactory transactionFactory;

    @RequestMapping("/run")
    @ResponseBody
    public WrapResponse run(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> paramsMap = RequestUtils.getParameterMap(request);
        String action = paramsMap.get("action").toString();
        YimqContextEntity yimqContextEntity = ((JSONObject) paramsMap.get("context")).toJavaObject(YimqContextEntity.class);
        TransactionService transactionService = transactionFactory.createProcessService(yimqContextEntity);
        transactionService.setAction(action);
        WrapResponse wrapResponse ;
        switch (action){
            case Constants.TRY:
                wrapResponse = transactionService.runTry(yimqContextEntity);
                break;
            case Constants.CONFIRM:
                wrapResponse = transactionService.runConfirm(yimqContextEntity);
                break;
            case Constants.CANCEL:
                wrapResponse = transactionService.runCancel(yimqContextEntity);
                break;
            case Constants.MESSAGE_CHECK:
                wrapResponse = transactionService.runMessageCheck(yimqContextEntity);
                break;
            default:
                wrapResponse = new WrapResponse(ResponseCodeConstants.FAIL, ResponseMessageConstants.EXCEPTION);
                throw new Exception(" Action "+action+" not exist .");
        }
        if (wrapResponse.getCode() != 0) {
            response.setStatus(400);
        }
        return wrapResponse;
    }

}
