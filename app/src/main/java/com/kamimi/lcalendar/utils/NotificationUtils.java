package com.kamimi.lcalendar.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import lombok.Builder;
import lombok.Getter;

/**
 * 消息通知工具
 */
public class NotificationUtils {

    @Getter
    @Builder
    public static class NotifyObject {

        @DrawableRes
        private final int icon;                         // 小图标
        private final long when;                        // 通知时间
        private final String title;                     // 标题
        private final String subText;                   // 小标题
        private final String content;                   // 内容
        private final String ticker;                    // 提示语
        private final NotificationCompat.Style style;   // 样式
        private final PendingIntent pi;                 // 要执行的动作

    }

    private static final String CHANNEL_ID = "LCalendar";

    private final Context context;
    private final NotificationManager mNotifyMgr;

    private NotificationUtils(Context context) {
        this.context = context;
        this.mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Android.O及以上版本需要手动设置channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "日程提醒", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("新日程通知");
            mChannel.enableLights(true);
            mChannel.setSound(null, null);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotifyMgr.createNotificationChannel(mChannel);
        }
    }

    /**
     * 获取实例
     */
    public static NotificationUtils init(Context context) {
        return new NotificationUtils(context);
    }

    /**
     * 参数构造器
     */
    public static NotifyObject.NotifyObjectBuilder paramBuilder() {
        return NotifyObject.builder();
    }

    /**
     * 推送通知
     */
    public void push(int nid, NotifyObject obj) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(obj.getIcon())
                .setWhen(obj.getWhen())
                .setContentTitle(obj.getTitle())
                .setSubText(obj.getSubText())
                .setContentText(obj.getContent())
                .setTicker(obj.getTicker())
                .setStyle(obj.getStyle())
                .setContentIntent(obj.getPi())
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setNumber(1)
                .build();
        mNotifyMgr.notify(nid, notification);
    }

}