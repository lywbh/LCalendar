package com.kamimi.lcalendar.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duma.ld.mylibrary.SwitchView;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.DialogUtils;
import com.kamimi.lcalendar.utils.FontLoader;
import com.stone.pile.libs.PileLayout;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationListAdapter extends PileLayout.Adapter {

    private final Context context;

    private final FragmentNotificationsBinding binding;

    private final NotificationLayerController layerController;

    /**
     * 日程数据库
     */
    private final SharedPreferences notificationSp;

    /**
     * 操作这个即可控制列表项内容
     */
    private final List<NotificationData> dataList;

    public NotificationListAdapter(Context context, FragmentNotificationsBinding binding, NotificationLayerController layerController) {
        this.context = context;
        this.binding = binding;
        this.layerController = layerController;

        // 初始化数据
        notificationSp = this.context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        String jsonArrStr = notificationSp.getString("LCalendarNotificationSp", "[]");
        JSONArray jsonArr = JSONArray.parseArray(jsonArrStr);
        this.dataList = jsonArr.stream()
                .map(jsonObj -> JSON.toJavaObject((JSON) jsonObj, NotificationData.class))
                .collect(Collectors.toList());
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
        NotificationData dataItem = dataList.get(index);
        dateView.setText(dataItem.getDate());
        titleView.setText(dataItem.getTitle());
        contentView.setText(dataItem.getContent());
        switchView.setChecked(dataItem.isNotifyOn());
        // 点击删除
        deleteButton.setOnClickListener(v -> DialogUtils.confirmDialog(context, "要删除日程吗", (dialog, witch) -> {
            // 删除数据
            String jsonArrStr = notificationSp.getString("LCalendarNotificationSp", "[]");
            JSONArray jsonArr = JSONArray.parseArray(jsonArrStr);
            jsonArr.remove(index);
            notificationSp.edit().putString("LCalendarNotificationSp", jsonArr.toJSONString()).apply();
            // 删除闹钟，通过开关关闭来触发
            switchView.setChecked(false);
            // 更新UI
            dataList.remove(index);
            binding.pileLayout.notifyDataSetChanged();
        }));
        // 切换通知开关
        switchView.setOnClickCheckedListener(() -> {
            // 存储到数据库
            String jsonArrStr = notificationSp.getString("LCalendarNotificationSp", "[]");
            JSONArray jsonArr = JSONArray.parseArray(jsonArrStr);
            ((JSONObject) jsonArr.get(index)).put("notifyOn", switchView.isChecked());
            notificationSp.edit().putString("LCalendarNotificationSp", jsonArr.toJSONString()).apply();
            // TODO 添加或删除闹钟
            if (switchView.isChecked()) {

            } else {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        showNotificationDetail(position);
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
     * @param position 打开的是第几个窗口，负数表示打开一个新增窗口
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
                // 保存数据
                String jsonArrStr = notificationSp.getString("LCalendarNotificationSp", "[]");
                JSONArray jsonArr = JSONArray.parseArray(jsonArrStr);
                NotificationData notificationData = NotificationData.builder()
                        .date(String.format("%s-%s-%s",
                                binding.notificationEditorDate.getSelectedYear(),
                                binding.notificationEditorDate.getSelectedMonth(),
                                binding.notificationEditorDate.getSelectedDay()))
                        .title(binding.notificationEditorTitle.getText().toString())
                        .content(binding.notificationEditorContent.getText().toString())
                        .notifyTime(String.format("%s:%s",
                                binding.notificationEditorTime.getSelectedHour(),
                                binding.notificationEditorTime.getSelectedMinute()))
                        .notifyOn(false)
                        .build();
                // 更新UI数据
                if (position >= 0 && position < dataList.size()) {
                    // 编辑时
                    jsonArr.set(position, JSONObject.toJSON(notificationData));
                    dataList.set(position, notificationData);
                } else {
                    // 新增时
                    jsonArr.add(JSONObject.toJSON(notificationData));
                    dataList.add(notificationData);
                }
                // 写入数据库
                notificationSp.edit().putString("LCalendarNotificationSp", jsonArr.toJSONString()).apply();
                // 刷新UI
                binding.pileLayout.notifyDataSetChanged();
                DialogUtils.toast(context, "保存成功");
                layerController.hideLayer();
            }
        });
        // 点击取消
        binding.notificationCancelButton.setOnClickListener(w -> DialogUtils.confirmDialog(context, "放弃编辑？", (dialog, which) -> layerController.hideLayer()));
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

}
