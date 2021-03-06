package com.kamimi.lcalendar.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.kamimi.lcalendar.obj.Day;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.utils.CommonUtils;
import com.kamimi.lcalendar.utils.SharedPreferencesLoader;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CalendarView extends View {

    /**
     * 画笔
     */
    private Paint paint;
    /***
     * 当前的时间
     */
    private Calendar calendar;
    /**
     * 选中监听
     */
    private OnSelectChangeListener listener;
    /**
     * 是否在本月里画其他月的日子
     */
    private boolean drawOtherDays = true;
    /**
     * 绘制月回调
     */
    private OnDrawMonth onDrawMonth;
    /**
     * 绘制天回调
     */
    private OnDrawDay onDrawDay;

    public CalendarView(Context context) {
        super(context);
        initView();
    }


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /***
     * 初始化控件
     */
    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        calendar = Calendar.getInstance(Locale.CHINA);
        DayManager.setCurrent(calendar.get((Calendar.DAY_OF_MONTH)));
        DayManager.setTempCurrent(calendar.get(Calendar.DAY_OF_MONTH));
        DayManager.setCurrentTime(calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.YEAR));
    }

    /**
     * 改变日期，并更改当前状态，由于绘图是在calendar基础上进行绘制的，所以改变calendar就可以改变图片
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        if ((calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.YEAR)).equals(DayManager.getCurrentTime())) {
            DayManager.setCurrent(DayManager.getTempCurrent());
        } else {
            DayManager.setCurrent(-1);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (onDrawMonth != null) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            onDrawMonth.drawMonth(year, month, canvas, paint);
        }
        List<Day> days = DayManager.createDayByCalendar(calendar, getMeasuredWidth(), getMeasuredHeight(), drawOtherDays);
        for (Day day : days) {
            canvas.save();
            canvas.translate(day.locationX * day.width, day.locationY * day.height);
            if (this.onDrawDay == null || !onDrawDay.drawDay(day, canvas, paint)) {
                day.drawDay(canvas, paint);
            }
            if (this.onDrawDay != null) {
                onDrawDay.drawDayAbove(day, canvas, paint);
            }
            canvas.restore();
        }
    }

    private enum MotionType {
        /**
         * 左滑
         */
        LEFT,
        /**
         * 右滑
         */
        RIGHT,
        /**
         * 无滑动
         */
        NONE
    }

    private volatile long downTime;
    private volatile float posX, posY, curPosX;
    private volatile ScheduledFuture<?> drawHeartFuture;

    private static final long DRAW_HEART_HOLD_TIME = 500;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按压时间
                downTime = System.currentTimeMillis();
                //点击位置
                float x = event.getX();
                float y = event.getY();
                //暂存按下位置
                posX = x;
                posY = y;
                curPosX = x;
                //半秒后画个心
                if (drawHeartFuture == null) {
                    drawHeartFuture = delayDrawHeart(event);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (drawHeartFuture != null
                        && (Math.abs(event.getX() - posX) > 10 || Math.abs(event.getY() - posY) > 10)) {
                    //手指动了 取消画心
                    drawHeartFuture.cancel(false);
                    drawHeartFuture = null;
                }
                curPosX = event.getX();
                return true;
            case MotionEvent.ACTION_UP:
                if (drawHeartFuture != null) {
                    //手指抬起了 取消画心
                    drawHeartFuture.cancel(false);
                    drawHeartFuture = null;
                }
                MotionType action = MotionType.NONE;
                if (curPosX - posX > 100) {
                    action = MotionType.LEFT;
                } else if (curPosX - posX < -100) {
                    action = MotionType.RIGHT;
                }
                boolean dragTriggered = dragEvent(action);
                if (!dragTriggered) {
                    // 选日期事件
                    return chooseEvent(event);
                }
            default:
                return true;
        }
    }

    private ScheduledFuture<?> delayDrawHeart(MotionEvent event) {
        // 按住了delay时间以上，画爱心/取消
        float x = event.getX();
        float y = event.getY();
        return CommonUtils.submitDelay(() -> {
            //判断点击的是哪个日期
            Calendar newCalendar = getCalendarByPosition(x, y);
            if (newCalendar == null) {
                return;
            }
            String day = CommonUtils.dateFormat(newCalendar.getTime(), "yyyy-M-d");
            SharedPreferences.Editor editor = SharedPreferencesLoader.markSp.edit();
            if (SharedPreferencesLoader.markSp.contains(day)) {
                editor.remove(day);
            } else {
                editor.putBoolean(day, true);
            }
            editor.commit();
            invalidate();
        }, DRAW_HEART_HOLD_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 选中操作
     */
    private boolean chooseEvent(MotionEvent event) {
        // 点击就一瞬间，太长的认为不是点击事件
        if (System.currentTimeMillis() - downTime > 150) {
            return super.onTouchEvent(event);
        }
        //判断点击的是哪个日期
        Calendar cc = getCalendarByPosition(event.getX(), event.getY());
        if (cc == null) {
            return super.onTouchEvent(event);
        }
        DayManager.setSelect(cc.get(Calendar.DAY_OF_MONTH));
        if (listener != null) {
            listener.selectChange(this, cc.getTime());
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 拖拽操作
     */
    private boolean dragEvent(MotionType action) {
        Calendar cc = (Calendar) calendar.clone();
        if (action == MotionType.LEFT) {
            cc.set(Calendar.DAY_OF_MONTH, 1);
            cc.add(Calendar.MONTH, -1);
        } else if (action == MotionType.RIGHT) {
            cc.set(Calendar.DAY_OF_MONTH, 1);
            cc.add(Calendar.MONTH, 1);
        } else {
            return false;
        }
        this.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.hide));
        DayManager.setSelect(-1);
        this.setCalendar(cc);
        this.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.show));
        return true;
    }

    /**
     * 根据点击位置设置日期
     */
    private Calendar getCalendarByPosition(float x, float y) {
        Calendar cc = (Calendar) calendar.clone();
        int locationX = (int) (x * 7 / getMeasuredWidth());
        int locationY = (int) ((cc.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1) * y / getMeasuredHeight());
        if (locationY == 0) {
            return null;
        } else if (locationY == 1) {
            cc.set(Calendar.DAY_OF_MONTH, 1);
            if (locationX < cc.get(Calendar.DAY_OF_WEEK) - 1) {
                return null;
            }
        } else if (locationY == cc.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
            cc.set(Calendar.DAY_OF_MONTH, cc.getActualMinimum(Calendar.DAY_OF_MONTH));
            if (locationX > cc.get(Calendar.DAY_OF_WEEK) + 1) {
                return null;
            }
        }
        cc.set(Calendar.WEEK_OF_MONTH, (int) locationY);
        cc.set(Calendar.DAY_OF_WEEK, (int) (locationX + 1));
        return cc;
    }

    /**
     * 是否画本月外其他日子
     *
     * @param drawOtherDays true 表示画，false表示不画 ，默认为true
     */
    public void setDrawOtherDays(boolean drawOtherDays) {
        this.drawOtherDays = drawOtherDays;
        invalidate();
    }

    /**
     * 设置日期选择改变监听
     */
    public void setOnSelectChangeListener(OnSelectChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 设置画天数回调
     */
    public void setOnDrawMonth(OnDrawMonth onDrawMonth) {
        this.onDrawMonth = onDrawMonth;
    }

    /**
     * 设置画天数回调
     */
    public void setOnDrawDay(OnDrawDay onDrawDay) {
        this.onDrawDay = onDrawDay;
    }

    /**
     * 日期选择改变监听的接口
     */
    public interface OnSelectChangeListener {
        void selectChange(CalendarView calendarView, Date date);
    }

    /**
     * 画天数回调（每画一天执行一次）
     */
    public interface OnDrawDay {
        /**
         * 层次在原画下
         *
         * @return true-覆盖默认的画面，false-不覆盖
         */
        default boolean drawDay(Day day, Canvas canvas, Paint paint) {
            return false;
        }

        /**
         * 层次在原画上
         */
        default void drawDayAbove(Day day, Canvas canvas, Paint paint) {
        }
    }

    /**
     * 画月份回调（每画一个月执行一次）
     */
    public interface OnDrawMonth {
        void drawMonth(int year, int month, Canvas canvas, Paint paint);
    }

}
