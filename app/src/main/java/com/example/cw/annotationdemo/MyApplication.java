package com.example.cw.annotationdemo;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cw on 2018/2/13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class routerClass = Class.forName("com.example.cw.annotationdemo.annotation.routerInject.Global$$RouterInject");
            Method method = routerClass.getMethod("inject");
            method.invoke(null,new Object[]{});
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
