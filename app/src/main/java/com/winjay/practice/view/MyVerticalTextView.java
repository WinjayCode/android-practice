package com.winjay.practice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

import com.winjay.practice.R;

/**
 * 唐诗考评竖向文字view
 *
 * @author Winjay
 * @date 2020/5/27
 */
public class MyVerticalTextView extends AppCompatTextView {
    private static String TAG = MyVerticalTextView.class.getSimpleName();
    private Context mContext;
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
        mContext = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
        mLineSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_lineSpacingExtra, 0);
        mCharSpacingExtra = mTypedArray.getDimension(R.styleable.VerticalTextView_charSpacingExtra, 0);
        mTypedArray.recycle();
    }

    public MyVerticalTextView setLineMaxCharNum(int num) {
        Log.d(TAG, "setLineMaxCharNum()_num=" + num);
        mLineMaxCharNum = num;
        return this;
    }

    public MyVerticalTextView setMaxLine(int num) {
        Log.d(TAG, "setMaxLine()_num=" + num);
        mMaxLine = num;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (getTextSize() * mMaxLine + mLineSpacingExtra);
        int height = measureHeight();
        setMeasuredDimension(width, height);
    }

    private int measureHeight() {
        return (int) (getTextSize() * mLineMaxCharNum + (mLineMaxCharNum - 1) * mCharSpacingExtra);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint = getTextPaint();
        Log.d(TAG, "text=" + getText());
        int textStrLength = getText().length();
        String content = getText().toString();
        if (textStrLength == 0) {
            return;
        }
        // 单列
        if (mMaxLine == 1) {
            float currentLineOffsetY = getTextSize();
            for (int i = 0; i < textStrLength; i++) {
                String char_i = String.valueOf(content.charAt(i));
                canvas.drawText(char_i, 0, currentLineOffsetY, textPaint);
                currentLineOffsetY += getTextSize() + mCharSpacingExtra;
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
            float currentLineOffsetY = getTextSize();

            for (int j = 0; j < textStrLength; j++) {
                String char_j = String.valueOf(content.charAt(j));

                boolean isCurrentLineFinish = ((j + 1) % (mLineMaxCharNum + 1) == 0);

                if (isCurrentLineFinish) {
                    currentLineOffsetX = 0;
                    currentLineOffsetY = getTextSize();
                }

                canvas.drawText(char_j, currentLineOffsetX, currentLineOffsetY, textPaint);
                currentLineOffsetY += getTextSize() + mCharSpacingExtra;
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
}
