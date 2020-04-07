package com.yimq.transaction.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author gtd
 * description:
 * date: Create in 9:31 2019/4/13
 */
@Configuration
@EnableTransactionManagement
public class DruidDataSourceConfig {

    @Bean("dataSource")
    public DataSource dataSource(DruidDataSourceSettings dataSourceSettings) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        //设置datasource的基本属性
        dataSource.setDriverClassName(dataSourceSettings.getDriverClassName());
        dataSource.setUrl(dataSourceSettings.getUrl());
        dataSource.setUsername(dataSourceSettings.getUsername());
        dataSource.setPassword(dataSourceSettings.getPassword());
        //设置Druid的连接属性
        dataSource.setInitialSize(dataSourceSettings.getInitialSize());
        dataSource.setMinIdle(dataSourceSettings.getMinIdle());
        dataSource.setMaxActive(dataSourceSettings.getMaxActive());
        dataSource.setMinEvictableIdleTimeMillis(dataSource.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(dataSourceSettings.getValidationQuery());
        dataSource.setTestWhileIdle(dataSourceSettings.getTestWhileIdle());
        dataSource.setTestOnBorrow(dataSourceSettings.getTestOnBorrow());
        dataSource.setTestOnReturn(dataSourceSettings.getTestOnReturn());
        dataSource.setPoolPreparedStatements(dataSourceSettings.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(dataSourceSettings.getMaxOpenPreparedStatements());
        dataSource.setFilters(dataSourceSettings.getFilters());
        dataSource.setDefaultAutoCommit(false);
        dataSource.setConnectProperties(dataSourceSettings.getConnectionProperties());
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DruidDataSourceSettings druidDataSourceSettings) throws SQLException {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(this.dataSource(druidDataSourceSettings));
        return dataSourceTransactionManager;

    }

}
