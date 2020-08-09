package com.winjay.compiler;

import com.squareup.javapoet.ClassName;

public class ClassNameUtil {
    public static final ClassName UI_THREAD = ClassName.get("androidx.annotation", "UiThread");
    public static final ClassName CALLSUPER = ClassName.get("androidx.annotation", "CallSuper");
    public static final ClassName VIEW = ClassName.get("android.view", "View");

    public static final ClassName UNBINDER = ClassName.get("com.winjay.bind", "Unbinder");
    public static final ClassName UTILS = ClassName.get("com.winjay.bind", "Utils");
}
