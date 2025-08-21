package com.ll.framework.ioc;

import java.util.HashMap;
import java.util.Map;

public class BeanRegistry {
    private final Map<String, Class<?>> beanClasses = new HashMap<>();
    private final Map<String, Object> beans = new HashMap<>();

    public void registerBeanClass(String beanName, Class<?> clazz) {
        beanClasses.put(beanName, clazz);
    }

    public void registerBean(String beanName, Object bean) {
        beans.put(beanName, bean);
    }

    public Class<?> getBeanClass(String beanName) {
        return beanClasses.get(beanName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        return (T) beans.get(beanName);
    }

    public boolean containsBean(String beanName) {
        return beans.containsKey(beanName);
    }
}