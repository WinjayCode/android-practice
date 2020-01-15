package com.winjay.practice.design_mode.mvp.view;

/**
 * 视图方法的抽象
 *
 * @author Winjay
 * @date 2020-01-10
 */
public interface IMVPView {
    /**
     * 显示数据
     *
     * @param data
     */
    void showData(String data);
}
