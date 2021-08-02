package com.kamimi.lcalendar.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSONObject;
import com.kamimi.lcalendar.obj.Day;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.obj.GlobalConstants;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.obj.RGB;
import com.kamimi.lcalendar.utils.CommonUtils;
import com.kamimi.lcalendar.databinding.FragmentHomeBinding;
import com.kamimi.lcalendar.utils.FontLoader;
import com.kamimi.lcalendar.utils.SharedPreferencesLoader;
import com.kamimi.lcalendar.view.CalendarView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    /**
     * 日历图标缓存
     */
    private final LruCache<String, Bitmap> heartPicCache = new LruCache<>(31);
    private final LruCache<String, Bitmap> sunPicCache = new LruCache<>(31);

    /**
     * 日历字体相关参数
     */
    private static final RGB CALENDAR_FONT_RGB = new RGB(34, 26, 44);
    private static final int WEEK_FONT_ALPHA = 200;
    private static final int DATE_FONT_ALPHA = 150;
    private static final int OUT_DATE_FONT_ALPHA = 100;
    private static final float DATE_FONT_SIZE = 50;
    private static final float SELECTED_FONT_SIZE = 70;
    private static final float OUT_DATE_FONT_SIZE = 35;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel.getTitleText().observe(getViewLifecycleOwner(), binding.titleHome::setText);
        homeViewModel.getMainText().observe(getViewLifecycleOwner(), binding.textHome::setText);
        homeViewModel.getHintText().observe(getViewLifecycleOwner(), binding.textHitokoto::setText);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置页面各字体
        binding.titleHome.setTypeface(FontLoader.ldzsFont);
        binding.textHome.setTypeface(FontLoader.ldzsFont);
        binding.textHitokoto.setTypeface(FontLoader.ldzsFont);
        // 日历绘制月份回调
        binding.calendar.setOnDrawMonth((year, month, canvas, paint) -> {
            // 更新头上的年月
            String msg = CommonUtils.monthToEn(month) + GlobalConstants.BLANK_GAP + year;
            if (!msg.equals(homeViewModel.getTitleText().getValue())) {
                binding.titleHome.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.hide));
                homeViewModel.getTitleText().setValue(msg);
                binding.titleHome.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.show));
            }
        });
        // 日历绘制天回调
        binding.calendar.setOnDrawDay(new CalendarView.OnDrawDay() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Paint paint) {
                float x = 30, y = 100;
                Paint p = new Paint(paint);
                p.setTypeface(FontLoader.ldzsFont);
                p.setARGB(WEEK_FONT_ALPHA, CALENDAR_FONT_RGB.r, CALENDAR_FONT_RGB.g, CALENDAR_FONT_RGB.b);
                p.setTextSize(DATE_FONT_SIZE);
                // 默认字体下字符的宽度
                float originWidth = p.measureText(day.text);
                if ("-1".equals(day.dateText)) {
                    p.setAlpha(WEEK_FONT_ALPHA);
                } else if (day.isCurrent) {
                    p.setAlpha(DATE_FONT_ALPHA);
                    if (day.backgroundStyle == 2 || day.backgroundStyle == 3) {
                        if (day.backgroundStyle == 3) {
                            p.setFakeBoldText(true);
                        }
                        p.setTextSize(SELECTED_FONT_SIZE);
                        // 字体缩放后需要移动一个偏移量，保证居中
                        x += (originWidth - p.measureText(day.text)) / 2;
                    }
                } else {
                    p.setAlpha(OUT_DATE_FONT_ALPHA);
                    p.setTextSize(OUT_DATE_FONT_SIZE);
                    // 字体缩放后需要移动一个偏移量，保证居中
                    x += (originWidth - p.measureText(day.text)) / 2;
                }
                canvas.drawText(day.text, x, y, p);
                return true;
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Paint paint) {
                // 星期栏，不作改变
                if ("-1".equals(day.dateText)) {
                    return;
                }
                // 画线（通知标记）
                boolean containsLine = SharedPreferencesLoader.notificationSp.getAll().values()
                        .stream().map(jsonStr -> JSONObject.parseObject((String) jsonStr, NotificationData.class).getDate())
                        .anyMatch(date -> date.equals(day.dateText));
                if (containsLine) {
                    long randomSeed = new StringBuilder(day.dateText).reverse().toString().hashCode();
                    Bitmap bitmap = sunPicCache.get(day.dateText);
                    if (bitmap == null) {
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inScaled = false;
                        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.line, option);
                        bitmap = randomSunRotate(randomSeed, bitmap);
                        bitmap = randomLineSize(randomSeed, bitmap);
                        sunPicCache.put(day.dateText, bitmap);
                    }
                    float[] randomPos = randomSunPos(randomSeed);
                    canvas.drawBitmap(bitmap, randomPos[0], randomPos[1], paint);
                }
                // 画爱心（自定义标记）
                if (SharedPreferencesLoader.markSp.contains(day.dateText)) {
                    long randomSeed = new StringBuilder(day.dateText).reverse().toString().hashCode();
                    Bitmap bitmap = heartPicCache.get(day.dateText);
                    if (bitmap == null) {
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inScaled = false;
                        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.heart);
                        bitmap = randomHeartRotate(randomSeed, bitmap);
                        bitmap = randomHeartSize(randomSeed, bitmap);
                        heartPicCache.put(day.dateText, bitmap);
                    }
                    float[] randomPos = randomHeartPos(randomSeed);
                    canvas.drawBitmap(bitmap, randomPos[0], randomPos[1], paint);
                }
            }
        });
    }

    /**
     * 线段随机大小
     */
    private Bitmap randomLineSize(long seed, Bitmap origin) {
        int w = CommonUtils.randomInt(seed, 90, 130);
        int h = CommonUtils.randomInt(seed, 30, 50);
        return resizeBitmap(origin, w, h);
    }

    /**
     * 线段随机偏移
     */
    private float[] randomSunPos(long seed) {
        float left = CommonUtils.randomFloat(seed, 0, 30);
        float top = CommonUtils.randomFloat(seed, 80, 120);
        return new float[]{left, top};
    }

    /**
     * 线段随机旋转
     */
    private Bitmap randomSunRotate(long seed, Bitmap origin) {
        float alpha = CommonUtils.randomFloat(seed, -50, 40);
        return rotateBitmap(origin, alpha);
    }

    /**
     * 爱心随机大小
     */
    private Bitmap randomHeartSize(long seed, Bitmap origin) {
        int w = CommonUtils.randomInt(seed, 60, 70);
        int h = CommonUtils.randomInt(seed, 60, 70);
        return resizeBitmap(origin, w, h);
    }

    /**
     * 爱心随机偏移
     */
    private float[] randomHeartPos(long seed) {
        float left = CommonUtils.randomFloat(seed, 40, 60);
        float top = CommonUtils.randomFloat(seed, 25, 45);
        return new float[]{left, top};
    }

    /**
     * 爱心随机旋转
     */
    private Bitmap randomHeartRotate(long seed, Bitmap origin) {
        float alpha = CommonUtils.randomFloat(seed, 10, 40);
        return rotateBitmap(origin, alpha);
    }

    private Bitmap resizeBitmap(Bitmap origin, int w, int h) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
        if (newBm.equals(origin)) {
            return newBm;
        }
        origin.recycle();
        return newBm;
    }

    /**
     * 旋转
     */
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBm.equals(origin)) {
            return newBm;
        }
        origin.recycle();
        return newBm;
    }

}