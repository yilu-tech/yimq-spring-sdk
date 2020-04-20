package com.common.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.factory.TransactionFactory;
import com.common.transaction.utils.YimqCommonUtils;
import com.common.transaction.utils.YimqRequestUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/4/10 19:44
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Service
public class TransactionAccessService {

    @Resource
    private TransactionFactory transactionFactory;

    @Resource
    private YimqCommonUtils yimqCommonUtils;

    public Object run(HttpServletRequest request, HttpServletResponse response) {
        Object result = null;
        Map<String,Object> paramsMap = YimqRequestUtils.getParameterMap(request);
        String action = paramsMap.get("action").toString();
        if (action.equals(YimqConstants.GET_CONFIG)) {
            return yimqCommonUtils.runGetConfig();
        }
        for (Map.Entry entry:paramsMap.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        ProcessesEntity processesEntity = ((JSONObject) paramsMap.get("context")).toJavaObject(ProcessesEntity.class);
        processesEntity.setTypeCode(YimqCommonUtils.getTransactionTypeCode(processesEntity.getType()));
        TransactionService transactionService = transactionFactory.createProcessService(processesEntity);
        transactionService.setAction(action);
        try {
            switch (action){
                case YimqConstants.TRY:
                    result = transactionService.runTry(processesEntity);
                    break;
                case YimqConstants.CONFIRM:
                    result = transactionService.runConfirm(processesEntity);
                    break;
                case YimqConstants.CANCEL:
                    result = transactionService.runCancel(processesEntity);
                    break;
                case YimqConstants.MESSAGE_CHECK:
                    result = transactionService.runMessageCheck(processesEntity);
                    break;
                default:
                    throw new RuntimeException(" Action "+action+" not exist .");
            }
            if (((JSONObject) JSONObject.toJSON(result)).getInteger("code") != 0 ){
                response.setStatus(400);
            }
        }catch (Exception e){
            response.setStatus(400);
        }
        return result;
    }


}
