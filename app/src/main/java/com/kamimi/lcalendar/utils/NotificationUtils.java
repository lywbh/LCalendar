package com.kamimi.lcalendar.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class NotifyObject {

    private final String title;
    private final String subText;
    private final String content;
    @DrawableRes
    private final int icon;

}

/**
 * 消息通知工具
 */
public class NotificationUtils {

    /**
     * 推送通知
     */
    private static void push(Context context, Class<Activity> activityClass, NotifyObject obj, int nid) {
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, activityClass);
        Notification notification;
        PendingIntent pi = PendingIntent.getActivity(context, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = "LCalendar";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 兼容Android8.0
            NotificationChannel mChannel = new NotificationChannel(channelId, "notice", NotificationManager.IMPORTANCE_LOW);
            mChannel.enableLights(true);
            mChannel.setDescription("LCalendar push channel");
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotifyMgr.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context, channelId)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.getTitle())
                    .setSubText(obj.getSubText())
                    .setContentText(obj.getContent())
                    .setOngoing(false)
                    .setSmallIcon(obj.getIcon())
                    .setWhen(System.currentTimeMillis())
                    .build();
        } else {
            // 8.0以下
            notification = new NotificationCompat.Builder(context, channelId).setAutoCancel(true)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.getTitle())
                    .setSubText(obj.getSubText())
                    .setContentText(obj.getContent())
                    .setOngoing(false)
                    .setSmallIcon(obj.getIcon())
                    .setWhen(System.currentTimeMillis())
                    .build();
        }
        mNotifyMgr.notify(nid, notification);
    }

}