package com.kamimi.lcalendar;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kamimi.lcalendar.utils.DialogUtils;

public class AlarmActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        DialogUtils.confirmDialog(this, "您有新的日程~", "", dialog -> {
            mediaPlayer.stop();
            finish();
        });
    }

}