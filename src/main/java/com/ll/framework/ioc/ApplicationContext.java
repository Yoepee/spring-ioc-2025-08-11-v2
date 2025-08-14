package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Repository;
import com.ll.framework.ioc.annotations.Service;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {

    private String basePackage;
    private Map<String, Object> beans = new HashMap<>();

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        // 1. basePackage 아래 모든 클래스 스캔
        Reflections reflections = new Reflections(basePackage);

        Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(Service.class);
        Set<Class<?>> repositoryClasses = reflections.getTypesAnnotatedWith(Repository.class);

        // 2. 먼저 repository 빈 생성
        for (Class<?> clazz : repositoryClasses) {
            createBean(clazz);
        }

        // 3. service 빈 생성
        for (Class<?> clazz : serviceClasses) {
            createBean(clazz);
        }
    }

    private Object createBean(Class<?> clazz) {
        try {
            // 빈 이름: 클래스 이름의 첫 글자를 소문자로
            String beanName = lowerFirst(clazz.getSimpleName());

            // 이미 있으면 리턴
            if (beans.containsKey(beanName)) {
                return beans.get(beanName);
            }

            // 생성자 주입
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] paramBeans = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                // 의존성 빈 생성
                paramBeans[i] = createBean(paramTypes[i]);
            }

            Object instance = constructor.newInstance(paramBeans);
            beans.put(beanName, instance);
            return instance;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T genBean(String beanName) {
        return (T) beans.get(beanName);
    }

    private String lowerFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
