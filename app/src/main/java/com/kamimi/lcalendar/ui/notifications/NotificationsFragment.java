package com.kamimi.lcalendar.ui.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.DialogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private NotificationListAdapter adapter;

    // 滑动动画
    private ValueAnimator slideAnimator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        // 显示右上角按钮
        setHasOptionsMenu(true);
        // 创建堆叠滑动列表
        adapter = new NotificationListAdapter(getContext(), binding.pileLayout);
        binding.pileLayout.setAdapter(adapter);
        // 屏蔽掉弹出层的点击事件
        binding.notificationDetail.setOnClickListener(v -> {
        });

        // 弹出层滑动动画
        slideAnimator = ValueAnimator.ofFloat(0, 5);
        slideAnimator.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            binding.notificationMainDiv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, currentValue));
            binding.notificationMainDiv.requestLayout();
        });
        slideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                binding.notificationDetail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 滑动结束后如果是隐藏状态，把弹层关闭
                float currentValue = (float) ((ValueAnimator) animation).getAnimatedValue();
                if (currentValue == 0) {
                    binding.notificationDetail.setVisibility(View.GONE);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_add_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.notification_add_button) {
            showNotificationDetail();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showNotificationDetail() {
        // 编辑框焦点变更
        setFocusChangeListener(binding.notificationEditorTitle);
        setFocusChangeListener(binding.notificationEditorContent);
        // 点击蒙层编辑框失焦
        binding.notificationShade.setOnClickListener(u -> {
            binding.notificationEditorTitle.clearFocus();
            binding.notificationEditorContent.clearFocus();
        });
        // 提醒数据库
        SharedPreferences notificationSp = getContext().getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        // 点击保存
        binding.notificationSubmitButton.setOnClickListener(w -> {
            Editable titleText = binding.notificationEditorTitle.getText();
            if (titleText.length() == 0) {
                DialogUtils.toast(getContext(), "标题还没有填呢~");
            } else {
                // 保存数据
                Set<String> currentSet = notificationSp.getStringSet("LCalendarNotificationSp", new HashSet<>());
                NotificationData notificationData = NotificationData.builder()
                        .date("2021-7-27")
                        .title(binding.notificationEditorTitle.getText().toString())
                        .content(binding.notificationEditorContent.getText().toString())
                        .build();
                Set<String> newSet = new HashSet<>(currentSet);
                newSet.add(JSONObject.toJSONString(notificationData));
                notificationSp.edit().putStringSet("LCalendarNotificationSp", newSet).apply();
                // 刷新UI
                List<NotificationData> dbList = newSet.stream()
                        .map(jsonStr -> JSON.toJavaObject(JSONObject.parseObject(jsonStr), NotificationData.class))
                        .sorted(Comparator.comparing(NotificationData::getDate))
                        .collect(Collectors.toList());
                adapter.reloadDataList(dbList);
                // 关闭层
                DialogUtils.toast(getContext(), "保存成功");
                hideLayer();
            }
        });
        // 点击取消
        binding.notificationCancelButton.setOnClickListener(w -> DialogUtils.confirmDialog(getContext(), "放弃编辑？", (dialog, which) -> hideLayer()));

        showLayer();
    }

    private void setFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                // 失焦时关闭软键盘
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    /**
     * 显示该弹出层，并隐藏添加按钮
     */
    private void showLayer() {
        setHasOptionsMenu(false);
        slideAnimator.start();
    }

    /**
     * 隐藏该弹出层，并显示添加按钮
     */
    private void hideLayer() {
        slideAnimator.reverse();
        setHasOptionsMenu(true);
    }

}