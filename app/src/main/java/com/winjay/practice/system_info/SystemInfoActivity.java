package com.winjay.practice.system_info;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import butterknife.BindView;

/**
 * 系统信息
 *
 * @author Winjay
 * @date 2020/9/8
 */
public class SystemInfoActivity extends BaseActivity {
    @BindView(R.id.system_info_tv)
    AppCompatTextView system_info_tv;

    @Override
    protected int getLayoutId() {
        return R.layout.system_info_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        system_info_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        getSystemInfo();
    }

    private void getSystemInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("========android.os.Build========" + "\n");
        // 主板
        stringBuilder.append("board: " + Build.BOARD + "\n");
        // Android系统定制商
        stringBuilder.append("brand: " + Build.BRAND + "\n");
        // CPU指令集
        stringBuilder.append("supported_abis: " + Build.SUPPORTED_ABIS + "\n");
        // 设备参数
        stringBuilder.append("device: " + Build.DEVICE + "\n");
        // 显示屏参数
        stringBuilder.append("display: " + Build.DISPLAY + "\n");
        // 唯一编号
        stringBuilder.append("fingerprint: " + Build.FINGERPRINT + "\n");
        // 硬件序列号
        stringBuilder.append("serial: " + Build.SERIAL + "\n");
        // 修订版本列表
        stringBuilder.append("id: " + Build.ID + "\n");
        // 硬件制造商
        stringBuilder.append("manufacturer: " + Build.MANUFACTURER + "\n");
        // 版本
        stringBuilder.append("model: " + Build.MODEL + "\n");
        // 硬件名
        stringBuilder.append("hardware: " + Build.HARDWARE + "\n");
        // 手机产品名
        stringBuilder.append("product: " + Build.PRODUCT + "\n");
        // 描述Build的标签
        stringBuilder.append("tags: " + Build.TAGS + "\n");
        // Builder类型
        stringBuilder.append("type: " + Build.TYPE + "\n");
        // 当前开发代号
        stringBuilder.append("codename: " + Build.VERSION.CODENAME + "\n");
        // 源码控制版本号
        stringBuilder.append("incremental: " + Build.VERSION.INCREMENTAL + "\n");
        // 版本字串符
        stringBuilder.append("release: " + Build.VERSION.RELEASE + "\n");
        // 版本号
        stringBuilder.append("sdk_int: " + Build.VERSION.SDK_INT + "\n");
        // Host值
        stringBuilder.append("host: " + Build.HOST + "\n");
        // User名
        stringBuilder.append("user: " + Build.USER + "\n");
        // 编译时间
        stringBuilder.append("time: " + Build.TIME + "\n\n");

        stringBuilder.append("========SystemProperty========" + "\n");
        // OS版本
        stringBuilder.append("os.version: " + System.getProperty("os.version") + "\n");
        // OS名称
        stringBuilder.append("os.name: " + System.getProperty("os.name") + "\n");
        // OS架构
        stringBuilder.append("os.arch: " + System.getProperty("os.arch") + "\n");
        // Home属性
        stringBuilder.append("user.home: " + System.getProperty("user.home") + "\n");
        // Name属性
        stringBuilder.append("user.name: " + System.getProperty("user.name") + "\n");
        // Dir属性
        stringBuilder.append("user.dir: " + System.getProperty("user.dir") + "\n");
        // 时区
        stringBuilder.append("user.timezone: " + System.getProperty("user.timezone") + "\n");
        // 路径分隔符
        stringBuilder.append("path.separator: " + System.getProperty("path.separator") + "\n");
        // 行分隔符
        stringBuilder.append("line.separator: " + System.getProperty("line.separator") + "\n");
        // 文件分隔符
        stringBuilder.append("file.separator: " + System.getProperty("file.separator") + "\n");
        // Java vender URL属性
        stringBuilder.append("java.vendor.url: " + System.getProperty("java.vendor.url") + "\n");
        // Java Class路径
        stringBuilder.append("java.class.path: " + System.getProperty("java.class.path") + "\n");
        // Java Class版本
        stringBuilder.append("java.class.version: " + System.getProperty("java.class.version") + "\n");
        // Java Vendor属性
        stringBuilder.append("java.vendor: " + System.getProperty("java.vendor") + "\n");
        // Java 版本
        stringBuilder.append("java.version: " + System.getProperty("java.version") + "\n");
        // Java Home属性
        stringBuilder.append("java.home: " + System.getProperty("java.home") + "\n");

        system_info_tv.setText(stringBuilder.toString());
    }
}
