package com.example.cw.annotationdemo.annotation.viewInject;

import android.app.Activity;
import android.view.View;

/**
 * Created by cw on 2018/2/12.
 */

public class ViewInjector {

    private static final String SUFFIX = "$$ViewInject";

    public static ViewInject findProxyActivity(Object activity){
        try {
            Class clz = activity.getClass();
            String className = clz.getName() + SUFFIX;
            Class injectorClass = Class.forName(className);
            return (ViewInject) injectorClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s when compling", activity.getClass().getSimpleName() + SUFFIX));
    }

    public static void injectView(Activity activity){
        ViewInject proxyActivity = findProxyActivity(activity);
        proxyActivity.inject(activity,activity);
    }

    public static void injectView(Object activity, View view){
        ViewInject proxActivity = findProxyActivity(activity);
        proxActivity.inject(activity,view);
    }
}
