package com.yimq.transaction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimq.transaction.constants.Constants;
import com.yimq.transaction.constants.MessageStatusConstants;
import com.yimq.transaction.constants.ResponseCodeConstants;
import com.yimq.transaction.dao.MessageDao;
import com.yimq.transaction.entity.ProcessesEntity;
import com.yimq.transaction.exception.MyTransactionException;
import com.yimq.transaction.config.TransactionClassConfig;
import com.yimq.transaction.constants.ResponseMessageConstants;
import com.yimq.transaction.dao.ProcessDao;
import com.yimq.transaction.entity.YimqContextEntity;
import com.yimq.transaction.entity.MessageEntity;
import com.yimq.transaction.http.WrapResponse;
import com.yimq.transaction.utils.ClassLoadByBeanNameUtils;
import com.yimq.transaction.utils.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * create by gaotiedun ON 2020/3/24 17:57
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public abstract class TransactionService {

    private static final Logger log = Logger.getLogger(TransactionService.class);

    protected ProcessesEntity processesEntity;
    protected JSONObject data;
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public TransactionService () {

    }

    @Resource
    private ProcessDao processDao;

    @Resource
    private CommonUtils commonUtils;

    @Resource
    private TransactionClassConfig transactionClassConfig;

    @Resource
    private MessageDao messageDao;

    @Resource
    private ClassLoadByBeanNameUtils classLoadByBeanNameUtils;

    public ProcessesEntity getProcessesEntity() {
        return processesEntity;
    }

    public void setProcessesEntity(YimqContextEntity yimqContextEntity) {
        this.processesEntity = new ProcessesEntity();
        processesEntity.setId(yimqContextEntity.getId());
        processesEntity.setMessageId(yimqContextEntity.getMessage_id());
        processesEntity.setType(commonUtils.TransactionTypeCode(yimqContextEntity.getType()));
        processesEntity.setProcessor(yimqContextEntity.getProcessor());
        processesEntity.setData(yimqContextEntity.getData().toJSONString());
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

    public abstract WrapResponse runTry(YimqContextEntity yimqContextEntity);

    public abstract WrapResponse runConfirm(YimqContextEntity yimqContextEntity);

    public abstract WrapResponse runCancel(YimqContextEntity yimqContextEntity);

    public WrapResponse runMessageCheck(YimqContextEntity yimqContextEntity) {
        int messageId = yimqContextEntity.getMessage_id();
        MessageEntity messageEntity = messageDao.selectMessageByIdForUpdate(messageId);
        WrapResponse wrapResponse = new WrapResponse(ResponseCodeConstants.FAIL, ResponseMessageConstants.EXCEPTION);;
        if (null == messageEntity) {
            throw new MyTransactionException( "message not exists .");
        }
        //对消息是否被消费 状态进行判断
        if (messageEntity.getStatus() == MessageStatusConstants.DONE) {
            wrapResponse = new WrapResponse(ResponseCodeConstants.SUCCESS, ResponseMessageConstants.MESSAGE_IS_CONSUMED);
        }
        //对消息是否被处理 状态进行判断
        if (messageEntity.getStatus() == MessageStatusConstants.CANCELED) {
            wrapResponse =  new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.MESSAGE_IS_CANCELED);
        }
        //如果locKForUpdate能拿到的message且处于PENGDING状态，说明本地回滚后设置 message状态失败，check的时候补偿状态
        if (messageEntity.getStatus() == MessageStatusConstants.PENDING) {
            messageEntity.setStatus(MessageStatusConstants.CANCELED);
            messageDao.saveOrUpdateMessage(messageEntity);
            wrapResponse = new WrapResponse(ResponseCodeConstants.SUCCESS,ResponseMessageConstants.MESSAGE_IS_CANCELED);
        }

        return wrapResponse;
    }

    public Object prepare() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String processor =  this.processesEntity.getProcessor();
        String[] processorArr = processor.split("@");
        String methodName = processorArr[1].split("\\.")[1];
        int methodType = this.processesEntity.getType();
        String prefix = null;
        if (methodType == 10) {     //public static final Integer EC_TYPE = 10;       // EC事务类型码

        }else if (methodType == 20) {       //TCC事务类型码
            if (action.equals(Constants.TRY)) {
                prefix = "try";
            }else if (action.equals(Constants.CONFIRM)) {
                prefix = "confirm";
            }else if (action.equals(Constants.CANCEL)) {
                prefix = "confirm";
            }
        }else if (methodType == 30) {//XA事务类型码
            if (action.equals(Constants.TRY)) {
                prefix = "try";
            }else if (action.equals(Constants.CONFIRM)) {
                prefix = "confirm";
            }
        }
        if (null != prefix) {
            methodName = prefix+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
        }
        String beanName = transactionClassConfig.getMaps().get(processorArr[1].split("\\.")[0]);            //获取注解名称
        TransactionService transactionService = classLoadByBeanNameUtils.getBean(beanName,TransactionService.class);
        transactionService.setData(this.data);
        transactionService.processesEntity = this.processesEntity;
        Method method = transactionService.getClass().getMethod(methodName);
        return method.invoke(transactionService);
    }

    public ProcessesEntity selectSubTaskById (Integer taskId){
        return processDao.selectProcessById(taskId);
    }

    public int saveOrUpdateSubTask(ProcessesEntity processesEntity) {
        return processDao.saveOrUpdateProcess(processesEntity);
    }

}
