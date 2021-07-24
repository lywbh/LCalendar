package com.kamimi.lcalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * 安卓工具类
 */
public class AndroidUtils {

    /**
     * 信息
     */
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 信息，指定展示时间
     */
    public static void toast(Context context, String text, int length) {
        Toast.makeText(context, text, length).show();
    }

    /**
     * 确认框，只有一个确认按钮
     */
    public static void confirmDialog(Context context, String title, String message,
                                     String positiveText,
                                     DialogInterface.OnClickListener positiveHandler) {
        AlertDialog.Builder bb = new AlertDialog.Builder(context);
        bb.setTitle(title);
        bb.setMessage(message);
        bb.setPositiveButton(positiveText, positiveHandler);
        bb.show();
    }

    /**
     * 确认框
     */
    public static void confirmDialog(Context context, String title, String message,
                                     String positiveText, String negativeText,
                                     DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler) {
        AlertDialog.Builder bb = new AlertDialog.Builder(context);
        bb.setTitle(title);
        bb.setMessage(message);
        bb.setPositiveButton(positiveText, positiveHandler);
        bb.setNegativeButton(negativeText, negativeHandler);
        bb.show();
    }

}
