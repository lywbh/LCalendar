package com.kamimi.lcalendar;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.NotificationUtils;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_DATA_NAME = "notificationData";

    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onReceive(Context context, Intent intent) {
        // 发送通知
        NotificationData data = JSONObject.parseObject(intent.getStringExtra(ALARM_DATA_NAME), NotificationData.class);
        Intent nextIntent = new Intent(context, MainActivity.class);
        nextIntent.putExtra(MainActivity.START_PAGE_NAME, R.id.navigation_notifications);
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
        // 播放声音
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("ERROR", "notification audio error", e);
        }
    }

}
