package com.winjay.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.winjay.annotations.BindView;
import com.winjay.annotations.OnClick;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.swing.text.View;
import javax.tools.Diagnostic;

/**
 * @author Winjay
 * @date 2020/7/29
 */
@AutoService(Processor.class)
public class WinjayProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Elements mElementUtils;
    private static final ClassName UI_THREAD = ClassName.get("androidx.annotation", "UiThread");
    private static final ClassName CALLSUPER = ClassName.get("androidx.annotation", "CallSuper");
    private static final ClassName VIEW = ClassName.get("android.view", "View");

    private static final ClassName UNBINDER = ClassName.get("com.winjay.bind", "Unbinder");
    private static final ClassName UTILS = ClassName.get("com.winjay.bind", "Utils");

    private Map<String, BindingClass> bindingClassMap = new HashMap<>();

    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    // 1.处理指定的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 2.给到需要处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        // 需要解析的自定义注解
        annotations.add(BindView.class);
//        annotations.add(OnClick.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//        // Process each @BindView element.
//        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
//        if (!bindViewElements.isEmpty()) {
//            processBindView(bindViewElements);
//        }
//
//        // Process each @OnClick element.
//        Set<? extends Element> onClickElements = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
//        if (!onClickElements.isEmpty()) {
//            processOnClick(onClickElements);
//        }

        bindingClassMap.clear();

        processBindViewClass(roundEnvironment);
        processOnClickClass(roundEnvironment);

        for (BindingClass bindingClass : bindingClassMap.values()) {
            JavaFile javaFile = bindingClass.generateJavaFile();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Generated code error!");
            }
        }

        return false;
    }

    private void processBindViewClass(RoundEnvironment roundEnvironment) {
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : bindViewElements) {
            String className = element.getEnclosingElement().getSimpleName().toString();
            BindingClass bindingClass = bindingClassMap.get(className);
            if (bindingClass == null) {
                bindingClass = new BindingClass(mElementUtils);
                bindingClassMap.put(className, bindingClass);
            }
            bindingClass.addBindingView(element);
        }
    }

    private void processOnClickClass(RoundEnvironment roundEnvironment) {
        Set<? extends Element> onClickElements = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        for (Element element : onClickElements) {
            String className = element.getEnclosingElement().getSimpleName().toString();
            BindingClass bindingClass = bindingClassMap.get(className);
            if (bindingClass == null) {
                bindingClass = new BindingClass(mElementUtils);
                bindingClassMap.put(className, bindingClass);
            }
            bindingClass.addBindingClick(element);
        }
    }

    private void processBindView(Set<? extends Element> elements) {
        // 解析 activity -> List<Element>
        Map<Element, List<Element>> elementsMap = new LinkedHashMap<>();

        // 解析 Map<activity, List<bindViewElements>>
        for (Element element : elements) {
            Element enclosingElement = element.getEnclosingElement();
            List<Element> bindViewElements = elementsMap.get(enclosingElement);
            if (bindViewElements == null) {
                bindViewElements = new ArrayList<>();
            }
            bindViewElements.add(element);
            elementsMap.put(enclosingElement, bindViewElements);
        }

        // 生成代码
        for (Map.Entry<Element, List<Element>> entry : elementsMap.entrySet()) {
            Element enclosingElement = entry.getKey();
            List<Element> viewBindElements = entry.getValue();

            // 生成 public class xxxActivity_ViewBinding implements Unbinder
            String activityClassNameStr = enclosingElement.getSimpleName().toString();

            // xxxActivity
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            TypeSpec.Builder classBuilder = TypeSpec
                    // xxxActivity_ViewBinding
                    .classBuilder(activityClassNameStr + "_ViewBinding")
                    // public
                    .addModifiers(Modifier.PUBLIC)
                    // implements
                    .addSuperinterface(UNBINDER)
                    // private xxxActivity target;
                    .addField(activityClassName, "target", Modifier.PRIVATE);

            // 实现unbind方法
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(CALLSUPER)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$T target = this.target", activityClassName)
                    .addStatement("if (target == null) throw new $T($S)", IllegalStateException.class, "Bindings already cleared.")
                    .addStatement("this.target = null")
                    .addCode("\n");

            // 构造函数
            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                    .addAnnotation(UI_THREAD)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(activityClassName, "target")
                    .addStatement("this.target = target")
                    .addCode("\n");

            // findViewById属性
            for (Element viewBindElement : viewBindElements) {
                String filedName = viewBindElement.getSimpleName().toString();
                int resId = viewBindElement.getAnnotation(BindView.class).value();
                // target.xxx = Utils.findViewById(target, xxx);
                constructorMethodBuilder.addStatement("target.$L = $T.findViewById(target, $L)", filedName, UTILS, resId);

                // target.appCompatTextView = null;
                unbindMethodBuilder.addStatement("target.$L = null", filedName);
            }

            classBuilder.addMethod(unbindMethodBuilder.build());
            classBuilder.addMethod(constructorMethodBuilder.build());

            // 生成类
            String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            try {
                JavaFile.builder(packageName, classBuilder.build())
                        .addFileComment("Generated code from Winjay Bind. Do not modify!")
                        .build()
                        .writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Generated code error!");
            }
        }
    }

    private void processOnClick(Set<? extends Element> elements) {
        // 解析 activity -> List<Element>
        Map<Element, List<Element>> elementsMap = new LinkedHashMap<>();

        // 解析 Map<activity, List<onClickElements>>
        for (Element element : elements) {
            Element enclosingElement = element.getEnclosingElement();
            List<Element> onClickElements = elementsMap.get(enclosingElement);
            if (onClickElements == null) {
                onClickElements = new ArrayList<>();
            }
            onClickElements.add(element);
            elementsMap.put(enclosingElement, onClickElements);
        }

        // 生成代码
        for (Map.Entry<Element, List<Element>> entry : elementsMap.entrySet()) {
            Element enclosingElement = entry.getKey();
            List<Element> onClickElements = entry.getValue();

            // 生成 public class xxxActivity_ViewBinding implements Unbinder
            String activityClassNameStr = enclosingElement.getSimpleName().toString();

            // xxxActivity
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            TypeSpec.Builder classBuilder = TypeSpec
                    // xxxActivity_ViewBinding
                    .classBuilder(activityClassNameStr + "_ViewBinding")
                    // public
                    .addModifiers(Modifier.PUBLIC)
                    // implements
                    .addSuperinterface(UNBINDER)
                    // private xxxActivity target;
                    .addField(activityClassName, "target", Modifier.PRIVATE);

            // 实现unbind方法
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(CALLSUPER)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$T target = this.target", activityClassName)
                    .addStatement("if (target == null) throw new $T($S)", IllegalStateException.class, "Bindings already cleared.")
                    .addStatement("this.target = null")
                    .addCode("\n");

            // 构造函数
            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                    .addAnnotation(UI_THREAD)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(activityClassName, "target")
                    .addStatement("this.target = target")
                    .addCode("\n");

            // setOnClickListener
            for (Element onClickElement : onClickElements) {
//                String filedName = onClickElement.getSimpleName().toString();
//                int resId = onClickElement.getAnnotation(BindView.class).value();
//                // target.xxx = Utils.findViewById(target, xxx);
//                constructorMethodBuilder.addStatement("target.$L = $T.findViewById(target, $L)", filedName, UTILS, resId);
//
//                // target.appCompatTextView = null;
//                unbindMethodBuilder.addStatement("target.$L = null", filedName);

                classBuilder.addField(VIEW, "view1", Modifier.PRIVATE);
                constructorMethodBuilder.addStatement("View view");
                constructorMethodBuilder.addStatement("view1 = view");
            }

            classBuilder.addMethod(unbindMethodBuilder.build());
            classBuilder.addMethod(constructorMethodBuilder.build());

            // 生成类
            String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            try {
                JavaFile.builder(packageName, classBuilder.build())
                        .addFileComment("Generated code from Winjay Bind. Do not modify!")
                        .build()
                        .writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Generated code error!");
            }
        }
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
