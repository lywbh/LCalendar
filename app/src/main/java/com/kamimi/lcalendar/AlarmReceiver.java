package com.kamimi.lcalendar;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.NotificationUtils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("NOTIFICATION")) {
            NotificationData data = JSONObject.parseObject(intent.getStringExtra("notificationData"), NotificationData.class);
            Intent nextIntent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationUtils.push(data.getId(), context, NotificationUtils.paramBuilder()
                    .icon(R.mipmap.heart)
                    .title(data.getTitle())
                    .content(data.getContent())
                    .subText("您有新的日程需要处理~")
                    .ticker("您有新的日程需要处理~")
                    .pi(pi)
                    .build()
            );
            // TODO 铃声如何不循环 & APP被杀死怎么办 & intent怎么传参能让APP打开以后页面直接跳到第三个fragment
            Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
            MediaPlayer mediaPlayer = MediaPlayer.create(context, uri);
            mediaPlayer.start();
        }

    }

}
