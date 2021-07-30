package com.kamimi.lcalendar;

import android.animation.ValueAnimator;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.kamimi.lcalendar.databinding.ActivityMainBinding;
import com.kamimi.lcalendar.utils.FontLoader;
import com.kamimi.lcalendar.utils.CommonUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String START_PAGE_NAME = "startPage";

    private ActivityMainBinding binding;

    private AssetManager assetManager;
    private volatile Drawable nextBackground;

    private ValueAnimator blurAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetManager = getAssets();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.mobile_navigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // 加载字体
        FontLoader.loadAll(this);
        // 随机轮换壁纸
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            setBackground();
            prepareBackground();
        });
        // 高斯模糊动画
        blurAnimator = ValueAnimator.ofInt(0, 10);
        blurAnimator.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            binding.backgroundBlur.setBlurRadius(currentValue);
            binding.backgroundBlur.requestLayout();
        });
        // 初始化第一张壁纸
        prepareBackground();
        setBackground();

        // 根据启动参数跳到不同页面
        int startPage = getIntent().getIntExtra(START_PAGE_NAME, R.id.navigation_home);
        if (startPage != R.id.navigation_home) {
            navController.navigate(startPage);
        }
    }

    /**
     * 随机加载一张背景图资源
     */
    private void prepareBackground() {
        try {
            String[] bgNames = assetManager.list("background");
            String bgName = CommonUtils.randomFrom(bgNames);
            nextBackground = Drawable.createFromStream(assetManager.open("background/" + bgName), bgName);
        } catch (IOException e) {
            Log.e("ERROR", "background fetch error", e);
        }
    }

    /**
     * 设置当前加载好的背景图
     */
    private void setBackground() {
        if (nextBackground != null) {
            binding.background.setBackground(nextBackground);
            reBlurBackground();
        }
    }

    /**
     * 重绘壁纸的模糊动画
     */
    public void reBlurBackground() {
        blurAnimator.start();
    }

}