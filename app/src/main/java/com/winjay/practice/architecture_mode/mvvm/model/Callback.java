package com.winjay.practice.architecture_mode.mvvm.model;

public interface Callback<T> {

    public void onSuccess(T t);

    public void onFailed(String msg);
}
