package com.example.libinjector.viewInject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cw on 2018/2/11.
 * annotation-findViewById
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface GetViewTo {
    int value() default -1;
}
