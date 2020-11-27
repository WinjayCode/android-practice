package com.winjay.practice.ui.view;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winjay.practice.R;

/**
 * Kui卡片识别区
 *
 * @author Winjay
 * @date 2018/10/19
 */
public class RecognitionView extends RelativeLayout {
    private static final String TAG = RecognitionView.class.getSimpleName();
    private Context mContext;
    /**
     * 识别文字
     */
    private TextView mRecognitionTv;

    public RecognitionView(Context context) {
        this(context, null);
    }

    public RecognitionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecognitionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.kui_ui_card_recognition, this);
        findView();
    }

    private void findView() {
        mRecognitionTv = (TextView) findViewById(R.id.kui_ui_recognition_tv);
        mRecognitionTv.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * 设置识别区内容
     *
     * @param content 识别内容
     */
    public void setRecognitionContent(String content) {
        mRecognitionTv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mRecognitionTv.setText(content);
    }

    /**
     * 设置TTS内容
     *
     * @param content TTS播报内容
     */
    public void setTTSContent(String content) {
        mRecognitionTv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mRecognitionTv.setText(content);
    }

}
