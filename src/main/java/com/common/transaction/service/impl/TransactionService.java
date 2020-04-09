package com.common.transaction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.config.TransactionClassConfig;
import com.common.transaction.constants.*;
import com.common.transaction.dao.MessageDao;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.exception.MyTransactionException;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.utils.ClassLoadByBeanNameUtils;
import com.common.transaction.utils.YimqCommonUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/24 17:57
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public abstract class TransactionService {

    private static final Logger log = Logger.getLogger(TransactionService.class);

    protected ProcessesEntity processesEntity;

    protected JSONObject data;

    private String action;

    public void setAction(String action) {
        this.action = action;
    }

    public TransactionService () {

    }

    @Resource
    private ProcessDao processDao;

    @Resource
    protected YimqCommonUtils yimqCommonUtils;

    @Resource
    private TransactionClassConfig transactionClassConfig;

    @Resource
    private MessageDao messageDao;

    @Resource
    private ClassLoadByBeanNameUtils classLoadByBeanNameUtils;

    public ProcessesEntity getProcessesEntity() {
        return processesEntity;
    }

    public void setProcessesEntity(ProcessesEntity processesEntity) {
        this.processesEntity = processesEntity;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    protected void saveProcessRecord(Integer transactionStatus){
        processesEntity.setStatus(transactionStatus);
        processDao.saveOrUpdateProcess(processesEntity);
    }

    protected ProcessesEntity setAndLockProcessModel(Integer processId){
        return processDao.selectProcessByIdForUpdate(processId);
    }

    public abstract YimqWrapResponse runTry(ProcessesEntity processesEntity);

    public abstract YimqWrapResponse runConfirm(ProcessesEntity processesEntity);

    public abstract YimqWrapResponse runCancel(ProcessesEntity processesEntity);

    public YimqWrapResponse runMessageCheck(ProcessesEntity processesEntity) {
        int messageId = processesEntity.getMessage_id();
        MessageEntity messageEntity = messageDao.selectMessageByIdForUpdate(messageId);
        YimqWrapResponse yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);;
        if (null == messageEntity) {
            throw new MyTransactionException( "message not exists .");
        }
        //对消息是否被消费 状态进行判断
        if (messageEntity.getStatus() == MessageStatusConstants.DONE) {
            yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED);
        }
        //对消息是否被处理 状态进行判断
        if (messageEntity.getStatus() == MessageStatusConstants.CANCELED) {
            yimqWrapResponse =  new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CANCELED);
        }
        //如果locKForUpdate能拿到的message且处于PENGDING状态，说明本地回滚后设置 message状态失败，check的时候补偿状态
        if (messageEntity.getStatus() == MessageStatusConstants.PENDING) {
            messageEntity.setStatus(MessageStatusConstants.CANCELED);
            messageDao.saveOrUpdateMessage(messageEntity);
            yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CANCELED);
        }

        return yimqWrapResponse;
    }

    public Object prepare() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String processor =  this.processesEntity.getProcessor();
        String[] processorArr = processor.split("@");
        String methodName = processorArr[1].split("\\.")[1];
        String methodType = this.processesEntity.getType();
        String prefix = null;
        if (methodType.equals(YimqConstants.EC)) {     //public static final Integer EC_TYPE = 10;       // EC事务类型码

        }else if (methodType.equals(YimqConstants.TCC)) {       //TCC事务类型码
            if (action.equals(YimqConstants.TRY)) {
                prefix = "try";
            }else if (action.equals(YimqConstants.CONFIRM)) {
                prefix = "confirm";
            }else if (action.equals(YimqConstants.CANCEL)) {
                prefix = "cancel";
            }
        }else if (methodType.equals(YimqConstants.XA)) {//XA事务类型码
            if (action.equals(YimqConstants.TRY)) {
                prefix = "try";
            }else if (action.equals(YimqConstants.CONFIRM)) {
                prefix = "confirm";
            }else if (action.equals(YimqConstants.CANCEL)) {
                prefix = "cancel";
            }
        }
        if (null != prefix) {
            methodName = prefix+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
        }
        String beanName = transactionClassConfig.getMaps().get(processorArr[1].split("\\.")[0]);            //获取注解名称
        TransactionService transactionService = classLoadByBeanNameUtils.getBean(beanName,TransactionService.class);
        transactionService.setData(this.data);
        transactionService.setProcessesEntity(processesEntity);
        Method method = transactionService.getClass().getMethod(methodName,Map.class);
        return method.invoke(transactionService,JSONObject.toJavaObject(this.data, Map.class));
    }

    public ProcessesEntity selectSubTaskById (Integer taskId){
        return processDao.selectProcessById(taskId);
    }

    public int saveOrUpdateSubTask(ProcessesEntity processesEntity) {
        return processDao.saveOrUpdateProcess(processesEntity);
    }

}
