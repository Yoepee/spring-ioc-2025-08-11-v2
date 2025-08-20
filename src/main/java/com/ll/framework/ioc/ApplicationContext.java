package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ll.standard.util.Ut.str.lcfirst;

public class ApplicationContext {
    private String basePackage;
    private static Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        reflections.getTypesAnnotatedWith(Component.class)
                .stream()
                .filter(c -> !c.isAnnotation())
                .forEach(c -> getBean(lcfirst(c.getSimpleName()), c));
    }

    @SuppressWarnings("unchecked")
    public static <T> T genBean(String beanName) {
        return (T) singletonObjects.get(beanName);
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean(String beanName, Class<T> cls) {
        return (T) singletonObjects.computeIfAbsent(beanName, k -> construct(cls));
    }

    @SneakyThrows
    private <T> T construct(Class<T> cls) {
        Constructor<T> constructor = (Constructor<T>) cls.getDeclaredConstructors()[0];
        Object[] args = Arrays.stream(constructor.getParameterTypes())
                .map(dep -> getBean(lcfirst(dep.getSimpleName()), dep))
                .toArray();

        return constructor.newInstance(args);
    }
}
