package com.common.transaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * create by gaotiedun ON 2020/3/27 11:31
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Data
@Component
@ConfigurationProperties(prefix = "transaction")
public class TransactionClassConfig {

    private Map<String,String> maps;

    public Map<String, String> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, String> maps) {
        this.maps = maps;
    }
}
