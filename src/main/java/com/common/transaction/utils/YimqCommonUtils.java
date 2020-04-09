package com.common.transaction.utils;

import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.SubTaskStatusConstants;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.exception.MyTransactionException;
import com.mysql.cj.jdbc.MysqlXid;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
public class YimqCommonUtils {

    @Resource
    private ProcessDao processDao;

    private static Map<String,Integer> transactionTypeCodeMap= new HashMap<>();
    private static Map<Integer,String> statusMap = new HashMap<>();
    private static Map<String,String> actionMap = new HashMap<>();


    public YimqCommonUtils(){
        transactionTypeCodeMap.put(YimqConstants.XA, YimqConstants.XA_TYPE);
        transactionTypeCodeMap.put(YimqConstants.TCC, YimqConstants.TCC_TYPE);
        transactionTypeCodeMap.put(YimqConstants.EC, YimqConstants.EC_TYPE);
        transactionTypeCodeMap.put(YimqConstants.BCST, YimqConstants.BCST_TYPE);
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


    public static Integer getTransactionTypeCode(String transactionType){
        Integer result = transactionTypeCodeMap.get(transactionType);
        if (null == result || 0 == result) {
            throw new MyTransactionException("transaction type code is Unidentified");
        }
        return result;
    }

    public ProcessesEntity getProcessById(Integer processId) {
        return processDao.selectProcessById(processId);
    }

}
