package com.kamimi.lcalendar.view;

import com.kamimi.lcalendar.obj.Day;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DayManager {

    /**
     * 记录当前的时间
     */
    public static String currentTime;

    /**
     * 当前的日期
     */
    private static int current = -1;

    /**
     * 储存当前的日期
     */
    private static int tempCurrent = -1;

    /**
     * 储存休息天数
     */
    static Set<Integer> restDays = new HashSet<>();

    /**
     * 选中的天
     */
    private static int select = -1;

    /**
     * 常量
     */
    static String[] weekArray = {"日", "一", "二", "三", "四", "五", "六"};
    static String[] dayArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

    public static void setCurrentTime(String currentTime) {
        DayManager.currentTime = currentTime;
    }

    public static String getCurrentTime() {
        return currentTime;
    }

    public static void setCurrent(int current) {
        DayManager.current = current;
    }

    public static void setTempCurrent(int tempCurrent) {
        DayManager.tempCurrent = tempCurrent;
    }

    public static int getTempCurrent() {
        return tempCurrent;
    }

    public static void setSelect(int select) {
        DayManager.select = DayManager.select == select ? -1 : select;
    }

    /**
     * 根据日历对象创建日期集合
     *
     * @param calendar 日历
     * @param width    控件的宽度
     * @param height   控件的高度
     * @return 返回的天数的集合
     */
    public static List<Day> createDayByCalendar(Calendar calendar, int width, int height, boolean drawOtherDay) {
        //初始化休息的天数
        initRestDays(calendar);

        List<Day> days = new ArrayList<>();
        Day day;
        int dayWidth = width / 7;
        int dayHeight = height / (calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1);
        //添加星期标识，
        for (int i = 0; i < 7; i++) {
            day = new Day(dayWidth, dayHeight);
            //为星期设置位置，为第0行，
            day.locationX = i;
            day.locationY = 0;
            day.text = weekArray[i];
            //设置日期颜色
            day.textColor = 0xFF699CF0;
            days.add(day);
        }
        int count = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstWeekCount = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //添加上一个月的天数
        if (drawOtherDay) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            int preCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 0; i < firstWeekCount; i++) {
                day = new Day(dayWidth, dayHeight);
                day.text = dayArray[preCount - firstWeekCount + i];
                day.locationX = i;
                day.locationY = 1;
                day.textColor = 0xffaaaaaa;
                day.isCurrent = false;
                day.dateText = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + day.text;
                days.add(day);
            }
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }

        //生成每一天的对象，其中第i次创建的是第i+1天
        for (int i = 0; i < count; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];
            calendar.set(Calendar.DAY_OF_MONTH, i + 1);
            //设置每个天数的位置
            day.locationY = calendar.get(Calendar.WEEK_OF_MONTH);
            day.locationX = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            day.dateText = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + day.text;
            //设置日期选择状态
            if (i == current - 1) {
                day.backgroundStyle = 3;
                day.textColor = 0xFF4384ED;
            } else if (i == select - 1) {
                day.backgroundStyle = 2;
                day.textColor = 0xFFFAFBFE;
            } else {
                day.backgroundStyle = 1;
                day.textColor = 0xFF8696A5;
            }
            days.add(day);
        }

        //添加下一个月的天数
        int lastCount = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < 7 - lastCount; i++) {
            day = new Day(dayWidth, dayHeight);
            day.text = dayArray[i];
            //设置每个天数的位置
            day.locationY = calendar.get(Calendar.WEEK_OF_MONTH);
            day.locationX = lastCount + i;
            day.isCurrent = false;
            day.textColor = 0xffaaaaaa;
            day.dateText = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 2) + "-" + day.text;
            days.add(day);
        }

        return days;
    }

    /**
     * 初始化休息的天数  计算休息的天数
     */
    private static void initRestDays(Calendar calendar) {
        int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < total; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i + 1);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                restDays.add(i + 1);
            }
        }
    }

}
