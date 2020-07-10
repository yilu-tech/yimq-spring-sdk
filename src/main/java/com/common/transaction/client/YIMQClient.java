package com.common.transaction.client;

import com.alibaba.fastjson.JSONObject;
import com.common.transaction.message.YimqTransactionMessage;
import com.common.transaction.subTask.EcSubTask;
import com.common.transaction.subTask.TccSubTask;
import com.common.transaction.utils.YIMQManager;
import com.common.transaction.utils.YimqCommonUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.transaction.TransactionException;
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

    @Resource
    private YIMQManager manager;
    @Value("${yimq.service.uri}")
    private String uri;

    private String serviceName;

    public YIMQClient(){

    }
    public YIMQClient(YIMQManager manager,String serviceName) {
        this.manager = manager;
        this.serviceName = serviceName;
    }

   /* public YimqTransactionMessage topic(String topic) {
        return new YimqTransactionMessage(this,topic);
    }*/

    public TccSubTask tcc(String processor) throws Exception {
        return new TccSubTask(this,this.getTransactionMessage(),processor);
    }

    public EcSubTask ec(String processor) throws Exception {
        return new EcSubTask(this,this.getTransactionMessage(),processor);
    }

    public void setTransactionMessage(YimqTransactionMessage transactionMessage) {
        this.manager.transactionMessage = transactionMessage;
    }

    public boolean hasTransactionMessage(){
        if (null == this.manager.transactionMessage)
            return false;
        return true;
    }

    public YimqTransactionMessage getTransactionMessage(){
        return this.manager.transactionMessage;
    }


    public JSONObject callServer(String action, Map<String,Object> context) {
        context.put("actor",this.manager.actorName);
        String url = uri+ YimqCommonUtils.getActionMap().get(action);
        JSONObject result;
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
