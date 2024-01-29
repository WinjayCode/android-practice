package com.winjay.mirrorcast.common;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.winjay.mirrorcast.util.LogUtil;

/**
 * Fragment预加载问题的解决方案：
 * 1.可以懒加载的Fragment
 * 2.切换到其他页面时停止加载数据（可选）
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {
    protected final String TAG = "BaseFragment";
    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;

    private VB binding;

    private OnBackPressedCallback mOnBackPressedCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = onCreateViewBinding(inflater, container);
        isInit = true;
        /**初始化的时候去加载数据**/
        isCanLoadData();
        return binding.getRoot();
    }

    public VB getBinding() {
        return binding;
    }

    protected abstract VB onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        mOnBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                showBackInfo();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, mOnBackPressedCallback);
    }

    public void showBackInfo() {
//        if (mOnBackPressedCallback != null){
//            mOnBackPressedCallback.setEnabled(false);
//        }
//        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        binding = null;
    }

    protected void toast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract void lazyLoad();

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以调用此方法
     */
    protected void stopLoad() {
    }
}
