package com.example.demo.annotation;/* Top Secret */

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";

    Scope scope() default Scope.SINGLETON;

}
