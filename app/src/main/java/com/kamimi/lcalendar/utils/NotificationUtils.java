package com.kamimi.lcalendar.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

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
    private final NotificationCompat.Style style;
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
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(obj.getIcon())
                .setContentTitle(obj.getTitle())
                .setSubText(obj.getSubText())
                .setContentText(obj.getContent())
                .setTicker(obj.getTicker())
                .setStyle(obj.getStyle())
                .setContentIntent(obj.getPi())
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .build();
        mNotifyMgr.notify(nid, notification);
    }

}