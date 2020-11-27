package com.winjay.practice.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.winjay.practice.R;
import com.winjay.practice.utils.LogUtil;

/**
 * 唐诗考评竖向文字view
 *
 * @author Winjay
 * @date 2020/5/27
 */
public class MyVerticalTextView extends AppCompatTextView {
    private static String TAG = MyVerticalTextView.class.getSimpleName();
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

    public MyVerticalTextView(Context context) {
        this(context, null);
    }

    public MyVerticalTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
        mLineSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_lineSpacingExtra, 0);
        mCharSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_charSpacingExtra, 0);
        mTypedArray.recycle();
    }

    public void setLineMaxCharNum(int num) {
        LogUtil.d(TAG, "setLineMaxCharNum()_num=" + num);
        mLineMaxCharNum = num;
    }

    public void setMaxLine(int num) {
        LogUtil.d(TAG, "setMaxLine()_num=" + num);
        mMaxLine = num;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(), measureHeight());
    }

    private int measureWidth() {
        return (int) (getTextSize() * mMaxLine + mLineSpacingExtra);
    }

    private int measureHeight() {
        return (int) ((Math.abs(getPaint().getFontMetricsInt().top) + getPaint().getFontMetricsInt().bottom) * mLineMaxCharNum + (mLineMaxCharNum - 1) * mCharSpacingExtra);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint = getTextPaint();
//        TLog.d(TAG, "text=" + getText());
        int textStrLength = getText().length();
        String content = getText().toString();
        if (textStrLength == 0) {
            return;
        }
        // 单列
        if (mMaxLine == 1) {
            float currentLineOffsetY = Math.abs(getPaint().getFontMetricsInt().top);
            for (int i = 0; i < textStrLength; i++) {
                String char_i = String.valueOf(content.charAt(i));
                canvas.drawText(char_i, 0, currentLineOffsetY, textPaint);
                currentLineOffsetY += getPaint().getFontMetricsInt().bottom + mCharSpacingExtra + Math.abs(getPaint().getFontMetricsInt().top);
            }
        }
        // 多列
        else {
            if (textStrLength > mMaxLine * mLineMaxCharNum) {
                content = content.substring(textStrLength - 2 * mLineMaxCharNum, textStrLength);
                content = content.replaceFirst(content.substring(0, 3), "...");
                textStrLength = mMaxLine * mLineMaxCharNum;
            }

            float currentLineOffsetX = getTextSize() + mLineSpacingExtra;
            float currentLineOffsetY = Math.abs(getPaint().getFontMetricsInt().top);

            for (int j = 0; j < textStrLength; j++) {
                String char_j = String.valueOf(content.charAt(j));

                boolean isCurrentLineFinish = ((j + 1) % (mLineMaxCharNum + 1) == 0);

                if (isCurrentLineFinish) {
                    currentLineOffsetX = 0;
                    currentLineOffsetY = Math.abs(getPaint().getFontMetricsInt().top);
                }

                canvas.drawText(char_j, currentLineOffsetX, currentLineOffsetY, textPaint);
                currentLineOffsetY += getPaint().getFontMetricsInt().bottom + mCharSpacingExtra + Math.abs(getPaint().getFontMetricsInt().top);
            }
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
