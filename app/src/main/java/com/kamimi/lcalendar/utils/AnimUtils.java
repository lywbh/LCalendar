package com.kamimi.lcalendar.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AnimUtils {

    /**
     * 权重滑动动画
     */
    public static void toggleWeightAnim(View view, float startWeight, float endWeight) {
        ValueAnimator va = ValueAnimator.ofFloat(startWeight, endWeight);
        va.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParam = view.getLayoutParams();
            view.setLayoutParams(new LinearLayout.LayoutParams(layoutParam.width, layoutParam.height, currentValue));
            view.requestLayout();
        });
        va.start();
    }

}
