package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import com.ll.standard.util.Ut;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private final Reflections reflections;
    private final Map<String, Class<?>> beanDefinitions = new ConcurrentHashMap<>();
    private final Map<String, Object> singletons = new ConcurrentHashMap<>();

    public ApplicationContext(String basePackage) {
        reflections = new Reflections(basePackage, Scanners.TypesAnnotated, Scanners.SubTypes);
    }

    public void init() {
        // 1) @Component 직붙 클래스 수집
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);

        // 2) 정의된 bean 이름 수집
        for (Class<?> c : classes) {
            String name = Ut.str.lcfirst(c.getSimpleName());
            beanDefinitions.put(name, c);
        }
    }

    public <T> T genBean(String beanName) { // 요청하신 메서드명 유지
        Object exist = singletons.get(beanName);
        if (exist != null) return (T) exist;

        Class<?> clazz = beanDefinitions.get(beanName);
        if (clazz == null) throw new NoSuchElementException("No bean named '" + beanName + "'");

        Object created = createBean(beanDefinitions.get(beanName));
        singletons.put(beanName, created);
        return (T) created;
    }

    /** 생성자 주입: 가장 파라미터 많은 public 생성자를 골라 타입으로 의존성 주입 */
    private Object createBean(Class<?> clazz) {
        try {
            Constructor<?>[] ctors = clazz.getConstructors();
            if (ctors.length == 0) {
                // public 생성자가 없으면 기본 생성자를 시도(비공개 포함)
                Constructor<?> c = clazz.getDeclaredConstructor();
                c.setAccessible(true);
                return c.newInstance();
            }

            Constructor<?> target = Arrays.stream(ctors)
                    .max(Comparator.comparingInt(Constructor::getParameterCount))
                    .orElseThrow();

            Object[] args = Arrays.stream(target.getParameterTypes())
                    .map(pt -> Ut.str.lcfirst(pt.getSimpleName()))
                    .map(this::genBean)
                    .toArray();

            target.setAccessible(true);
            return target.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No suitable constructor for " + clazz.getName(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean: " + clazz.getName(), e);
        }
    }
}
