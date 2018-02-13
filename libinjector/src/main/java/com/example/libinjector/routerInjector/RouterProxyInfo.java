package com.example.libinjector.routerInjector;

import com.example.libinjector.viewInject.ClassValidator;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by cw on 2018/2/12.
 */

public class RouterProxyInfo {

    public static final String PROXY = "RouterInject";

    public String packageName;
    public String proxyClassName;
    public TypeElement typeElement;
    public static RouterProxyInfo mInstance;

    public static Map<String,TypeElement> injectTypeElements = new HashMap<>();

    private RouterProxyInfo(TypeElement typeElement, Elements elementUtils){
        this.typeElement = typeElement;
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        packageName = packageElement.getQualifiedName().toString();
        String className = ClassValidator.getClassName(typeElement, packageName);
        proxyClassName = className + "$$" + PROXY;
    }

    public static RouterProxyInfo getInstance(TypeElement typeElement, Elements elementUtils){
        if (mInstance == null){
            synchronized (RouterProxyInfo.class){
                if (mInstance == null){
                    mInstance = new RouterProxyInfo(typeElement,elementUtils);
                }
            }
        }
        return mInstance;
    }

    public String generateJavaCode(){
        StringBuilder builder = new StringBuilder();
        builder.append("//Auto Generated Code, Do not Modify!\n");
        builder.append("package ").append("com.example.cw.annotationdemo.annotation.routerInject").append(";\n\n");
        builder.append("import com.example.cw.annotationdemo.annotation.routerInject.*;\n");
        builder.append("public class ").append("Global$$RouterInject").append("{\n");

        generateMethods(builder);
        builder.append("\n");

        builder.append("}\n");

        return builder.toString();
    }

    public void generateMethods(StringBuilder builder){
        builder.append("public static void inject(){\n");
        for (String id : injectTypeElements.keySet()){
            TypeElement typeElement = injectTypeElements.get(id);
            RouterMap routerMap = typeElement.getAnnotation(RouterMap.class);
            String value = routerMap.value();
            builder.append("RouterManager.addRouter(\"").append(value).append("\",").append(typeElement).append(".class);\n");
        }
        builder.append("}\n");
    }

    public String getProxyClassFullName(){
        return packageName + "." + "Global$$RouterInject";
    }

    public TypeElement getTypeElement(){
        return typeElement;
    }

}
