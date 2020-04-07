package com.yimq.transaction.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gtd
 * description:
 * date: Create in 21:50 2019/4/15
 */
@Configuration
@AutoConfigureAfter(MybatisDataSourceConfig.class)
public class MyBatisMapperScanerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.yimq.transaction.dao");
        return mapperScannerConfigurer;
    }

}
