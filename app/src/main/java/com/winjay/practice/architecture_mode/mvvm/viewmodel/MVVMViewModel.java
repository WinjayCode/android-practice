package com.winjay.practice.architecture_mode.mvvm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.winjay.practice.architecture_mode.mvvm.model.Callback;
import com.winjay.practice.architecture_mode.mvvm.model.MVVMDataBean;
import com.winjay.practice.architecture_mode.mvvm.model.MVVMRepository;
import com.winjay.practice.utils.JsonUtil;
import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 * MVVM 的VM层
 * 注意，没有持有View层的任何引用
 */
public class MVVMViewModel extends ViewModel {
    private static final String TAG = "UserListViewModel";
    /**
     * 用户信息
     */
    private MutableLiveData<List<MVVMDataBean>> userListLiveData;

    /**
     * 进条度的显示
     */
    private MutableLiveData<Boolean> loadingLiveData;

    public MVVMViewModel() {
        userListLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
    }

    /**
     * 获取用户列表信息
     * 假装网络请求 2s后 返回用户信息
     */
    public void getUserInfo() {

        loadingLiveData.setValue(true);

        MVVMRepository.getUserRepository().getUsersFromServer(new Callback<List<MVVMDataBean>>() {
            @Override
            public void onSuccess(List<MVVMDataBean> users) {
                LogUtil.d(TAG, "users=" + JsonUtil.getInstance().toJson(users));
                loadingLiveData.setValue(false);
                userListLiveData.setValue(users);
            }

            @Override
            public void onFailed(String msg) {
                LogUtil.d(TAG, "failed=" + msg);
                loadingLiveData.setValue(false);
                userListLiveData.setValue(null);
            }
        });
    }

    /**
     * 返回LiveData类型
     */
    public LiveData<List<MVVMDataBean>> getUserListLiveData() {
        return userListLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }
}