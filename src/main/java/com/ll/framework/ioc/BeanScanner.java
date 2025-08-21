package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Stream;

import static com.ll.standard.util.Ut.str.lcfirst;

/**
 * 빈 등록 대상 클래스를 스캔하는 역할의 클래스
 */
public class BeanScanner {
    private final String basePackage;
    private final BeanRegistry beanRegistry;

    public BeanScanner(String basePackage, BeanRegistry beanRegistry) {
        this.basePackage = basePackage;
        this.beanRegistry = beanRegistry;
    }

    public void scan() {
        Reflections reflections = new Reflections(basePackage);

        Stream.of(reflections.getTypesAnnotatedWith(Component.class))
              .flatMap(Set::stream)
              .filter(clazz -> !clazz.isInterface() && !clazz.isAnnotation() && !clazz.isEnum())
              .forEach(clazz -> {
                  String beanName = lcfirst(clazz.getSimpleName());
                  beanRegistry.registerBeanClass(beanName, clazz);
              });
    }
}
