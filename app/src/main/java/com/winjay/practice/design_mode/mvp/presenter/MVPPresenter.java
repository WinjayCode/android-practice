package com.winjay.practice.design_mode.mvp.presenter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.winjay.practice.design_mode.mvp.model.IMVPModel;
import com.winjay.practice.design_mode.mvp.model.MVPModel;
import com.winjay.practice.design_mode.mvp.view.IMVPView;
import com.winjay.practice.utils.LogUtil;

/**
 * MVP中Presenter层
 *
 * @author Winjay
 * @date 2020-01-10
 */
public class MVPPresenter implements LifecycleObserver {
    private static final String TAG = "MVPPresenter";

    private IMVPView mIMVPView;
    private IMVPModel mIMVPModel;

    public MVPPresenter() {
        mIMVPModel = new MVPModel();
    }

    public void getData() {
        LogUtil.d(TAG);
        mIMVPModel.getData(new IMVPModel.GetDataListener() {
            @Override
            public void onComplete(String data) {
                LogUtil.d(TAG, "data=" + data);
                if (mIMVPView != null) {
                    mIMVPView.showData(data);
                }
            }
        });
    }

    /**
     * Lifecycle在MVP中的使用
     * 当Activity生命周期发生变化时，Presenter就可以感知并执行方法，不需要在Activity的多个生命周期方法中调用Presenter的方法了。
     *
     * @param owner
     */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    private void getDataOnStart(LifecycleOwner owner) {
        LogUtil.d(TAG);
        mIMVPModel.getData(new IMVPModel.GetDataListener() {
            @Override
            public void onComplete(String data) {
                LogUtil.d(TAG, "data=" + data);
                // getData 是耗时操作，回调后检查当前生命周期状态是否大于或等于给定状态
                if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    if (mIMVPView != null) {
                        mIMVPView.showData(data + "(Lifecycle回调数据)");
                    }
                }
            }
        });
    }

    public void attachView(IMVPView mvpView) {
        mIMVPView = mvpView;
    }

    public void detachView() {
        mIMVPView = null;
    }
}
