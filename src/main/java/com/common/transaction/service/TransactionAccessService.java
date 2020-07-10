package com.common.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.factory.TransactionFactory;
import com.common.transaction.utils.YimqCommonUtils;
import com.common.transaction.utils.YimqRequestUtils;
import org.apache.log4j.Logger;
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
    private static final Logger log = Logger.getLogger(TransactionAccessService.class);

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
        log.info(" Ec consumer context :"+paramsMap.get("context"));
        ProcessesEntity processesEntity = transferProcesses((JSONObject) paramsMap.get("context"),action);
        TransactionService transactionService = transactionFactory.createProcessService(processesEntity,action);
        try {
            switch (action){
                case YimqConstants.TRY:
                    result = transactionService.runTry(processesEntity,action);
                    break;
                case YimqConstants.CONFIRM:
                    result = transactionService.runConfirm(processesEntity,action);
                    break;
                case YimqConstants.CANCEL:
                    result = transactionService.runCancel(processesEntity,action);
                    break;
                case YimqConstants.MESSAGE_CHECK:
                    result = transactionService.runMessageCheck(processesEntity);
                    break;
                case YimqConstants.ACTOR_CLEAR:
                    result = transactionService.runActorClear(processesEntity);
                    break;
                default:
                    throw new RuntimeException(" Action "+action+" not exist .");
            }
            if (null == result || ((JSONObject) JSONObject.toJSON(result)).getInteger("code") != 0 ){
                response.setStatus(400);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("执行异步事务接收方业务时发生异常,异常信息:"+e);
            log.error("----------message_id:"+processesEntity.getMessage_id()+",请求的process:"+processesEntity.getProcessor()+"请求参数："+processesEntity.getData());
            response.setStatus(400);
        }
    return result;
    }

    public ProcessesEntity transferProcesses(JSONObject jsonObject,String action) {
        ProcessesEntity processesEntity = new ProcessesEntity();
        processesEntity.setId(jsonObject.getInteger("id"));
        processesEntity.setData(jsonObject.get("data"));
        processesEntity.setProcessor(jsonObject.getString("processor"));
        processesEntity.setType(jsonObject.getString("type"));
        processesEntity.setMessage_id(jsonObject.getInteger("message_id"));
        processesEntity.setMessageIds(jsonObject.getJSONArray("message_ids"));
        processesEntity.setProcessIds(jsonObject.getJSONArray("process_ids"));
        if (!action.equals(YimqConstants.MESSAGE_CHECK) && !action.equals(YimqConstants.ACTOR_CLEAR)) {
            processesEntity.setTypeCode(YimqCommonUtils.getTransactionTypeCode(processesEntity.getType()));
        }
         return processesEntity;
    }

}
