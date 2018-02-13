package com.example.libinjector.viewInject;


import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by cw on 2018/2/12.
 */

public final class ClassValidator {

    public static boolean isPrivate(Element annotatedClass){
        return annotatedClass.getModifiers().contains(Modifier.PRIVATE);
    }

    public static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
