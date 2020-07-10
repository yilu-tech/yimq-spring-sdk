package com.common.transaction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.transaction.config.TransactionClassConfig;
import com.common.transaction.constants.*;
import com.common.transaction.dao.MessageDao;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.dao.SubTaskDao;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.exception.MyTransactionException;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.utils.ClassLoadByBeanNameUtils;
import com.common.transaction.utils.YimqCommonUtils;
import com.common.transaction.utils.YimqFrameDateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * create by gaotiedun ON 2020/3/24 17:57
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Service
public abstract class TransactionService {

    private static final Logger log = Logger.getLogger(TransactionService.class);

    //private String action;

   /* public void setAction(String action) {
        this.action = action;
    }*/

    private Integer processId;

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public TransactionService () {

    }

    @Resource
    private ProcessDao processDao;

    @Resource
    private SubTaskDao subTaskDao;

    @Resource
    protected YimqCommonUtils yimqCommonUtils;

    @Resource
    private TransactionClassConfig transactionClassConfig;

    @Resource
    private MessageDao messageDao;

    @Resource
    private ClassLoadByBeanNameUtils classLoadByBeanNameUtils;


    public void saveProcessRecord(ProcessesEntity processesEntity){
        processesEntity.setUpdateTime(YimqFrameDateUtils.currentFormatDate());
        processDao.updateProcess(processesEntity);
    }

    public void insertProcessRecord(ProcessesEntity processesEntity) {
        processesEntity.setCreateTime(YimqFrameDateUtils.currentFormatDate());
        processDao.insertProcess(processesEntity);
    }

    public ProcessesEntity setAndLockProcessModel(Integer processId){
        return processDao.selectProcessByIdForUpdate(processId);
    }

    public abstract Object runTry(ProcessesEntity processesEntity,String action) ;

    public abstract Object runConfirm(ProcessesEntity processesEntity,String action);

    public abstract Object runCancel(ProcessesEntity processesEntity,String action);

    @Transactional
    public Object runMessageCheck(ProcessesEntity processesEntity) {
        int messageId = processesEntity.getMessage_id();
        MessageEntity messageEntity = messageDao.selectMessageByIdForUpdate(messageId);
        YimqWrapResponse yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.FAIL, YimqResponseMessageConstants.EXCEPTION);;
        if (null == messageEntity) {
            throw new MyTransactionException( "message not exists .");
        }
        //对消息是否被消费 状态进行判断
        if (messageEntity.getStatus() == MessageStatusConstants.DONE) {
            yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CONSUMED,null,"DONE");
        }
        //对消息是否被处理 状态进行判断
        if (messageEntity.getStatus() == MessageStatusConstants.CANCELED) {
            yimqWrapResponse =  new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CANCELED,null,"CANCELED");
        }
        //如果locKForUpdate能拿到的message且处于PENGDING状态，说明本地回滚后设置 message状态失败，check的时候补偿状态
        if (messageEntity.getStatus() == MessageStatusConstants.PENDING) {
            messageEntity.setStatus(MessageStatusConstants.CANCELED);
            messageDao.saveOrUpdateMessage(messageEntity);
            yimqWrapResponse = new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS, YimqResponseMessageConstants.MESSAGE_IS_CANCELED,null,"CANCELED");
        }
        return yimqWrapResponse;
    }

    /**
     *  message清理接口
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Object runActorClear(ProcessesEntity processesEntity) {
        try {
            if (null != processesEntity.getMessageIds()) {
                List<Integer> messageIdList = JSONObject.parseArray(processesEntity.getMessageIds().toJSONString(), Integer.class);
                if (null != messageIdList && 0 != messageIdList.size()) {
                    clearMessage(messageIdList);
                }
            }
            if (null != processesEntity.getProcessIds()) {
                List<Integer> processIdList = JSONObject.parseArray(processesEntity.getProcessIds().toJSONString(), Integer.class);
                if (null != processIdList && 0 != processIdList.size()) {
                    clearProcess(processIdList);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new YimqWrapResponse(YimqResponseCodeConstants.SYSTEM_ERROR,e.getMessage());
        }
        return new YimqWrapResponse(YimqResponseCodeConstants.SUCCESS,"success");
    }

    protected boolean clearMessage(List<Integer> messageIdList) {
        for (Integer messageId:messageIdList) {
            MessageEntity messageEntity = messageDao.selectMessageByIdForUpdate(messageId);
            if (null != messageEntity) {
                if (messageEntity.getStatus() == MessageStatusConstants.DONE
                        || messageEntity.getStatus() == MessageStatusConstants.CANCELED) {
                    messageDao.deleteMessageByMessageId(messageId);
                    subTaskDao.deleteSubTaskByMessageId(messageId);
                }else{
                    log.error(" yimqMessage messageClear happened exception , messageId : "+messageId);
                    throw new RuntimeException(" yimqMessage messageClear happened exception , messageId : "+messageId);
                }
            }
        }
        return true;
    }

    protected boolean clearProcess(List<Integer> processIdList) {
        for (Integer processId:processIdList) {
            ProcessesEntity processesEntity = processDao.selectProcessByIdForUpdate(processId);
            if (null != processesEntity) {
                if (processesEntity.getStatus() == SubTaskStatusConstants.DONE
                        || processesEntity.getStatus() == SubTaskStatusConstants.CANCELED) {
                    processDao.deleteProcessById(processId);
                }else{
                    log.error(" yimqMessage processClear happened exception , processId : "+processId);
                   throw new RuntimeException(" yimqMessage processClear happened exception , processId : "+processId);
                }
            }
        }
        return true;
    }

    public Object prepare(ProcessesEntity processesEntity,String action) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String processorClass =  processesEntity.getProcessor();
        String beanNameAndMethodName = transactionClassConfig.getMaps().get(processorClass);
        String beanName = beanNameAndMethodName.split("\\.")[0];
        int methodTypeCode = processesEntity.getTypeCode();
        String methodName = beanNameAndMethodName.split("\\.")[1];
        Object transactionService = null;
        transactionService = classLoadByBeanNameUtils.getBean(beanName, Object.class);
        if (methodTypeCode == YimqConstants.EC_TYPE) {     //public static final Integer EC_TYPE = 10;       // EC事务类型码
            //EC事务 事务比较多，方法名获取配置通过反射调用
        }else if (methodTypeCode == YimqConstants.TCC_TYPE) {       //TCC事务类型码
            //如果是TCC类型，需要手动为实体类的processesEntity 赋值
            Method setProcessesEntityMethod = null;
            setProcessesEntityMethod = transactionService.getClass().getMethod("setProcessId", Integer.class);
            setProcessesEntityMethod.invoke(transactionService, processesEntity.getId());
            if (action.equals(YimqConstants.TRY)) {
                methodName = "try" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            } else if (action.equals(YimqConstants.CONFIRM)) {
                methodName = "confirm" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            } else if (action.equals(YimqConstants.CANCEL)) {
                methodName = "cancel" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            }
        }else if (methodTypeCode == YimqConstants.XA_TYPE) {//XA事务类型码
            if (action.equals(YimqConstants.TRY)) {

            } else if (action.equals(YimqConstants.CONFIRM)) {

            } else if (action.equals(YimqConstants.CANCEL)) {

            }
        }else{
            throw new RuntimeException("the transaction type undefined");
        }
        Method[] methodArr = transactionService.getClass().getMethods();
        Method method = null;
        Class paramType = null;
        for (int i = 0; i < methodArr.length; i++) {
            if (methodArr[i].getName().equals(methodName)) {
                method = methodArr[i];
                Class[] paramTypes = methodArr[i].getParameterTypes();
                if (paramTypes.length > 1) {
                    throw new RuntimeException("异步事务方法只能接受一个参数");
                }
                paramType = paramTypes[0];
                break;
            }
        }
        if (null == method) {
            throw new RuntimeException("未找到可执行的方法：" + methodName);
        }
        if (processesEntity.getData() instanceof JSONArray) {
            return method.invoke(transactionService, ((JSONArray) processesEntity.getData()).toJavaList(Object.class));
        } else {
            return method.invoke(transactionService, JSONObject.toJavaObject((JSONObject) processesEntity.getData(), paramType));
        }
    }
    public ProcessesEntity selectSubTaskById (Integer processId){
        return processDao.selectProcessById(processId);
    }

    public int updateProcess(ProcessesEntity processesEntity) {
        processesEntity.setUpdateTime(YimqFrameDateUtils.currentFormatDate());
        return processDao.updateProcess(processesEntity);
    }

}
