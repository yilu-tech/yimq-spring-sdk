package com.yimq.transaction.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author gtd
 * description:
 * date: Create in 21:36 2019/4/15
 */
@Component
@ConfigurationProperties(prefix = "mybatis")
public class MybatisDataSourceConfig {

    private static final Log log  = LogFactory.getLog(MybatisDataSourceConfig.class);

    @Autowired
    @Qualifier("dataSource")
    private final DataSource dataSource;

    public MybatisDataSourceConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    //  配置类型别名
    private String typeAliasesPackage;

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }


    private String mapperLocations;


    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(){
        log.info("------初始化sqlSessionFactory------");
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        SqlSessionFactory sqlSessionFactory = null;
        bean.setDataSource(dataSource);
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(mapperLocations);
            bean.setMapperLocations(resources);
            bean.setTypeAliasesPackage(typeAliasesPackage);
            sqlSessionFactory = bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlSessionFactory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(@Autowired SqlSessionFactory sqlSessionFactory){
        log.info("------初始化SqlSessionTemplate开始------");
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
