package com.kamimi.lcalendar.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kamimi.lcalendar.obj.GlobalConstants;

/**
 * 系统弹窗工具类
 */
public class DialogUtils {

    /**
     * 信息
     */
    public static Toast toast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    /**
     * 信息，指定展示多久
     */
    public static Toast toast(Context context, String text, int length) {
        Toast toast = Toast.makeText(context, text, length);
        toast.show();
        return toast;
    }

    /**
     * 提示框（无按钮）
     */
    public static AlertDialog confirm(Context context, String title, String message,
                                      DialogInterface.OnDismissListener dismissHandler) {
        return confirm(context, title, message, null, null, null, null, dismissHandler);
    }

    /**
     * 确认框（无标题，仅设置确认回调）
     */
    public static AlertDialog confirm(Context context, String message,
                                      String positiveText, String negativeText,
                                      DialogInterface.OnClickListener positiveHandler) {
        return confirm(context, message, positiveText, negativeText, positiveHandler, null, null);
    }

    /**
     * 确认框（无标题）
     */
    public static AlertDialog confirm(Context context, String message,
                                      String positiveText, String negativeText,
                                      DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler,
                                      DialogInterface.OnDismissListener dismissHandler) {
        return confirm(context, null, message, positiveText, negativeText, positiveHandler, negativeHandler, dismissHandler);
    }

    /**
     * 确认框
     */
    public static AlertDialog confirm(Context context, String title, String message,
                                      String positiveText, String negativeText,
                                      DialogInterface.OnClickListener positiveHandler, DialogInterface.OnClickListener negativeHandler,
                                      DialogInterface.OnDismissListener dismissHandler) {
        // 构造提示框
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton(positiveText, positiveHandler)
                .setNegativeButton(negativeText, negativeHandler)
                .setOnDismissListener(dismissHandler).create();
        // 设置标题
        if (title != null) {
            LinearLayout titleView = new LinearLayout(context);
            TextView titleTextView = new TextView(context);
            titleView.addView(titleTextView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 50, 50, 0);
            titleTextView.setLayoutParams(layoutParams);
            titleTextView.setTextSize(16);
            titleTextView.setTypeface(FontLoader.ldzsFont);
            titleTextView.setText(title);
            dialog.setCustomTitle(titleView);
        }
        // 设置内容
        if (message != null) {
            LinearLayout messageView = new LinearLayout(context);
            TextView messageTextView = new TextView(context);
            messageView.addView(messageTextView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 50, 50, 50);
            messageTextView.setLayoutParams(layoutParams);
            messageTextView.setTextSize(12);
            messageTextView.setTypeface(FontLoader.ldzsFont);
            String messageText = GlobalConstants.BLANK_GAP + message;
            messageTextView.setText(messageText);
            dialog.setView(messageView);
        }
        // 设置按钮字体，注意要先显示对话框才能找到按钮
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(FontLoader.ldzsFont);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(FontLoader.ldzsFont);
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTypeface(FontLoader.ldzsFont);
        });
        // 弹出对话框
        dialog.show();
        return dialog;
    }

}
