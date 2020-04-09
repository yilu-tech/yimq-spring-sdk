package com.common.transaction.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.factory.TransactionFactory;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.service.impl.TransactionService;
import com.common.transaction.utils.YimqCommonUtils;
import com.common.transaction.utils.YimqRequestUtils;
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
    public YimqWrapResponse run(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> paramsMap = YimqRequestUtils.getParameterMap(request);
        String action = paramsMap.get("action").toString();
        ProcessesEntity processesEntity = ((JSONObject) paramsMap.get("context")).toJavaObject(ProcessesEntity.class);
        processesEntity.setTypeCode(YimqCommonUtils.getTransactionTypeCode(processesEntity.getType()));
        TransactionService transactionService = transactionFactory.createProcessService(processesEntity);
        transactionService.setAction(action);
        YimqWrapResponse yimqWrapResponse;
        switch (action){
            case YimqConstants.TRY:
                yimqWrapResponse = transactionService.runTry(processesEntity);
                break;
            case YimqConstants.CONFIRM:
                yimqWrapResponse = transactionService.runConfirm(processesEntity);
                break;
            case YimqConstants.CANCEL:
                yimqWrapResponse = transactionService.runCancel(processesEntity);
                break;
            case YimqConstants.MESSAGE_CHECK:
                yimqWrapResponse = transactionService.runMessageCheck(processesEntity);
                break;
            default:
                yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);
                throw new Exception(" Action "+action+" not exist .");
        }
        if (yimqWrapResponse.getCode() != 0) {
            response.setStatus(400);
        }
        return yimqWrapResponse;
    }

}
