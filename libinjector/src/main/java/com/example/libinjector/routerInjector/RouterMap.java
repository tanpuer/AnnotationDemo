package com.example.libinjector.routerInjector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cw on 2018/2/12.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RouterMap {
    String value() default "router://blank";
}
