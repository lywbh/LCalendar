package com.kamimi.lcalendar.ui.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.Utils;
import com.kamimi.lcalendar.obj.DiaryPreview;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kamimi.lcalendar.obj.GlobalConstants.BLANK_GAP;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {

    private final Context context;

    private final RecyclerView recyclerView;
    private final View diaryDetailView;
    private final LayoutInflater mInflater;

    /** 操作这个即可控制列表项内容 */
    private final List<DiaryPreview> mPreviews;

    // 字体
    private final Typeface ldzsFont;
    private final Typeface laksFont;

    // 滑动动画
    private final ValueAnimator slideAnimator;

    public DiaryListAdapter(Context context, RecyclerView recyclerView, View diaryDetailView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.diaryDetailView = diaryDetailView;
        this.mInflater = LayoutInflater.from(context);
        this.mPreviews = new ArrayList<>();
        ldzsFont = Typeface.createFromAsset(context.getAssets(), "fonts/ldzs.ttf");
        laksFont = Typeface.createFromAsset(context.getAssets(), "fonts/LaksOner.ttf");
        // 弹出层滑动动画
        View diaryDetailPanel = diaryDetailView.findViewById(R.id.diary_detail_panel);
        slideAnimator = ValueAnimator.ofFloat(0, 5);
        slideAnimator.setDuration(300);
        slideAnimator.setRepeatMode(ValueAnimator.RESTART);
        slideAnimator.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            diaryDetailPanel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, currentValue));
            diaryDetailPanel.requestLayout();
        });
        slideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 滑动结束后如果是隐藏状态，把弹层关闭
                float currentValue = (float) ((ValueAnimator) animation).getAnimatedValue();
                if (currentValue == 0) {
                    diaryDetailView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 刷新列表
     */
    public void reloadDiaryList() {
        // 日记数据库
        SharedPreferences diarySp = context.getSharedPreferences("LCalendarDiarySp", Context.MODE_PRIVATE);
        //diarySp.edit().putString("2021-07-22", "2021-07-22").putString("2021-07-21", "2021-07-21").putString("2021-07-20", "2021-07-20").putString("2021-07-19", "2021-07-19").commit();
        String[] dateList = diarySp.getAll().keySet().toArray(new String[0]);
        Arrays.sort(dateList);
        // 插入列表项
        mPreviews.clear();
        String todayStr = Utils.dateFormat(new Date(), "yyyy-M-dd");
        if (dateList.length <= 0 || !todayStr.equals(dateList[0])) {
            // 今天还没有日记，先生成一个空项放最前面
            mPreviews.add(new DiaryPreview(todayStr, "今天还没有写日记哟~"));
        }
        for (int i = dateList.length - 1; i >= 0; --i) {
            // 日期从新到旧插入到列表中
            String content = diarySp.getString(dateList[i], "出现了一些错误！");
            mPreviews.add(new DiaryPreview(dateList[i], content));
        }
        // TODO 数据变化和UI动画同时触发会有bug
        notifyDataSetChanged();
        restoreUI();
    }

    private void restoreUI() {
        // 隐藏所有删除按钮
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            Button deleteButton = recyclerView.getChildAt(i).findViewWithTag("diary_delete_button");
            float buttonWeight = ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).weight;
            if (buttonWeight == 3) {
                buttons.add(deleteButton);
            }
        }
        ValueAnimator va = ValueAnimator.ofFloat(3, 0);
        va.setDuration(150);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            for (Button button : buttons) {
                button.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, currentValue));
                button.requestLayout();
            }
        });
        va.start();
    }

    // 滑动相关参数
    private volatile float startPosX, lastPosX;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.diary_list_item_holder, parent, false);
        view.getLayoutParams().height = recyclerView.getHeight() / 4;
        View viewItem = view.findViewById(R.id.diary_list_item);
        // 日记数据库
        SharedPreferences diarySp = context.getSharedPreferences("LCalendarDiarySp", Context.MODE_PRIVATE);
        // 点击列表项弹出层，并初始化内容和绑定各种点击事件
        viewItem.setOnClickListener(v -> {
            View diaryDetailPanel = diaryDetailView.findViewById(R.id.diary_detail_panel);
            View diaryEditorPanel = diaryDetailView.findViewById(R.id.diary_editor_panel);
            LoaderTextView diaryDetailText = diaryDetailPanel.findViewById(R.id.diary_detail_text);
            EditText diaryEditorText = diaryEditorPanel.findViewById(R.id.diary_editor_text);
            diaryDetailText.setTypeface(ldzsFont);
            diaryEditorText.setTypeface(ldzsFont);
            View diaryShade = diaryDetailView.findViewById(R.id.diary_shade);
            // 内容填充到详情页
            String date = ((TextView) v.findViewWithTag("diary_date")).getText().toString();
            // 从数据库获取当天的日记内容
            String content = diarySp.getString(date, "记录下你的愿望吧~");
            diaryDetailText.setText(content);
            // 编辑框失焦时隐藏软键盘
            diaryEditorText.setOnFocusChangeListener((v1, hasFocus) -> {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(diaryEditorText.getWindowToken(), 0);
                }
            });
            // 点击编辑
            diaryDetailPanel.findViewById(R.id.diary_edit_button).setOnClickListener(w -> {
                diaryEditorText.setText(diaryDetailText.getText());
                diaryDetailPanel.setVisibility(View.GONE);
                diaryEditorPanel.setVisibility(View.VISIBLE);
                // 编辑态点击蒙层 编辑框失焦
                diaryShade.setOnClickListener(u -> diaryEditorText.clearFocus());
            });
            // 点击保存
            diaryEditorPanel.findViewById(R.id.diary_submit_button).setOnClickListener(w -> {
                Editable editorText = diaryEditorText.getText();
                if (editorText.length() == 0) {
                    Toast.makeText(context, "写点什么再保存吧~", Toast.LENGTH_SHORT).show();
                } else {
                    diarySp.edit().putString(date, editorText.toString()).commit();
                    reloadDiaryList();
                    diaryDetailText.setText(editorText);
                    diaryDetailPanel.setVisibility(View.VISIBLE);
                    diaryEditorPanel.setVisibility(View.GONE);
                    // 默认点击蒙层关闭页面
                    diaryShade.setOnClickListener(u -> slideAnimator.reverse());
                }
            });
            // 点击取消
            diaryEditorPanel.findViewById(R.id.diary_cancel_button).setOnClickListener(w -> {
                diaryDetailPanel.setVisibility(View.VISIBLE);
                diaryEditorPanel.setVisibility(View.GONE);
                // 默认点击蒙层关闭页面
                diaryShade.setOnClickListener(u -> slideAnimator.reverse());
            });
            // 默认点击蒙层关闭页面
            diaryShade.setOnClickListener(w -> slideAnimator.reverse());
            // 默认展示详情页，屏蔽编辑页
            diaryDetailPanel.setVisibility(View.VISIBLE);
            diaryEditorPanel.setVisibility(View.GONE);
            // 显示该弹出层
            diaryDetailView.setVisibility(View.VISIBLE);
            slideAnimator.start();
        });
        // 滑动列表项，左滑显示删除按钮
        Button deleteButton = view.findViewWithTag("diary_delete_button");
        viewItem.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float currentWeight;
                    startPosX = lastPosX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentWeight = ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).weight;
                    float weight = (lastPosX - event.getX()) / 100 + currentWeight;
                    if (weight > 3) weight = 3;
                    else if (weight < 0) weight = 0;
                    deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
                    deleteButton.requestLayout();
                    lastPosX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(startPosX - event.getX()) < 10) {
                        // 移动幅度小视为点击
                        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0));
                        deleteButton.requestLayout();
                        v.performClick();
                    }
                case MotionEvent.ACTION_CANCEL:
                    currentWeight = ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).weight;
                    if (currentWeight >= 1.5) weight = 3;
                    else weight = 0;
                    ValueAnimator va = ValueAnimator.ofFloat(currentWeight, weight);
                    va.addUpdateListener(animation -> {
                        float currentValue = (float) animation.getAnimatedValue();
                        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, currentValue));
                        deleteButton.requestLayout();
                    });
                    va.start();
                    break;
            }
            return true;
        });
        deleteButton.setOnClickListener(b -> {
            String date = ((TextView) viewItem.findViewWithTag("diary_date")).getText().toString();
            diarySp.edit().remove(date).commit();
            reloadDiaryList();
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView dateText = holder.item_tv.findViewWithTag("diary_date");
        TextView previewText = holder.item_tv.findViewWithTag("diary_preview");
        dateText.setTypeface(laksFont);
        dateText.setText(mPreviews.get(position).getDate());
        previewText.setTypeface(ldzsFont);
        String content = BLANK_GAP + mPreviews.get(position).getContent();
        previewText.setText(content);
    }

    @Override
    public int getItemCount() {
        return mPreviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_tv;

        public ViewHolder(View view) {
            super(view);
            item_tv = view.findViewById(R.id.diary_list_item);
        }
    }

}
