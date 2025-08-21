package com.ll.framework.ioc;

import com.ll.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import java.lang.reflect.Constructor;
import java.util.Arrays;

@RequiredArgsConstructor
public class DependencyResolver {
    private final BeanRegistry beanRegistry;
    private final ApplicationContext context;

    public Object createBean(String beanName) {
        Class<?> clazz = beanRegistry.getBeanClass(beanName);
        if (clazz == null)
            throw new RuntimeException("해당 이름의 빈/클래스를 찾을 수 없습니다: \" + beanName");

        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            if (constructors.length == 0) return null;

            Object[] args = Arrays.stream(constructors[0].getParameterTypes())
                    .map(type -> context.genBean(Ut.str.lcfirst(type.getSimpleName())))
                    .toArray();

            Object bean = constructors[0].newInstance(args);
            beanRegistry.registerBean(beanName, bean);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("빈 생성 실패: " + beanName, e);
        }
    }
}