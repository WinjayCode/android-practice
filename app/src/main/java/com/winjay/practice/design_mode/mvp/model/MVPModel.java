package com.winjay.practice.design_mode.mvp.model;

/**
 * Model层
 *
 * @author Winjay
 * @date 2020-01-13
 */
public class MVPModel implements IMVPModel {

    @Override
    public void getData(GetDataListener getDataListener) {
        getDataListener.onComplete("这是MVP模式的数据!");
    }
}
