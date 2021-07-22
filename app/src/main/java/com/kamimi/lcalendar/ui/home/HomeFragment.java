package com.kamimi.lcalendar.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
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

import com.kamimi.lcalendar.Day;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.Utils;
import com.kamimi.lcalendar.databinding.FragmentHomeBinding;
import com.kamimi.lcalendar.view.CalendarView;

import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private LruCache<String, Bitmap> heartPicCache = new LruCache<>(31);
    private Typeface ldzsFont, laksFont;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel.getTitleText().observe(getViewLifecycleOwner(), binding.titleHome::setText);
        homeViewModel.getMText().observe(getViewLifecycleOwner(), binding.textHome::setText);
        homeViewModel.getHText().observe(getViewLifecycleOwner(), binding.textHitokoto::setText);

        SharedPreferences markSp = getContext().getSharedPreferences("LCalendarMarkSp", Context.MODE_PRIVATE);
        ldzsFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/ldzs.ttf");
        laksFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/LaksOner.ttf");

        binding.titleHome.setTypeface(laksFont);
        binding.textHome.setTypeface(ldzsFont);
        binding.textHitokoto.setTypeface(ldzsFont);

        binding.calendar.setOnDrawDays(new CalendarView.OnDrawDays() {
            @Override
            public boolean drawDay(Day day, Canvas canvas, Context context, Paint paint) {
                float x = day.location_x + 30, y = day.location_y + 100;
                Paint p = new Paint(paint);
                p.setARGB(155, 34,26,44);
                if (day.dateText.equals("-1")) {
                    p.setTypeface(ldzsFont);
                    p.setTextSize(50);
                } else if (day.isCurrent) {
                    p.setTypeface(laksFont);
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
                    p.setTypeface(ldzsFont);
                    p.setTextSize(35);
                }
                canvas.drawText(day.text, x, y, p);
                return true;
            }

            @Override
            public void drawDayAbove(Day day, Canvas canvas, Context context, Paint paint) {
                if (day.isCurrent && day.text.equals("01")) {
                    String[] daySplit = day.dateText.split("-");
                    String msg = Utils.monthToEn(Integer.parseInt(daySplit[1])) + "  " + daySplit[0];
                    if (!msg.equals(homeViewModel.getTitleText().getValue())) {
                        binding.titleHome.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.hide));
                        homeViewModel.getTitleText().setValue(msg);
                        binding.titleHome.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.show));
                    }
                }
                // 画爱心
                if (markSp.contains(day.dateText)) {
                    long randomSeed = new StringBuilder(day.dateText).reverse().toString().hashCode();
                    float[] randomPos = randomPos(randomSeed);
                    Bitmap bitmap;
                    if ((bitmap = heartPicCache.get(day.dateText)) == null) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.heart);
                        bitmap = randomRotate(randomSeed, bitmap);
                        bitmap = randomSize(randomSeed, bitmap);
                        heartPicCache.put(day.dateText, bitmap);
                    }
                    canvas.drawBitmap(bitmap, randomPos[0], randomPos[1], paint);
                }
            }
        });
        return binding.getRoot();
    }

    /**
     * 随机大小
     */
    private Bitmap randomSize(long seed, Bitmap origin) {
        int w = Utils.randomInt(seed, 60, 70);
        int h = Utils.randomInt(seed, 60, 70);
        return resizeBitmap(origin, w, h);
    }

    /**
     * 随机偏移
     */
    private float[] randomPos(long seed) {
        float left = Utils.randomFloat(seed, 40, 60);
        float top = Utils.randomFloat(seed, 25, 45);
        return new float[]{left, top};
    }

    /**
     * 随机旋转
     */
    private Bitmap randomRotate(long seed, Bitmap origin) {
        float alpha = Utils.randomFloat(seed, 5, 45);
        return rotateBitmap(origin, alpha);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
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