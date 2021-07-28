package com.kamimi.lcalendar.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;
import com.kamimi.lcalendar.utils.FontLoader;

import org.jetbrains.annotations.NotNull;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private NotificationListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        // 显示右上角按钮
        setHasOptionsMenu(true);

        // 弹出层控制器
        NotificationLayerController layerController = new NotificationLayerController(this, binding);
        // 屏蔽掉弹出层的点击事件
        binding.notificationDetail.setOnClickListener(v -> {
        });
        // 创建堆叠滑动列表
        adapter = new NotificationListAdapter(getContext(), binding, layerController);
        binding.pileLayout.setAdapter(adapter);

        // 设置弹出层的字体
        binding.notificationEditorDate.getYearWheelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorDate.getYearLabelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorDate.getMonthWheelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorDate.getMonthLabelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorDate.getDayWheelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorDate.getDayLabelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorTimeText.setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorTime.getHourWheelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorTime.getHourLabelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorTime.getMinuteWheelView().setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorTitle.setTypeface(FontLoader.ldzsFont);
        binding.notificationEditorContent.setTypeface(FontLoader.ldzsFont);
        binding.notificationSubmitButton.setTypeface(FontLoader.ldzsFont);
        binding.notificationCancelButton.setTypeface(FontLoader.ldzsFont);
        // 设置组件样式
        binding.notificationEditorDate.setRange(DateEntity.target(1900, 1, 1), DateEntity.target(2099, 12, 31));
        binding.notificationEditorTime.setRange(TimeEntity.target(0, 0, 0), TimeEntity.target(23, 59, 59));

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
            adapter.showNotificationDetail();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}