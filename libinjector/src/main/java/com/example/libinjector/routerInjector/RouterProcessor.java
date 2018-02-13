package com.example.libinjector.routerInjector;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
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
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by cw on 2018/2/12.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.example.libinjector.routerInjector.RouterMap")
public class RouterProcessor extends AbstractProcessor {

    private Elements mUtils;
    private Filer mFiler;
    private Messager messager;
    private RouterProxyInfo mProxyInfo;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (mProxyInfo == null) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(RouterMap.class);
            for (Element element : elements) {
                if (checkAnnotationValid(element)) {
                    TypeElement typeElement = (TypeElement) element;
                    if (mProxyInfo == null) {
                        mProxyInfo = RouterProxyInfo.getInstance(typeElement, mUtils);
                    }
                    RouterMap routerMap = typeElement.getAnnotation(RouterMap.class);
                    String value = routerMap.value();
                    RouterProxyInfo.injectTypeElements.put(value, typeElement);
                }
            }
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        mProxyInfo.getProxyClassFullName(),
                        mProxyInfo.getTypeElement()
                );
                Writer writer = jfo.openWriter();
                writer.write(mProxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean checkAnnotationValid(Element element){
        if (element.getKind() != ElementKind.CLASS){
            return false;
        }
        if (element.getModifiers().contains(Modifier.PRIVATE)){
            return false;
        }
        return true;
    }

}
