package com.yimq.transaction.utils;

import com.yimq.transaction.client.YIMQClient;
import com.yimq.transaction.message.TransactionYimqMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/31 16:45
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class YIMQManager {
    public Map<String, YIMQClient> yimqClientMap;
    public TransactionYimqMessage transactionMessage = null;
    @Value("${actor.name}")
    public String actorName;

    public YIMQManager(){

    }

    public YIMQClient client(String name) {
        return getClient(name);
    }

    public YIMQClient getClient(String name){
        if (null == yimqClientMap.get(name)) {
            yimqClientMap.put(name,new YIMQClient(this,name));
        }
        return yimqClientMap.get(name);
    }

}
