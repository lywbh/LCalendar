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
     * 信息，指定展示多久
     */
    public static void toast(Context context, String text, int length) {
        Toast.makeText(context, text, length).show();
    }

    /**
     * 提示框（无标题，无按钮）
     */
    public static void confirmDialog(Context context, String message,
                                     DialogInterface.OnDismissListener dismissHandler) {
        confirmDialog(context, message, null, null, null, null, dismissHandler);
    }

    /**
     * 确认框（无标题，取消和关闭无回调）
     */
    public static void confirmDialog(Context context, String message,
                                     String positiveText, String negativeText,
                                     DialogInterface.OnClickListener positiveHandler) {
        confirmDialog(context, message, positiveText, negativeText, positiveHandler, null, null);
    }

    /**
     * 确认框（无标题）
     */
    public static void confirmDialog(Context context, String message,
                                     String positiveText, String negativeText,
                                     DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler,
                                     DialogInterface.OnDismissListener dismissHandler) {
        confirmDialog(context, null, message, positiveText, negativeText, positiveHandler, negativeHandler, dismissHandler);
    }

    /**
     * 确认框
     */
    public static void confirmDialog(Context context, String title, String message,
                                     String positiveText, String negativeText,
                                     DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler,
                                     DialogInterface.OnDismissListener dismissHandler) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveHandler)
                .setNegativeButton(negativeText, negativeHandler)
                .setOnDismissListener(dismissHandler)
                .show();
    }

}
