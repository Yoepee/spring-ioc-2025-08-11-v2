package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.ll.standard.util.Ut.str.lcfirst;

/**
 * 현재 상황
 * Component (Configuration, Repository, Service) 어노테이션 생성되어 있음
 * beanName으로 넘어오는 인자는 "testRepository", "testService", "testFacadeService"
 * 싱글톤 객체 생성이 목표
 * 빈 생성 시 하드코딩 금지 -> org.reflections:reflections 라이브러리를 활용

 * 문제 해결 전략
 * 1. @component 어노테이션 붙은 클래스 먼저 찾기
 * 2. 각 클래스 객체 생성
 *      - 객체 타입
 *      - 생성자 파라미터 타입
 * 3. beans Map에 저장
 */
public class ApplicationContext {

    Reflections reflections;
    String basePackage;
    Map<String, Class<?>> beanTypes = new HashMap<>();
    Map<String, Object> beans = new HashMap<>();


    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Component.class);
        annotatedClasses.removeIf(Class::isAnnotation);

        for (Class<?> c : annotatedClasses) {
            String beanName = lcfirst(c.getSimpleName());
//            System.out.println(beanName);
            beanTypes.put(beanName, c);
        }


    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        if(beans.containsKey(beanName)) {
            return (T) beans.get(beanName);
        }

        Class<?> c = beanTypes.get(beanName);
        if(c == null) return null;
        try {
            Constructor<?> constructor = c.getDeclaredConstructors()[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();

            Object[] params = new Object[paramTypes.length];

            for(int i=0; i<paramTypes.length; i++) {
                String paramBeanName = lcfirst(paramTypes[i].getSimpleName());
                params[i] = genBean(paramBeanName);
            }

            Object bean = constructor.newInstance(params);
            beans.put(beanName, bean);
            return (T) bean;

        } catch (Exception e) {
            return null;
        }
    }
}
