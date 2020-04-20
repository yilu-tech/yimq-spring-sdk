package com.common.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.config.TransactionClassConfig;
import com.common.transaction.constants.MessageStatusConstants;
import com.common.transaction.constants.YimqConstants;
import com.common.transaction.constants.YimqResponseCodeConstants;
import com.common.transaction.constants.YimqResponseMessageConstants;
import com.common.transaction.dao.MessageDao;
import com.common.transaction.dao.ProcessDao;
import com.common.transaction.entity.MessageEntity;
import com.common.transaction.entity.ProcessesEntity;
import com.common.transaction.exception.MyTransactionException;
import com.common.transaction.http.YimqWrapResponse;
import com.common.transaction.utils.ClassLoadByBeanNameUtils;
import com.common.transaction.utils.YimqCommonUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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
@Service
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

    public void saveProcessRecord(Integer transactionStatus){
        processesEntity.setStatus(transactionStatus);
        processDao.saveOrUpdateProcess(processesEntity);
    }

    public ProcessesEntity setAndLockProcessModel(Integer processId){
        return processDao.selectProcessByIdForUpdate(processId);
    }

    public abstract Object runTry(ProcessesEntity processesEntity) ;

    public abstract Object runConfirm(ProcessesEntity processesEntity);

    public abstract Object runCancel(ProcessesEntity processesEntity);

    public Object runMessageCheck(ProcessesEntity processesEntity) {
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
        String beanNameAndMethodName = transactionClassConfig.getMaps().get(processor);
        String beanName = beanNameAndMethodName.split("\\.")[0];
        String methodType = this.processesEntity.getType();
        String methodName = beanNameAndMethodName.split("\\.")[1];
        TransactionService transactionService = null;
        if (methodType.equals(YimqConstants.EC)) {     //public static final Integer EC_TYPE = 10;       // EC事务类型码
            //EC事务 事务比较多，方法名获取配置通过反射调用
            transactionService = classLoadByBeanNameUtils.getBean(beanName,EcTransactionService.class);

        }else if (methodType.equals(YimqConstants.TCC)) {       //TCC事务类型码
            transactionService = classLoadByBeanNameUtils.getBean(beanName, TccTransactionService.class);
            if (action.equals(YimqConstants.TRY)) {
                methodName  = "try"+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
            }else if (action.equals(YimqConstants.CONFIRM)) {
                methodName  = "confirm"+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
            }else if (action.equals(YimqConstants.CANCEL)) {
                methodName  = "cancel"+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
            }
        }else if (methodType.equals(YimqConstants.XA)) {//XA事务类型码
            if (action.equals(YimqConstants.TRY)) {

            }else if (action.equals(YimqConstants.CONFIRM)) {

            }else if (action.equals(YimqConstants.CANCEL)) {

            }
        }
        if (null == transactionService) {
            throw new RuntimeException("未识别的事务类型");
        }
        Method[] methodArr = transactionService.getClass().getMethods();
        Method method = null;
        Class paramType = null;
        for (int i=0;i<methodArr.length;i++) {
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
            throw new RuntimeException("未找到可执行的方法："+methodName);
        }
        return method.invoke(transactionService,JSONObject.toJavaObject(this.data, paramType));
    }

    public ProcessesEntity selectSubTaskById (Integer taskId){
        return processDao.selectProcessById(taskId);
    }

    public int saveOrUpdateSubTask(ProcessesEntity processesEntity) {
        return processDao.saveOrUpdateProcess(processesEntity);
    }

}
