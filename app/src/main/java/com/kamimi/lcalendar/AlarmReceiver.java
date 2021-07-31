package com.kamimi.lcalendar;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSONObject;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.CommonUtils;
import com.kamimi.lcalendar.utils.NotificationUtils;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_DATA_NAME = "notificationData";

    private static final String NOTIFICATION_HINT_TEXT = "您有新的日程需要处理";

    private NotificationUtils notificationUtils;

    private Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationData data = JSONObject.parseObject(intent.getStringExtra(ALARM_DATA_NAME), NotificationData.class);
        // 通知时间
        String dateTimeStr = String.format("%s %s:00", data.getDate(), data.getNotifyTime());
        Date notifyTime = CommonUtils.parseDate(dateTimeStr, "yyyy-M-d HH:mm:ss");
        // 创建展开通知明细
        NotificationCompat.BigTextStyle mBigTextStyle = new NotificationCompat.BigTextStyle();
        mBigTextStyle.setBigContentTitle(data.getTitle());
        mBigTextStyle.setSummaryText(NOTIFICATION_HINT_TEXT);
        mBigTextStyle.bigText(data.getContent());
        // 创建跳转动作
        Intent nextIntent = new Intent(context, MainActivity.class)
                .putExtra(MainActivity.START_PAGE_NAME, R.id.navigation_notifications);
        PendingIntent pi = PendingIntent.getActivity(context, data.getId(), nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 构造参数
        final NotificationUtils.NotifyObject params = NotificationUtils.paramBuilder()
                .icon(R.mipmap.heart)
                .when(notifyTime.getTime())
                .title(data.getTitle())
                .content(data.getContent())
                .subText(NOTIFICATION_HINT_TEXT)
                .ticker(NOTIFICATION_HINT_TEXT)
                .style(mBigTextStyle)
                .pi(pi)
                .build();
        // 发送消息
        if (notificationUtils == null) {
            notificationUtils = NotificationUtils.init(context);
        }
        notificationUtils.push(data.getId(), params);
        // 播放声音
        if (ringtone == null) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(context, uri);
        }
        ringtone.play();
    }

}
