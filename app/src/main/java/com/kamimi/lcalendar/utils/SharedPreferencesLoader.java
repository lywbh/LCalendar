package com.kamimi.lcalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesLoader {

    /**
     * 自定义标记数据库
     */
    public static SharedPreferences markSp;

    /**
     * 日记数据库
     */
    public static SharedPreferences diarySp;

    /**
     * 日程数据库
     */
    public static SharedPreferences notificationSp;

    public static void load(Context context) {
        markSp = context.getSharedPreferences("LCalendarMarkSp", Context.MODE_PRIVATE);
        diarySp = context.getSharedPreferences("LCalendarDiarySp", Context.MODE_PRIVATE);
        notificationSp = context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
    }

}
