package com.kamimi.lcalendar.ui.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;

import com.alibaba.fastjson.JSONObject;
import com.duma.ld.mylibrary.SwitchView;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.kamimi.lcalendar.AlarmReceiver;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.CommonUtils;
import com.kamimi.lcalendar.utils.DialogUtils;
import com.kamimi.lcalendar.utils.FontLoader;
import com.kamimi.lcalendar.utils.IdGenerator;
import com.kamimi.lcalendar.utils.SharedPreferencesLoader;
import com.stone.pile.libs.PileLayout;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationListAdapter extends PileLayout.Adapter {

    private final Context context;
    private final FragmentNotificationsBinding binding;
    private final NotificationLayerController layerController;
    private final AlarmManager alarmManager;

    private volatile int displayPosition;

    /**
     * ID生成器，新建日程时使用
     */
    private final IdGenerator idGenerator;

    /**
     * 操作这个即可控制列表项内容
     */
    private final List<NotificationData> dataList;

    public NotificationListAdapter(Context context, FragmentNotificationsBinding binding, NotificationLayerController layerController) {
        this.context = context;
        this.binding = binding;
        this.layerController = layerController;
        this.alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
        this.dataList = SharedPreferencesLoader.notificationSp.getAll()
                .values().stream()
                .map(jsonStr -> JSONObject.parseObject((String) jsonStr, NotificationData.class))
                .sorted(Comparator.comparing(NotificationData::getId))
                .collect(Collectors.toList());
        // 设置ID生成器，之后新增的日程ID依次递增
        int initId = dataList.isEmpty() ? 0 : dataList.get(dataList.size() - 1).getId();
        this.idGenerator = IdGenerator.create(initId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.notification_item;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void bindView(View view, int index) {
        // 各种视图
        TextView dateView = view.findViewWithTag("notification_date");
        TextView titleView = view.findViewWithTag("notification_title");
        TextView contentView = view.findViewWithTag("notification_content");
        SwitchView switchView = view.findViewWithTag("notification_alert_switch");
        Button deleteButton = view.findViewWithTag("notification_delete");
        // 设置字体
        dateView.setTypeface(FontLoader.ldzsFont);
        titleView.setTypeface(FontLoader.ldzsFont);
        contentView.setTypeface(FontLoader.ldzsFont);
        deleteButton.setTypeface(FontLoader.ldzsFont);
        // 数据填充
        NotificationData data = dataList.get(index);
        dateView.setText(data.getDate());
        titleView.setText(data.getTitle());
        contentView.setText(data.getContent());
        switchView.setChecked(data.isNotifyOn());
        // 点击删除
        deleteButton.setOnClickListener(v -> DialogUtils.confirm(context, "要删除日程吗", "确认", "取消", (dialog, witch) -> {
            NotificationData dataItem = dataList.get(index);
            // 删除数据库
            SharedPreferencesLoader.notificationSp.edit().remove(dataItem.getId().toString()).apply();
            // 删除闹钟
            cancelAlarm(dataItem);
            // 更新UI
            dataList.remove(index);
            binding.pileLayout.notifyDataSetChanged();
        }));
        // 切换通知开关
        switchView.setOnClickCheckedListener(() -> {
            NotificationData dataItem = dataList.get(index);
            // 修改UI数据
            dataItem.setNotifyOn(switchView.isChecked());
            // 存储到数据库
            String jsonStr = SharedPreferencesLoader.notificationSp.getString(dataItem.getId().toString(), null);
            NotificationData dbData = JSONObject.parseObject(jsonStr, NotificationData.class);
            dbData.setNotifyOn(dataItem.isNotifyOn());
            jsonStr = JSONObject.toJSONString(dbData);
            SharedPreferencesLoader.notificationSp.edit().putString(dataItem.getId().toString(), jsonStr).apply();
            // 添加/删除闹钟
            if (dataItem.isNotifyOn()) {
                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    setAlarm(dataItem);
                } else {
                    switchView.setChecked(false);
                    askForNotifyOn();
                }
            } else {
                cancelAlarm(dataItem);
            }
        });
    }

    /**
     * 提示用户去开启通知
     */
    private void askForNotifyOn() {
        DialogUtils.confirm(context, "开启失败", "开启日程提醒需要授权APP发送通知", "去开启", "取消", (dialog, witch) -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                intent.putExtra(Notification.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
            } else {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
            }
            context.startActivity(intent);
        });
    }

    /**
     * item点击事件
     */
    @Override
    public void onItemClick(View view, int position) {
        // 只能点击最上层展示的那一个
        if (position == displayPosition) {
            showNotificationDetail(position);
        }
    }

    /**
     * item滑到最上层展示的事件
     */
    @Override
    public void displaying(int position) {
        this.displayPosition = position;
    }

    /**
     * 新增弹出框
     */
    public void showNotificationDetail() {
        showNotificationDetail(-1);
    }

    /**
     * 展示详情弹出框
     *
     * @param position 打开的是第几个窗口，超出列表范围的数表示创建一个新窗口
     */
    public void showNotificationDetail(int position) {
        // 编辑框焦点变更
        setFocusChangeListener(binding.notificationEditorTitle);
        setFocusChangeListener(binding.notificationEditorContent);
        // 点击蒙层编辑框失焦
        binding.notificationShade.setOnClickListener(u -> {
            binding.notificationEditorTitle.clearFocus();
            binding.notificationEditorContent.clearFocus();
        });
        // 点击保存
        binding.notificationSubmitButton.setOnClickListener(w -> {
            Editable titleText = binding.notificationEditorTitle.getText();
            if (titleText.length() == 0) {
                DialogUtils.toast(context, "标题还没有填呢~");
            } else {
                // 更新UI数据
                NotificationData data;
                if (position >= 0 && position < dataList.size()) {
                    data = dataList.get(position);
                } else {
                    data = new NotificationData();
                    dataList.add(data);
                }
                fillDataFromEditor(data);
                // 写入数据库
                SharedPreferencesLoader.notificationSp.edit().putString(data.getId().toString(), JSONObject.toJSONString(data)).apply();
                // 刷新UI，这一步会触发闹钟开关切换，即自动关闭该闹钟
                binding.pileLayout.notifyDataSetChanged();
                DialogUtils.toast(context, "保存成功");
                layerController.hideLayer();
            }
        });
        // 点击取消
        binding.notificationCancelButton.setOnClickListener(w -> DialogUtils.confirm(context, "放弃编辑？", "确认", "取消", (dialog, which) -> layerController.hideLayer()));
        // 数据填充
        if (position >= 0 && position < dataList.size()) {
            // 编辑时
            NotificationData data = dataList.get(position);
            String[] dateSplit = data.getDate().split("-");
            String[] timeSplit = data.getNotifyTime().split(":");
            binding.notificationEditorDate.setDefaultValue(DateEntity.target(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2])));
            binding.notificationEditorTime.setDefaultValue(TimeEntity.target(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]), 0));
            binding.notificationEditorTitle.setText(data.getTitle());
            binding.notificationEditorContent.setText(data.getContent());
        } else {
            // 创建时
            binding.notificationEditorDate.setDefaultValue(DateEntity.today());
            binding.notificationEditorTime.setDefaultValue(TimeEntity.now());
            binding.notificationEditorTitle.setText("");
            binding.notificationEditorContent.setText("");
        }
        layerController.showLayer();
    }

    private void setFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                // 失焦时关闭软键盘
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    private void fillDataFromEditor(NotificationData notificationData) {
        if (notificationData.getId() == null) {
            notificationData.setId(idGenerator.next());
        }
        notificationData.setDate(String.format("%s-%s-%s",
                binding.notificationEditorDate.getSelectedYear(),
                binding.notificationEditorDate.getSelectedMonth(),
                binding.notificationEditorDate.getSelectedDay()));
        notificationData.setTitle(binding.notificationEditorTitle.getText().toString());
        notificationData.setContent(binding.notificationEditorContent.getText().toString());
        notificationData.setNotifyTime(String.format("%s:%s",
                binding.notificationEditorTime.getSelectedHour(),
                binding.notificationEditorTime.getSelectedMinute()));
        notificationData.setNotifyOn(false);
    }

    /**
     * 开启闹钟
     */
    private void setAlarm(NotificationData dataItem) {
        String dateTimeStr = String.format("%s %s:00", dataItem.getDate(), dataItem.getNotifyTime());
        Date triggerTime = CommonUtils.parseDate(dateTimeStr, "yyyy-M-d HH:mm:ss");
        if (triggerTime != null && triggerTime.getTime() >= System.currentTimeMillis()) {
            Intent intent = new Intent(context, AlarmReceiver.class)
                    .putExtra(AlarmReceiver.ALARM_DATA_NAME, JSONObject.toJSONString(dataItem));
            PendingIntent pi = PendingIntent.getBroadcast(context, dataItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime.getTime(), pi);
        }
    }

    /**
     * 关闭闹钟，如果当前没开启闹钟则无事发生
     */
    private void cancelAlarm(NotificationData dataItem) {
        PendingIntent pi = PendingIntent.getBroadcast(context, dataItem.getId(), new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }

}
