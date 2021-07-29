package com.kamimi.lcalendar.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class NotifyObject {

    @DrawableRes
    private final int icon;
    private final String title;
    private final String subText;
    private final String content;
    private final String ticker;
    private final android.app.Notification.Style style;
    private final NotificationCompat.Style oldStyle;
    private final PendingIntent pi;

}

/**
 * 消息通知工具
 */
public class NotificationUtils {

    private static final String CHANNEL_ID = "LCalendar";

    public static NotifyObject.NotifyObjectBuilder paramBuilder() {
        return NotifyObject.builder();
    }

    /**
     * 推送通知
     */
    public static void push(int nid, Context context, NotifyObject obj) {
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 兼容Android8.0
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "notice", NotificationManager.IMPORTANCE_LOW);
            mChannel.enableLights(true);
            mChannel.setDescription("LCalendar push channel");
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotifyMgr.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setContentTitle(obj.getTitle())
                    .setSubText(obj.getSubText())
                    .setContentText(obj.getContent())
                    .setTicker(obj.getTicker())
                    .setStyle(obj.getStyle())
                    .setOngoing(false)
                    .setSmallIcon(obj.getIcon())
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(obj.getPi())
                    .build();
        } else {
            // 8.0以下
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setContentTitle(obj.getTitle())
                    .setSubText(obj.getSubText())
                    .setContentText(obj.getContent())
                    .setTicker(obj.getTicker())
                    .setStyle(obj.getOldStyle())
                    .setOngoing(false)
                    .setSmallIcon(obj.getIcon())
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(obj.getPi())
                    .build();
        }
        mNotifyMgr.notify(nid, notification);
    }

}