package com.kamimi.lcalendar.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * 系统弹窗工具类
 */
public class DialogUtils {

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
     * 确认框（无标题&只有一个按钮）
     */
    public static void confirmDialog(Context context, String message,
                                     String positiveText, DialogInterface.OnClickListener positiveHandler) {
        confirmDialog(context, null, message, positiveText, positiveHandler);
    }

    /**
     * 确认框（只有一个按钮）
     */
    public static void confirmDialog(Context context, String title, String message,
                                     String positiveText, DialogInterface.OnClickListener positiveHandler) {
        confirmDialog(context, title, message, positiveText, null, positiveHandler, null);
    }

    /**
     * 确认框（无标题&默认按钮&只定义确认按钮的回调）
     */
    public static void confirmDialog(Context context, String message,
                                     DialogInterface.OnClickListener positiveHandler) {
        confirmDialog(context, null, message, positiveHandler, null);
    }

    /**
     * 确认框（无标题&默认按钮）
     */
    public static void confirmDialog(Context context, String message,
                                     DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler) {
        confirmDialog(context, null, message, positiveHandler, negativeHandler);
    }

    /**
     * 确认框（默认按钮）
     */
    public static void confirmDialog(Context context, String title, String message,
                                     DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler) {
        confirmDialog(context, title, message, "确认", "取消", positiveHandler, negativeHandler);
    }

    /**
     * 确认框
     */
    public static void confirmDialog(Context context, String title, String message,
                                     String positiveText, String negativeText,
                                     DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveHandler)
                .setNegativeButton(negativeText, negativeHandler)
                .show();
    }

}
