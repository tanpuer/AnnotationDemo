package com.example.libinjector.viewInject;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;


/**
 * Created by cw on 2018/2/11.
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.example.libinjector.viewInject.GetViewTo")
@SupportedSourceVersion(SourceVersion.RELEASE_7)

public class GetViewProcessor extends AbstractProcessor{

    private Elements mUtils;
    private Filer mFiler;
    private Messager messager;
    private Map<String, GetViewProxyInfo> mProxyMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.print("!!!!!!!!!!!!!!!");
        mProxyMap.clear();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(GetViewTo.class);
        for (Element element : elements){

            checkAnnotationValid(element);

            VariableElement variableElement = (VariableElement) element;

            //class type
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            //full className
            String fqClassName = typeElement.getQualifiedName().toString();

            GetViewProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null){
                proxyInfo = new GetViewProxyInfo(mUtils,typeElement);
                mProxyMap.put(fqClassName,proxyInfo);
            }

            GetViewTo getViewTo = variableElement.getAnnotation(GetViewTo.class);
            int id = getViewTo.value();
            proxyInfo.injectVariables.put(id,variableElement);
        }
        for (String key : mProxyMap.keySet()){
            GetViewProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean checkAnnotationValid(Element element){
        if (element.getKind() != ElementKind.FIELD){
            return false;
        }
        if (element.getModifiers().contains(Modifier.PRIVATE)){
            return false;
        }
        return true;
    }

}
