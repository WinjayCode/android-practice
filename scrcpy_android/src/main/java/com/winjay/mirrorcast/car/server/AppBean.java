package com.winjay.mirrorcast.car.server;

import android.graphics.drawable.Drawable;

/**
 * @author F2848777
 * @date 2022-11-24
 */
public class AppBean {
    private String appName;
    private Drawable appIcon;
    private String pkgName;
    private String enterClass;

    public AppBean() {
    }

    public AppBean(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getEnterClass() {
        return enterClass;
    }

    public void setEnterClass(String enterClass) {
        this.enterClass = enterClass;
    }
}
