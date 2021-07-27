package com.kamimi.lcalendar.ui.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.kamimi.lcalendar.databinding.FragmentNotificationsBinding;

public class NotificationLayerController {

    private final ValueAnimator slideAnimator;

    private final Fragment fragment;

    public NotificationLayerController(Fragment fragment, FragmentNotificationsBinding binding) {
        this.fragment = fragment;

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
    }

    /**
     * 显示该弹出层，并隐藏添加按钮
     */
    public void showLayer() {
        fragment.setHasOptionsMenu(false);
        slideAnimator.start();
    }

    /**
     * 隐藏该弹出层，并显示添加按钮
     */
    public void hideLayer() {
        slideAnimator.reverse();
        fragment.setHasOptionsMenu(true);
    }


}
