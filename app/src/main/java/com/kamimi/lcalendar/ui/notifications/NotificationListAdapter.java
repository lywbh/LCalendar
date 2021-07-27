package com.kamimi.lcalendar.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.AnimUtils;
import com.kamimi.lcalendar.utils.DialogUtils;
import com.kamimi.lcalendar.utils.FontLoader;
import com.stone.pile.libs.PileLayout;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationListAdapter extends PileLayout.Adapter {

    private final Context context;

    private final FragmentNotificationsBinding binding;

    private final NotificationLayerController layerController;

    /**
     * 操作这个即可控制列表项内容
     */
    private List<NotificationData> dataList;

    public NotificationListAdapter(Context context, FragmentNotificationsBinding binding, NotificationLayerController layerController) {
        this.context = context;
        this.binding = binding;
        this.layerController = layerController;

        // 初始化数据
        SharedPreferences notificationSp = this.context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        //notificationSp.edit().remove("LCalendarNotificationSp").commit();
        Set<String> jsonStrSet = notificationSp.getStringSet("LCalendarNotificationSp", new HashSet<>());
        this.dataList = jsonStrSet.stream()
                .map(jsonStr -> JSON.toJavaObject(JSONObject.parseObject(jsonStr), NotificationData.class))
                .sorted(Comparator.comparing(NotificationData::getDate))
                .collect(Collectors.toList());
    }

    /**
     * 刷新UI
     */
    public void reloadDataList(List<NotificationData> dataList) {
        this.dataList = dataList;
        binding.pileLayout.notifyDataSetChanged();
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
        TextView dateView = view.findViewWithTag("notification_date");
        TextView titleView = view.findViewWithTag("notification_title");
        TextView contentView = view.findViewWithTag("notification_content");
        //TextView switchView = view.findViewWithTag("notification_switch");
        Button deleteButton = view.findViewWithTag("notification_delete");

        dateView.setTypeface(FontLoader.ldzsFont);
        titleView.setTypeface(FontLoader.ldzsFont);
        contentView.setTypeface(FontLoader.ldzsFont);
        deleteButton.setTypeface(FontLoader.ldzsFont);

        NotificationData dataItem = dataList.get(index);
        dateView.setText(dataItem.getDate());
        titleView.setText(dataItem.getTitle());
        contentView.setText(dataItem.getContent());
        //switchView.setText(dataItem.getNotifyTime());

    }

    @Override
    public void displaying(int position) {
        // TODO 滑到最前端时触发
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
     * @param position 打开的是第几个窗口，负数表示是新增
     */
    public void showNotificationDetail(int position) {
        NotificationData data = position < 0 ? NotificationData.builder().build() : dataList.get(position);
        // 编辑框焦点变更
        setFocusChangeListener(binding.notificationEditorTitle);
        setFocusChangeListener(binding.notificationEditorContent);
        // 点击蒙层编辑框失焦
        binding.notificationShade.setOnClickListener(u -> {
            binding.notificationEditorTitle.clearFocus();
            binding.notificationEditorContent.clearFocus();
        });
        // 提醒数据库
        SharedPreferences notificationSp = context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        // 点击保存
        binding.notificationSubmitButton.setOnClickListener(w -> {
            Editable titleText = binding.notificationEditorTitle.getText();
            if (titleText.length() == 0) {
                DialogUtils.toast(context, "标题还没有填呢~");
            } else {
                // 保存数据
                Set<String> currentSet = notificationSp.getStringSet("LCalendarNotificationSp", new HashSet<>());
                List<NotificationData> currentList = currentSet.stream()
                        .map(jsonStr -> JSON.toJavaObject(JSONObject.parseObject(jsonStr), NotificationData.class))
                        .sorted(Comparator.comparing(NotificationData::getDate))
                        .collect(Collectors.toList());
                NotificationData notificationData = NotificationData.builder()
                        .date(String.format("%s-%s-%s",
                                binding.notificationEditorDate.getSelectedYear(),
                                binding.notificationEditorDate.getSelectedMonth(),
                                binding.notificationEditorDate.getSelectedDay()))
                        .title(binding.notificationEditorTitle.getText().toString())
                        .content(binding.notificationEditorContent.getText().toString())
                        .build();
                if (position < 0) {
                    currentList.add(notificationData);
                } else {
                    currentList.set(position, notificationData);
                }
                Set<String> newSet = currentList.stream().map(JSONObject::toJSONString).collect(Collectors.toSet());
                notificationSp.edit().putStringSet("LCalendarNotificationSp", newSet).apply();
                // 刷新UI
                reloadDataList(currentList);
                // 关闭层
                DialogUtils.toast(context, "保存成功");
                layerController.hideLayer();
            }
        });
        // 点击取消
        binding.notificationCancelButton.setOnClickListener(w -> DialogUtils.confirmDialog(context, "放弃编辑？", (dialog, which) -> layerController.hideLayer()));

        binding.notificationEditorDate.setRange(DateEntity.target(1900, 1, 1), DateEntity.target(2099, 12, 31));
        if (position < 0) {
            binding.notificationEditorDate.setDefaultValue(DateEntity.today());
            binding.notificationEditorTitle.setText("");
            binding.notificationEditorContent.setText("");
        } else {
            String[] dateSplit = data.getDate().split("-");
            binding.notificationEditorDate.setDefaultValue(DateEntity.target(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2])));
            binding.notificationEditorTitle.setText(data.getTitle());
            binding.notificationEditorContent.setText(data.getContent());
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
