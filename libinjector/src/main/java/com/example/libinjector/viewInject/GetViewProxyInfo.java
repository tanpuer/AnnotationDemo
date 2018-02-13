package com.example.libinjector.viewInject;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by cw on 2018/2/12.
 */

public class GetViewProxyInfo {

    public static final String PROXY = "ViewInject";

    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;

    public Map<Integer,VariableElement> injectVariables = new HashMap<>();

    public GetViewProxyInfo(Elements elementUtils, TypeElement typeElement){
        this.typeElement = typeElement;
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = ClassValidator.getClassName(typeElement,packageName);
        this.packageName = packageName;
        this.proxyClassName = className + "$$" + PROXY;
    }

    public String generateJavaCode(){
        StringBuilder builder = new StringBuilder();
        builder.append("//Generated Code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import ").append("com.example.cw.annotationdemo.annotation.viewInject.*;\n\n");

        builder.append("public class ").append(proxyClassName).append(" implements " + GetViewProxyInfo.PROXY + "<").append(typeElement.getQualifiedName()).append(">");
        builder.append("{\n");

        generateMethods(builder);
        builder.append("\n");

        builder.append("}");
        return builder.toString();
    }

    public void generateMethods(StringBuilder builder){
        builder.append("@Override\n");
        builder.append("public void inject(").append(typeElement.getQualifiedName()).append(" host, Object source){\n");
        for (int id : injectVariables.keySet()){
            VariableElement variableElement = injectVariables.get(id);
            String name = variableElement.getSimpleName().toString();
            String type = variableElement.asType().toString();
            builder.append(" if(source instanceof android.app.Activity) {\n");
            builder.append("host.").append(name).append("=");
            builder.append("(").append(type).append(")(((android.app.Activity)source).findViewById( ").append(id).append("));\n");
            builder.append("}else{\n");
            builder.append("host.").append(name).append("=");
            builder.append("(").append(type).append(")(((android.view.View)source).findViewById( ").append(id).append("));\n");
            builder.append("}\n");
        }
        builder.append("}\n");
    }

    public String getProxyClassFullName(){
        return (packageName + "." + proxyClassName);
    }

    public TypeElement getTypeElement(){
        return typeElement;
    }
}
