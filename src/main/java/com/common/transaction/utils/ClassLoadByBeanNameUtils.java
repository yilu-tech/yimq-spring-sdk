package com.common.transaction.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * create by gaotiedun ON 2020/3/27 11:41
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Component
public class ClassLoadByBeanNameUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(ClassLoadByBeanNameUtils.applicationContext == null) {
            ClassLoadByBeanNameUtils.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name,以及Clazz返回指定的Bean
    public <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
}
