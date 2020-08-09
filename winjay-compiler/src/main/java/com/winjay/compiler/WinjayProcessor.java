package com.winjay.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.winjay.annotations.BindView;
import com.winjay.annotations.OnClick;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static javax.lang.model.element.ElementKind.METHOD;

/**
 * @author Winjay
 * @date 2020/7/29
 */
@AutoService(Processor.class)
public class WinjayProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Elements mElementUtils;
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
        annotations.add(OnClick.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
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
            // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
            if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
                throw new IllegalStateException(
                        String.format("@%s annotation must be on a method.", OnClick.class.getSimpleName()));
            }
            ExecutableElement executableElement = (ExecutableElement) element;
            String executableMethodName = executableElement.getSimpleName().toString();
            String className = element.getEnclosingElement().getSimpleName().toString();
            BindingClass bindingClass = bindingClassMap.get(className);
            if (bindingClass == null) {
                bindingClass = new BindingClass(mElementUtils);
                bindingClassMap.put(className, bindingClass);
            }
            bindingClass.addBindingClick(executableMethodName, element);
        }
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
