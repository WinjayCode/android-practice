package com.winjay.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.winjay.annotations.BindView;
import com.winjay.annotations.ListenerClass;
import com.winjay.annotations.ListenerMethod;
import com.winjay.annotations.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

/**
 * 需要生成的Activity类，包括BindView和OnClick
 *
 * @author Winjay
 * @date 2020/8/4
 */
public class BindingClass {
    private Elements mElementUtils;
    private List<Element> viewElementList = new ArrayList<>();
    private Map<String, List<Element>> onClickElementMap = new HashMap<>();

    public BindingClass(Elements elements) {
        mElementUtils = elements;
    }

    public void addBindingView(Element element) {
        viewElementList.add(element);
    }

    public void addBindingClick(String executableMethodName, Element element) {
        List<Element> onClickElementList = new ArrayList<>();
        onClickElementList.add(element);
        onClickElementMap.put(executableMethodName, onClickElementList);
    }

    public JavaFile generateJavaFile() {
        // 生成 public class xxxActivity_ViewBinding implements Unbinder
        String activityClassNameStr = viewElementList.get(0).getEnclosingElement().getSimpleName().toString();

        // xxxActivity
        ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
        TypeSpec.Builder classBuilder = TypeSpec
                // xxxActivity_ViewBinding
                .classBuilder(activityClassNameStr + "_ViewBinding")
                // public
                .addModifiers(Modifier.PUBLIC)
                // implements
                .addSuperinterface(ClassNameUtil.UNBINDER)
                // private xxxActivity target;
                .addField(activityClassName, "target", Modifier.PRIVATE);

        // 构造函数
        MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                .addAnnotation(ClassNameUtil.UI_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(activityClassName, "target")
                .addStatement("this.target = target")
                .addCode("\n");

        // 实现unbind方法
        MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                .addAnnotation(Override.class)
                .addAnnotation(ClassNameUtil.CALLSUPER)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T target = this.target", activityClassName)
                .addStatement("if (target == null) throw new $T($S)", IllegalStateException.class, "Bindings already cleared.")
                .addStatement("this.target = null")
                .addCode("\n");

        // findView
        for (Element viewBindElement : viewElementList) {
            String filedName = viewBindElement.getSimpleName().toString();
            int resId = viewBindElement.getAnnotation(BindView.class).value();
            // target.xxx = Utils.findViewById(target, xxx);
            constructorMethodBuilder.addStatement("target.$L = $T.findViewById(target, $L)", filedName, ClassNameUtil.UTILS, resId);

            // target.appCompatTextView = null;
            unbindMethodBuilder.addStatement("target.$L = null", filedName);
            unbindMethodBuilder.addCode("\n");
        }

        // setOnClickListener
        if (!onClickElementMap.isEmpty()) {
            constructorMethodBuilder.addStatement("View view");
        }
        for (Map.Entry<String, List<Element>> entry : onClickElementMap.entrySet()) {
            String executableMethodName = entry.getKey();
            List<Element> onClickElementList = entry.getValue();
            for (Element onClickElement : onClickElementList) {
                OnClick onClick = onClickElement.getAnnotation(OnClick.class);
                int[] resIdArr = onClick.value();
                ListenerClass mListenerClass = onClick.annotationType().getAnnotation(ListenerClass.class);
                ListenerMethod method = mListenerClass.method()[0];
                for (int resId : resIdArr) {
                    String fieldName = "view" + resId;
                    String bindName = "view";
                    TypeSpec.Builder callback = TypeSpec.anonymousClassBuilder("")
                            .superclass(ClassName.bestGuess(mListenerClass.type()))
                            .addMethod(MethodSpec.methodBuilder(method.name())
                                    .addAnnotation(Override.class)
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.VOID)
                                    .addParameter(ClassNameUtil.VIEW, bindName)
                                    .addStatement("target.$L($L)", executableMethodName, bindName)
                                    .build());
                    classBuilder.addField(ClassNameUtil.VIEW, fieldName, Modifier.PRIVATE);
                    constructorMethodBuilder.addStatement("$N = $T.findViewById(target, $L)", bindName, ClassNameUtil.UTILS, resId);
                    constructorMethodBuilder.addStatement("$L = $N", fieldName, bindName);
                    constructorMethodBuilder.addStatement("$N.$L($L)", bindName, mListenerClass.setter(), callback.build());

                    // unbind
                    unbindMethodBuilder.addStatement("$N.setOnClickListener(null)", fieldName);
                    unbindMethodBuilder.addStatement("$N = null", fieldName);
                    unbindMethodBuilder.addCode("\n");
                }
            }
        }

        classBuilder.addMethod(constructorMethodBuilder.build());
        classBuilder.addMethod(unbindMethodBuilder.build());

        String packageName = mElementUtils.getPackageOf(viewElementList.get(0).getEnclosingElement()).getQualifiedName().toString();

        return JavaFile.builder(packageName, classBuilder.build())
                .addFileComment("Generated code from Winjay Bind. Do not modify!")
                .build();
    }
}
