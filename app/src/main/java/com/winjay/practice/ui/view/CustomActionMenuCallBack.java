package com.winjay.practice.ui.view;

public interface CustomActionMenuCallBack {
    /**
     * 创建ActionMenu菜单。
     * 返回值false，保留默认菜单；返回值true，移除默认菜单
     *
     * @param menu
     * @return 返回false，保留默认菜单；返回true，移除默认菜单
     */
    boolean onCreateCustomActionMenu(ActionMenu menu);

    /**
     * ActionMenu菜单的点击事件
     *
     * @param itemTitle       ActionMenu菜单item的title
     * @param selectedContent 选择的文字
     */
    void onCustomActionItemClicked(String itemTitle, String selectedContent);
}
