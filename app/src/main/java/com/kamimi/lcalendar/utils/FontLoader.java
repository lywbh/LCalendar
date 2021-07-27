package com.kamimi.lcalendar.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

public class FontLoader {

    public static void loadAll(Context context) {
        AssetManager assets = context.getAssets();
        ldzsFont = Typeface.createFromAsset(assets, "fonts/ldzs.ttf");
    }

    /**
     * 卡通字体
     */
    public static Typeface ldzsFont;

}
