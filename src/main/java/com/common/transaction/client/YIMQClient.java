package com.common.transaction.client;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.message.TransactionYimqMessage;
import com.common.transaction.subTask.EcSubTask;
import com.common.transaction.subTask.TccSubTask;
import com.common.transaction.utils.YimqCommonUtils;
import com.common.transaction.utils.YIMQManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.transaction.TransactionException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 15:25
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class YIMQClient {

    private static final Logger log = Logger.getLogger(YIMQClient.class);

    @Resource
    private YIMQManager manager;
    @Value("${yimq.service.uri}")
    private String uri;

    private String serviceName;
    @Resource
    private YimqCommonUtils yimqCommonUtils;

    public YIMQClient(){

    }
    public YIMQClient(YIMQManager manager,String serviceName) {
        this.manager = manager;
        this.serviceName = serviceName;
    }

    public TransactionYimqMessage topic(String topic) {
        return new TransactionYimqMessage(this,topic);
    }

    public TccSubTask tcc(String processor) throws Exception {
        if (hasTransactionMessage()) {
            throw new Exception(" Not begin a yimq transaction ");
        }
        return new TccSubTask(this,this.getTransactionMessage(),processor);
    }

    public EcSubTask ec(String processor) throws Exception {
        if (!hasTransactionMessage()) {
            throw new Exception(" Not begin a yimq transaction ");
        }
        //return new EcSubTask(this,this.getTransactionMessage(),processor);
        return null;
    }

    public void setTransactionMessage(TransactionYimqMessage transactionMessage) {
        this.manager.transactionMessage = transactionMessage;
    }

    public boolean hasTransactionMessage(){
        if (null == this.manager.transactionMessage) return false;
        return true;
    }

    public TransactionYimqMessage getTransactionMessage(){
        return this.manager.transactionMessage;
    }


    public JSONObject callServer(String action, Map<String,Object> context) {
        context.put("actor",this.manager.actorName);
        String url = uri+ YimqCommonUtils.getActionMap().get(action);
        JSONObject result = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            StringEntity postingString = new StringEntity(new JSONObject(context).toString());
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
            String content = EntityUtils.toString(response.getEntity());
            result = (JSONObject) JSONObject.parse(content);
        }catch (RuntimeException | IOException e) {
            e.printStackTrace();
            throw new TransactionException("call server happen Exception");
        }
    return result;
    }

}
