package com.ll.framework.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) //어노테이션 적용 위치 제한 (클래스, 인터페이스, enum, annotation)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}