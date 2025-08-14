package com.ll.framework.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component //@Configuration 어노테이션에 @Component 어노테이션 포함됨
public @interface Configuration {
}
