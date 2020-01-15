package com.winjay.practice.design_mode.mvp.presenter;

import com.winjay.practice.design_mode.mvp.model.IMVPModel;
import com.winjay.practice.design_mode.mvp.model.MVPModel;
import com.winjay.practice.design_mode.mvp.view.IMVPView;

/**
 * Presenter层
 *
 * @author Winjay
 * @date 2020-01-10
 */
public class MVPPresenter {
    private IMVPView mIMVPView;
    private IMVPModel mIMVPModel;

    public MVPPresenter(IMVPView mvpView) {
        mIMVPView = mvpView;
        mIMVPModel = new MVPModel();
    }

    public void getData() {
        mIMVPModel.getData(new IMVPModel.GetDataListener() {
            @Override
            public void onComplete(String data) {
                mIMVPView.showData(data);
            }
        });
    }
}
