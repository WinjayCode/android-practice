package com.winjay.practice.package_manager;

import android.graphics.drawable.Drawable;

public class PMAppInfo {
    private String appLabel;
    private Drawable appIcon;
    private String pkgName;
    private String appEnterClass;

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
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

    public String getAppEnterClass() {
        return appEnterClass;
    }

    public void setAppEnterClass(String appEnterClass) {
        this.appEnterClass = appEnterClass;
    }
}
