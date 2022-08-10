package com.winjay.practice.hardware_test.multi_touch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class TouchCircleView {
    public float x;           //圆心点的x坐标
    public float y;           //圆心点的y坐标
    public int r = 100;         //圆的半径
    public int pointId;      //员的下标
    //初始化颜色
    int red;
    int green;
    int blue;
    Random random = new Random();//初始化随机数

    public TouchCircleView(float x, float y, int pointId) {
        this.x = x;
        this.y = y;
        this.pointId = pointId;
        red = random.nextInt(255);
        green = random.nextInt(255);
        blue = random.nextInt(255);
    }

    public void drawSelf(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(red, green, blue));
        canvas.drawCircle(x, y, r, paint);
    }
}
