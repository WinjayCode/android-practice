package com.winjay.practice.architecture_mode.mvp.model;

/**
 * @author Winjay
 * @date 2020-01-13
 */
public interface IMVPModel {
    void getData(GetDataListener getDataListener);

    interface GetDataListener {
        void onComplete(String data);
    }
}
