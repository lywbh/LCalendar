package com.kamimi.lcalendar.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kamimi.lcalendar.obj.Day;
import com.kamimi.lcalendar.MainActivity;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.utils.CommonUtils;
import com.kamimi.lcalendar.databinding.FragmentHomeBinding;
import com.kamimi.lcalendar.utils.FontLoader;
import com.kamimi.lcalendar.view.CalendarView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private final LruCache<String, Bitmap> heartPicCache = new LruCache<>(31);
    private final LruCache<String, Bitmap> sunPicCache = new LruCache<>(31);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel.getTitleText().observe(getViewLifecycleOwner(), binding.titleHome::setText);
        homeViewModel.getMText().observe(getViewLifecycleOwner(), binding.textHome::setText);
        homeViewModel.getHText().observe(getViewLifecycleOwner(), binding.textHitokoto::setText);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置页面各字体
        binding.titleHome.setTypeface(FontLoader.ldzsFont);
        binding.textHome.setTypeface(FontLoader.ldzsFont);
        binding.textHitokoto.setTypeface(FontLoader.ldzsFont);
        // 爱心记录
        SharedPreferences markSp = getContext().getSharedPreferences("LCalendarMarkSp", Context.MODE_PRIVATE);
        // 日记记录
        SharedPreferences diarySp = getContext().getSharedPreferences("LCalendarDiarySp", Context.MODE_PRIVATE);
        //日历绘制回调
        binding.calendar.setOnDrawDays(new CalendarView.OnDrawDays() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Context context, Paint paint) {
                float x = day.location_x + 30, y = day.location_y + 100;
                Paint p = new Paint(paint);
                p.setARGB(155, 34,26,44);
                if (day.dateText.equals("-1")) {
                    p.setTypeface(FontLoader.ldzsFont);
                    p.setTextSize(50);
                } else if (day.isCurrent) {
                    p.setTypeface(FontLoader.ldzsFont);
                    p.setTextSize(50);
                    if (day.backgroundStyle == 2) {
                        p.setTextSize(70);
                        x -= 10;
                        y += 5;
                    } else if (day.backgroundStyle == 3) {
                        p.setFakeBoldText(true);
                        p.setARGB(200, 34,26,44);
                        p.setTextSize(70);
                        x -= 10;
                        y += 5;
                    }
                } else {
                    p.setTypeface(FontLoader.ldzsFont);
                    p.setTextSize(35);
                }
                canvas.drawText(day.text, x, y, p);
                return true;
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint) {
                if (day.isCurrent && day.text.equals("01")) {
                    String[] daySplit = day.dateText.split("-");
                    String msg = CommonUtils.monthToEn(Integer.parseInt(daySplit[1])) + "  " + daySplit[0];
                    if (!msg.equals(homeViewModel.getTitleText().getValue())) {
                        ((MainActivity) getActivity()).reBlurBackground();
                        binding.titleHome.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.hide));
                        homeViewModel.getTitleText().setValue(msg);
                        binding.titleHome.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.show));
                    }
                }
                // 画太阳
                if (diarySp.contains(day.dateText)) {
                    long randomSeed = new StringBuilder(day.dateText).reverse().toString().hashCode();
                    Bitmap bitmap;
                    if ((bitmap = sunPicCache.get(day.dateText)) == null) {
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
                // 画爱心
                if (markSp.contains(day.dateText)) {
                    long randomSeed = new StringBuilder(day.dateText).reverse().toString().hashCode();
                    Bitmap bitmap;
                    if ((bitmap = heartPicCache.get(day.dateText)) == null) {
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
        float alpha = CommonUtils.randomFloat(seed, 5, 45);
        return rotateBitmap(origin, alpha);
    }

    private Bitmap resizeBitmap(Bitmap origin, int w, int h) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        Log.v("scaleWidth,scaleHeight", scaleWidth + "," + scaleHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
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
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

}