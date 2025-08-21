package com.ll.framework.ioc;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationContext {
    private final BeanRegistry beanRegistry = new BeanRegistry();
    private final BeanScanner beanScanner;
    private final DependencyResolver dependencyResolver;

    public void init() {
        beanScanner.scan();
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        if (beanRegistry.containsBean(beanName)) {
            return (T) beanRegistry.getBean(beanName);
        }

        return (T) dependencyResolver.createBean(beanName);
    }
}