package com.yimq.transaction.utils;

import com.yimq.transaction.constants.Constants;
import com.yimq.transaction.constants.SubTaskStatusConstants;
import com.yimq.transaction.exception.MyTransactionException;
import com.mysql.cj.jdbc.MysqlXid;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/24 17:31
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class CommonUtils {

    private static Map<String,Integer> transactionTypeCodeMap= new HashMap<>();
    private static Map<Integer,String> statusMap = new HashMap<>();
    private static Map<String,String> actionMap = new HashMap<>();


    public CommonUtils(){
        transactionTypeCodeMap.put(Constants.XA,Constants.XA_TYPE);
        transactionTypeCodeMap.put(Constants.TCC,Constants.TCC_TYPE);
        transactionTypeCodeMap.put(Constants.EC,Constants.EC_TYPE);
        transactionTypeCodeMap.put(Constants.BCST, Constants.BCST_TYPE);
        //
        statusMap.put(SubTaskStatusConstants.PREPARING,"PREPARING");
        statusMap.put(SubTaskStatusConstants.PREPARED,"PREPARED");
        statusMap.put(SubTaskStatusConstants.DOING,"DOING");
        statusMap.put(SubTaskStatusConstants.DONE,"DONE");
        statusMap.put(SubTaskStatusConstants.CANCELLING,"CANCELLING");
        statusMap.put(SubTaskStatusConstants.CANCELED,"CANCELED");

        actionMap.put("create","/message/create");
        actionMap.put("subTask","/message/subTask");
        actionMap.put("prepare","/message/prepare");
        actionMap.put("confirm","/message/confirm");
        actionMap.put("cancel","/message/cancel");

    }

    public static Map<Integer, String> getStatusMap() {
        return statusMap;
    }


    public void checkType(String type,String subTaskType) {
        if (!StringUtils.isEmpty(type) && type.equals(subTaskType)) {
            return;
        }
        throw new MyTransactionException(type+" processor can not process  "+subTaskType+" subTask.");
    }

    public static Map<String,String> getActionMap(){return actionMap;}

    public MysqlXid getMysqlXid(byte[] globalId) {

        byte[] bqual = new byte[]{'A'};

        int formatId = 0;

        return new MysqlXid(globalId, bqual, formatId);
    }


    public Integer TransactionTypeCode(String transactionType){
        Integer result = transactionTypeCodeMap.get(transactionType);
        if (null == result || 0 == result) {
            throw new MyTransactionException("transaction type code is Unidentified");
        }
        return result;
    }

}
