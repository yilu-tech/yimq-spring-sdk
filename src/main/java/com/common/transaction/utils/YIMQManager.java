package com.common.transaction.utils;

import com.common.transaction.client.YIMQClient;
import com.common.transaction.message.YimqTransactionMessage;
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
    public YimqTransactionMessage transactionMessage = null;
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
