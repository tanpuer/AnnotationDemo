###注解

元注解是JAVA提供的基础注解，负责注解其他注解

#####Retention
@Retention 保留注解的生命周期，对应RetentionPolicy的枚举类,表示注解在何时生效:

     SOURCE: 只在源码中有效，编译时抛弃
     CLASS: 编译class文件时生效
     RUNTIME: 运行时才生效

#####Target
@Target 标明注解的适用范围，对应ElementType枚举类，明确了注解的有效范围:

    TYPE: 类，接口，枚举，注解类型
    FILED: 类成员
    METHOD: 方法
    PARAMETER: 参数
    CONSTRUCTOR：构造器。
    LOCAL_VARIABLE：局部变量
    ANNOTATION_TYPE：注解。
    PACKAGE：包声明
    TYPE_PARAMETER：类型参数。
    TYPE_USE：类型使用声明。

####自定义注解

#####Runtime
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface getViewTo {
    int value() default -1;
}

public class AnnotationTest extends Activity {

    @getViewTo(R.id.annotation_btn)
    private Button mButton;

    @getViewTo(R.id.annotation_text)
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotation_activity);
    }

    private void getAllAnnotationView(){
        //获得成员变量
        Field[] fields = getClass().getFields();
        for (Field field : fields){
            //判断注解
            if (field.getAnnotations() != null){
                //确定注解类型
                if (field.isAnnotationPresent(getViewTo.class)){
                    field.setAccessible(true);
                    getViewTo getViewTo = field.getAnnotation(com.example.cw.summary.annotaion.getViewTo.class);
                    try {
                        field.set(this, findViewById(getViewTo.value()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
```
像这样的运行时注解，虽然通过反射代替了findViewById，但是影响效率

#####Class
说到编译时注解，就不得不提到注解处理器AbstractProcessor，是一个javac工具，用于在编译时扫描和处理注解。你可以自定义注解，并注册相应的注解处理器，用于处理注解逻辑。
在Android Studio里面是无法获得AbstractProcessor这个类的，需要建一个Java Library。
```java
public class CustomProcessor extends AbstractProcessor{

    //初始化处理器，一般在此获取我们需要的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    //返回支持的注解类集合  @SupportedAnnotationTypes同功能
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    //制定java版本 @SupportedSourceVersion同功能
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    //处理器实际处理逻辑入口
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }

}
```
一般处理器处理逻辑：
1. 遍历源码，得到需要解析的元素列表
2. 判断元素是否可见和符合要求
3. 组织数据结构得到输出类参数
4. 输入生成java文件
5. 错误处理

Element代表源代码，TypeElement代表源代码中的类型元素，在process方法中需要我们手动生成Java代码。此外想要在AndroidStudio里使用javapoet需要新建一个JavaModule，下面还是以viewInjector为例。
```java
//定义定义编译时注解GetViewTo
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface GetViewTo {
    int value() default -1;
}

//注解代理类 主要是完成Java代码的生成工作
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
        builder.append("import ").append("com.example.cw.summary.annotation.viewInject.*;\n\n");

        //实现接口ViewInject
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

//最后在AbstractProcessor的process方法中调用GetViewProxyInfo生成Java代码
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.example.libinjector.GetViewTo")
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

//定义接口ViewInject
public interface ViewInject<T>{

    void inject(T t, Object source);

}

//以及注入工具类ViewInjector
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

//编译时生成后缀为$$ViewInject的代理类，实现ViewInject接口，在app/build/generated/source/apt/debug目录下可以找到对应的类。
//Generated Code. Do not modify!
package com.example.cw.summary.annotation;

import com.example.cw.summary.annotation.viewInject.*;

public class AnnotationTest$$ViewInject implements ViewInject<com.example.cw.summary.annotation.AnnotationTest>{
@Override
public void inject(com.example.cw.summary.annotation.AnnotationTest host, Object source){
 if(source instanceof android.app.Activity) {
host.mTextView=(android.widget.TextView)(((android.app.Activity)source).findViewById( 2131165215));
}else{
host.mTextView=(android.widget.TextView)(((android.view.View)source).findViewById( 2131165215));
}
 if(source instanceof android.app.Activity) {
host.mButton=(android.widget.Button)(((android.app.Activity)source).findViewById( 2131165214));
}else{
host.mButton=(android.widget.Button)(((android.view.View)source).findViewById( 2131165214));
}
}
}

//注意使用必须在setContentView之后
setContentView(R.layout.annotation_activity);
ViewInjector.injectView(this);
```

这样就完成了一个简单的注解框架！

基于同样的思路，完成了RouterManager
```java
@RouterMap("router://test1/index?name=hy&age=16")
public class RouterTest1 extends Activity{
}
```
通过APT生成了Global$$RouterInject类，在Application的onCreate方法中反射初始化注入路由，还有个🤔️，通过APT能完全避免反射吗？
```java
//Auto Generated Code, Do not Modify!
package com.example.cw.summary.annotation.routerInject;

import com.example.cw.summary.annotation.routerInject.*;
public class Global$$RouterInject{
    public static void inject(){
        RouterManager.addRouter("router://test2/index?name=cw&age=18",com.example.cw.summary.annotation.routerInject.RouterTest2.class);
        RouterManager.addRouter("router://test1/index?name=hy&age=16",com.example.cw.summary.annotation.routerInject.RouterTest1.class);
    }
}
```


