package com.winjay.practice.ui.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

/**
 * 古诗竖向文字view
 *
 * @author Winjay
 * @date 2020/5/27
 */
public class AncientPoetryTextView extends AppCompatTextView {
    private static String TAG = AncientPoetryTextView.class.getSimpleName();
    /**
     * 每列最多显示文字个数
     */
    private int mLineMaxCharNum = 0;
    /**
     * 最大列数
     */
    private int mMaxLine = 0;
    /**
     * 字符间距
     */
    private float mCharSpacingExtra;
    /**
     * 列间距
     */
    private float mLineSpacingExtra;

    public AncientPoetryTextView(Context context) {
        this(context, null);
    }

    public AncientPoetryTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AncientPoetryTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
        mLineSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_lineSpacingExtra, 0);
        mCharSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_charSpacingExtra, 0);
        mTypedArray.recycle();

        AssetManager mgr = context.getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/new.ttf");
        setTypeface(tf);
    }

    public void setLineMaxCharNum(int num) {
        LogUtil.d(TAG, "num=" + num);
        mLineMaxCharNum = num;
    }

    public void setMaxLine(int num) {
        LogUtil.d(TAG, "num=" + num);
        mMaxLine = num;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtil.d(TAG, "width=" + measureWidth());
        setMeasuredDimension(measureWidth(), measureHeight());
    }

    private int measureWidth() {
        return (int) (getTextSize() * mMaxLine + mLineSpacingExtra * (mMaxLine - 1));
    }

    private int measureHeight() {
        return (int) ((Math.abs(getPaint().getFontMetricsInt().top) + getPaint().getFontMetricsInt().bottom) * mLineMaxCharNum + (mLineMaxCharNum - 1) * mCharSpacingExtra);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint = getTextPaint();
        LogUtil.d(TAG, "text=" + getText());
        int textStrLength = getText().length();
        String content = getText().toString();
        if (textStrLength == 0) {
            return;
        }

        float currentLineOffsetX = (getTextSize() + mLineSpacingExtra) * (mMaxLine - 1);
        float currentLineOffsetY = Math.abs(getPaint().getFontMetricsInt().top);

        for (int j = 0; j < textStrLength; j++) {
            String char_j = String.valueOf(content.charAt(j));

            if (j != 0) {
                boolean isCurrentLineFinish = (j % mLineMaxCharNum == 0);

                if (isCurrentLineFinish) {
                    currentLineOffsetX = currentLineOffsetX - getTextSize() - mLineSpacingExtra;
                    currentLineOffsetY = Math.abs(getPaint().getFontMetricsInt().top);
                }
            }

            canvas.drawText(char_j, currentLineOffsetX, currentLineOffsetY, textPaint);
            currentLineOffsetY += getPaint().getFontMetricsInt().bottom + mCharSpacingExtra + Math.abs(getPaint().getFontMetricsInt().top);
        }
    }

    /**
     * 文字画笔
     *
     * @return
     */
    private TextPaint getTextPaint() {
        // 文字画笔
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        return textPaint;
    }

    public void setText(String text) {
        super.setText(text);
        requestLayout();
        invalidate();
    }
}
