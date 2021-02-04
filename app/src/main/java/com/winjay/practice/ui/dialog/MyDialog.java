package com.winjay.practice.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.winjay.practice.R;
import com.winjay.practice.utils.ToastUtils;

public class MyDialog extends Dialog {
    private static final String TAG = MyDialog.class.getSimpleName();

    public MyDialog(@NonNull Context context) {
        super(context, R.style.Dialog_Fullscreen);
//        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.my_dialog);

        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.END);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = getScreenSize()[1];

        lp.x = 20;
        lp.y = 50;

        window.setAttributes(lp);


//        Window window = getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.TOP | Gravity.END);
////            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////            window.getDecorView().setPadding(0, 0, 0, 0);
////            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
//            WindowManager.LayoutParams layoutParams = window.getAttributes();
//            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                // 延伸显示区域到刘海
//                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//                window.setAttributes(layoutParams);
//                // 设置页面全屏显示
//                View decorView = window.getDecorView();
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            }
//            window.setAttributes(layoutParams);
//        }


        Button button = findViewById(R.id.click_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getContext(), "点不到点不到");
            }
        });

//        hideSystemUI(button);
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    private int[] getScreenSize() {
        int[] size = new int[2];
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        size[0] = outMetrics.widthPixels;
        Log.d(TAG, "width=" + size[0]);
        size[1] = outMetrics.heightPixels;
        Log.d(TAG, "height=" + size[1]);
        return size;
    }

    // 显示在状态栏下面，隐藏导航栏
    public void hideSystemUI(View view) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            );
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    // 隐藏状态栏和导航栏
    public void hideSystemUI2(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

}
