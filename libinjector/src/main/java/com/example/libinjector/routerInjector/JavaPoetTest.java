package com.example.libinjector.routerInjector;

import com.squareup.javapoet.MethodSpec;


/**
 * Created by cw on 2018/2/13.
 */

public class JavaPoetTest {

    private void test(){
        MethodSpec main = MethodSpec.methodBuilder("main")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();
    }
}
