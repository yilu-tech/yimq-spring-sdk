package com.yimq.transaction.config;

import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;

/**
 * create by gaotiedun ON 2020/3/25 9:58
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Configuration
@EnableTransactionManagement
public class XADataSourceConfig {

    @Bean("xaDataSource")
    public MysqlXADataSource getXADataSource(DruidDataSourceSettings dataSourceSettings){
        MysqlXADataSource druidXADataSource = new MysqlXADataSource();
        druidXADataSource.setUrl("jdbc:mysql://127.0.0.1:3306/world?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        druidXADataSource.setUser("root");
        druidXADataSource.setPassword("root");
        return druidXADataSource;
    }


    @Bean("xaResource")
    public XAResource getXAResource(MysqlXADataSource druidXADataSource){
        XAResource xaResource = null;
        try {
            XAConnection connection = druidXADataSource.getXAConnection();
            xaResource = connection.getXAResource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return xaResource;
    }

}
