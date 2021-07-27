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
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
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

        // 设置弹出层的字体
        this.binding.notificationEditorDate.getYearWheelView().setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorDate.getYearLabelView().setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorDate.getMonthWheelView().setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorDate.getMonthLabelView().setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorDate.getDayWheelView().setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorDate.getDayLabelView().setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorTitle.setTypeface(FontLoader.ldzsFont);
        this.binding.notificationEditorContent.setTypeface(FontLoader.ldzsFont);
        this.binding.notificationSubmitButton.setTypeface(FontLoader.ldzsFont);
        this.binding.notificationCancelButton.setTypeface(FontLoader.ldzsFont);

        // 初始化数据
        notificationSp = this.context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        // notificationSp.edit().remove("LCalendarNotificationSp").commit();
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

        deleteButton.setOnClickListener(v -> DialogUtils.confirmDialog(context, "要删除日程吗", (dialog, witch) -> {
            // 删除数据
            String jsonArrStr = notificationSp.getString("LCalendarNotificationSp", "[]");
            JSONArray jsonArr = JSONArray.parseArray(jsonArrStr);
            jsonArr.remove(index);
            notificationSp.edit().putString("LCalendarNotificationSp", jsonArr.toJSONString()).apply();
            // 更新UI
            dataList.remove(index);
            binding.pileLayout.notifyDataSetChanged();
        }));
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
                        .build();
                if (position < 0) {
                    jsonArr.add(JSONObject.toJSON(notificationData));
                    dataList.add(notificationData);
                } else {
                    jsonArr.set(position, JSONObject.toJSON(notificationData));
                    dataList.set(position, notificationData);
                }
                notificationSp.edit().putString("LCalendarNotificationSp", jsonArr.toJSONString()).apply();
                // 刷新UI
                binding.pileLayout.notifyDataSetChanged();
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
