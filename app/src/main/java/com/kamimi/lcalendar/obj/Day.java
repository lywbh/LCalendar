package com.kamimi.lcalendar.obj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Day {
    /**
     * 单个日期格子的宽
     */
    public int width;
    /**
     * 单个日期格子的高
     */
    public int height;
    /**
     * 一月的第几天的文本
     */
    public String text;
    /**
     * 文本字体的颜色
     */
    public int textColor;
    /**
     * 当天年月日的文本
     */
    public String dateText = "-1";
    /**
     * 日期背景的类型 0代表无任何背景，1代表正常打卡，2代表所选日期，3代表当前日期 4,代表即是当前日期，也被选中
     */
    public int backgroundStyle;
    /**
     * 字体的大小
     */
    public float textSize;
    /**
     * 背景的半径
     */
    public int backgroundR;
    /**
     * 字体在第几行
     */
    public int locationX;
    /**
     * 字体在第几列
     */
    public int locationY;
    /**
     * 是否是本月
     */
    public boolean isCurrent = true;

    /**
     * 创建日期对象
     *
     * @param width  每个日期格子的宽度
     * @param height 每个日期格子的高度
     */
    public Day(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 画天数
     *
     * @param canvas  要画的画布
     * @param paint   画笔
     * @param context 画布的上下文对象
     */
    public void drawDays(Canvas canvas, Context context, Paint paint) {
        //取窄的边框为圆的半径
        backgroundR = Math.min(width, height);
        //画背景
        drawBackground(canvas, paint);
        //画数字
        drawText(canvas, paint);
    }

    /**
     * 画数字
     */
    private void drawText(Canvas canvas, Paint paint) {
        //根据圆的半径设置字体的大小
        textSize = (float) backgroundR / 3;
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        //计算文字的宽度
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        int w = rect.width();
        //计算画文字的位置
        float x = (float) (width - w) / 2;
        float y = (height + textSize / 2) / 2;
        canvas.drawText(text, x, y, paint);
    }

    /**
     * 画背景
     */
    private void drawBackground(Canvas canvas, Paint paint) {
        //画背景 根据背景状态设置画笔类型
        if (backgroundStyle == 0) {
            return;
        }
        switch (backgroundStyle) {
            case 1:
                paint.setColor(0xFFECF1F4);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 2:
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xFF457BF4);
                break;
            case 3:
                paint.setColor(0xFF457BF4);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                break;
            default:
                break;
        }
        //计算圆心的位置
        float cx = (float) width / 2;
        float cy = (float) height / 2;
        canvas.drawCircle(cx, cy, (float) backgroundR * 9 / 20, paint);
    }

}
