package com.kamimi.lcalendar.ui.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.kamimi.lcalendar.utils.DialogUtils;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.utils.CommonUtils;
import com.kamimi.lcalendar.obj.DiaryPreview;
import com.kamimi.lcalendar.utils.FontLoader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kamimi.lcalendar.obj.GlobalConstants.BLANK_GAP;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryViewHolder> {

    private final Context context;

    private final RecyclerView recyclerView;
    private final View diaryDetailView;
    private final LayoutInflater mInflater;

    /**
     * 操作这个即可控制列表项内容
     */
    private final List<DiaryPreview> mPreviews;

    // 滑动动画
    private final ValueAnimator slideAnimator;

    /**
     * 构造函数
     */
    public DiaryListAdapter(Context context, RecyclerView recyclerView, View diaryDetailView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.diaryDetailView = diaryDetailView;
        this.mInflater = LayoutInflater.from(context);
        this.mPreviews = new ArrayList<>();
        // 弹出层滑动动画
        View diaryMainDiv = diaryDetailView.findViewById(R.id.diary_main_div);
        slideAnimator = ValueAnimator.ofFloat(0, 5);
        slideAnimator.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            diaryMainDiv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, currentValue));
            diaryMainDiv.requestLayout();
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
        //diarySp.edit().putString("2021-07-22", "2021-07-22").putString("2021-07-21", "2021-07-21").putString("2021-07-20", "2021-07-20").putString("2021-07-19", "2021-07-19").putString("2021-07-18", "2021-07-18").commit();
        String[] dateList = diarySp.getAll().keySet().toArray(new String[0]);
        Arrays.sort(dateList);
        // 插入列表项
        mPreviews.clear();
        String todayStr = CommonUtils.dateFormat(new Date(), "yyyy-M-dd");
        if (dateList.length <= 0 || !todayStr.equals(dateList[dateList.length - 1])) {
            // 今天还没有日记，先生成一个空项放最前面
            mPreviews.add(DiaryPreview.createVirtual(todayStr));
        }
        for (int i = dateList.length - 1; i >= 0; --i) {
            // 日期从新到旧插入到列表中
            String content = diarySp.getString(dateList[i], "");
            mPreviews.add(DiaryPreview.createNormal(dateList[i], content));
        }
        notifyDataSetChanged();
    }

    // 滑动相关参数
    private volatile float touchStartPosX, touchStartWeight;

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.diary_list_item_holder, parent, false);
        view.getLayoutParams().height = recyclerView.getHeight() / 5; // 列表项高度
        DiaryViewHolder holder = new DiaryViewHolder(view); // 创建holder

        View viewItem = view.findViewById(R.id.diary_list_item); // 左侧的内容
        Button deleteButton = view.findViewWithTag("diary_delete_button"); // 右侧的删除按钮
        SharedPreferences diarySp = context.getSharedPreferences("LCalendarDiarySp", Context.MODE_PRIVATE); // 日记数据库

        // 列表项点击事件
        viewItem.setOnClickListener(v -> {
            View diaryDetailPanel = diaryDetailView.findViewById(R.id.diary_detail_panel);
            View diaryEditorPanel = diaryDetailView.findViewById(R.id.diary_editor_panel);
            LoaderTextView diaryDetailText = diaryDetailPanel.findViewById(R.id.diary_detail_text);
            EditText diaryEditorText = diaryEditorPanel.findViewById(R.id.diary_editor_text);
            diaryDetailText.setTypeface(FontLoader.ldzsFont);
            diaryEditorText.setTypeface(FontLoader.ldzsFont);
            View diaryShade = diaryDetailView.findViewById(R.id.diary_shade);
            // 日记内容填充到界面上
            String date = ((TextView) v.findViewWithTag("diary_date")).getText().toString();
            String content = diarySp.getString(date, "");
            diaryDetailText.setText(content);
            // 编辑框焦点变更
            diaryEditorText.setOnFocusChangeListener((v1, hasFocus) -> {
                if (!hasFocus) {
                    // 失焦时关闭软键盘
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(diaryEditorText.getWindowToken(), 0);
                }
            });
            // 点击编辑
            diaryDetailPanel.findViewById(R.id.diary_edit_button).setOnClickListener(w -> {
                diaryEditorText.setText(diaryDetailText.getText());
                diaryDetailPanel.setVisibility(View.GONE);
                diaryEditorPanel.setVisibility(View.VISIBLE);
                // 编辑态点击蒙层不关闭页面 而是让编辑框失焦
                diaryShade.setOnClickListener(u -> diaryEditorText.clearFocus());
            });
            // 点击保存
            diaryEditorPanel.findViewById(R.id.diary_submit_button).setOnClickListener(w -> {
                Editable editorText = diaryEditorText.getText();
                if (editorText.length() == 0) {
                    DialogUtils.toast(context, "写点什么再保存吧~");
                } else {
                    diarySp.edit().putString(date, editorText.toString()).apply();
                    diaryDetailText.setText(editorText);
                    diaryDetailPanel.setVisibility(View.VISIBLE);
                    diaryEditorPanel.setVisibility(View.GONE);
                    // 展示态点击蒙层关闭页面
                    diaryShade.setOnClickListener(u -> {
                        slideAnimator.reverse();
                        reloadDiaryList();
                    });
                    DialogUtils.toast(context, "保存成功");
                }
            });
            // 点击取消
            diaryEditorPanel.findViewById(R.id.diary_cancel_button).setOnClickListener(w -> DialogUtils.confirmDialog(context, "放弃编辑？", (dialog, which) -> {
                if (viewType == DiaryPreview.VIEW_TYPE_VIRTUAL) {
                    // 如果是新建日记，点取消直接把弹出层关了
                    slideAnimator.reverse();
                } else if (viewType == DiaryPreview.VIEW_TYPE_NORMAL) {
                    // 其他情况从编辑态回到展示态
                    diaryDetailPanel.setVisibility(View.VISIBLE);
                    diaryEditorPanel.setVisibility(View.GONE);
                    // 展示态点击蒙层关闭页面
                    diaryShade.setOnClickListener(u -> {
                        slideAnimator.reverse();
                        reloadDiaryList();
                    });
                }
            }));
            // 新建日记进入编辑态，否则进入展示态
            if (viewType == DiaryPreview.VIEW_TYPE_VIRTUAL) {
                diaryEditorText.setText("");
                diaryDetailPanel.setVisibility(View.GONE);
                diaryEditorPanel.setVisibility(View.VISIBLE);
                // 编辑态点击蒙层不关闭页面 而是让编辑框失焦
                diaryShade.setOnClickListener(u -> diaryEditorText.clearFocus());
            } else if (viewType == DiaryPreview.VIEW_TYPE_NORMAL) {
                diaryDetailPanel.setVisibility(View.VISIBLE);
                diaryEditorPanel.setVisibility(View.GONE);
                // 展示态点击蒙层关闭页面
                diaryShade.setOnClickListener(w -> {
                    slideAnimator.reverse();
                    reloadDiaryList();
                });
            }
            // 显示该弹出层
            diaryDetailView.setVisibility(View.VISIBLE);
            slideAnimator.start();
        });

        // 列表项滑动事件
        if (viewType == DiaryPreview.VIEW_TYPE_VIRTUAL) {
            // 虚拟项，不允许左滑删除
            viewItem.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 触摸开始时隐藏其他的删除按钮
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            Button button = recyclerView.getChildAt(i).findViewWithTag("diary_delete_button");
                            if (button != deleteButton) {
                                float buttonWeight = ((LinearLayout.LayoutParams) button.getLayoutParams()).weight;
                                if (buttonWeight > 0) {
                                    toggleWeightAnim(button, buttonWeight, 0);
                                }
                            }
                        }
                        // 记录触摸起点和触摸时按钮的宽度
                        touchStartPosX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(touchStartPosX - event.getX()) < 10) {
                            // 移动幅度很小则视为点击
                            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0));
                            deleteButton.requestLayout();
                            v.performClick();
                        }
                        break;
                }
                return true;
            });
        } else if (viewType == DiaryPreview.VIEW_TYPE_NORMAL) {
            // 普通项，左滑显示删除按钮
            viewItem.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 触摸开始时隐藏其他的删除按钮
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            Button button = recyclerView.getChildAt(i).findViewWithTag("diary_delete_button");
                            if (button != deleteButton) {
                                float buttonWeight = ((LinearLayout.LayoutParams) button.getLayoutParams()).weight;
                                if (buttonWeight > 0) {
                                    toggleWeightAnim(button, buttonWeight, 0);
                                }
                            }
                        }
                        // 记录触摸起点和触摸时按钮的宽度
                        touchStartPosX = event.getX();
                        touchStartWeight = ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).weight;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 按钮最多拖到长度为3，太长了不好看
                        float weight = (touchStartPosX - event.getX()) / 62 + touchStartWeight;
                        if (weight > 3) weight = 3;
                        else if (weight < 0) weight = 0;
                        // 按钮跟随手指
                        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
                        deleteButton.requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(touchStartPosX - event.getX()) < 10) {
                            // 移动幅度很小则视为点击
                            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0));
                            deleteButton.requestLayout();
                            v.performClick();
                        }
                        // 注意这里没有break
                    case MotionEvent.ACTION_CANCEL:
                        // 发生纵向移动或移出了边界，判断当前按钮挪出了多少，如果超过一半直接带出来，否则复位隐藏
                        float currentWeight = ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).weight;
                        if (currentWeight >= 1.5) weight = 3;
                        else weight = 0;
                        toggleWeightAnim(deleteButton, currentWeight, weight);
                        break;
                }
                return true;
            });
            // 点击删除按钮
            deleteButton.setOnClickListener(button -> DialogUtils.confirmDialog(context, "要删除这篇日记吗？", (dialog, which) -> {
                // 隐藏刚才划出来的删除按钮
                float currentWeight = ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).weight;
                toggleWeightAnim(deleteButton, currentWeight, 0);
                // 删除数据库
                String date = ((TextView) viewItem.findViewWithTag("diary_date")).getText().toString();
                diarySp.edit().remove(date).apply();
                int deletePos = holder.getAbsoluteAdapterPosition();
                if (CommonUtils.dateFormat(new Date(), "yyyy-M-dd").equals(date)) {
                    // 删掉的如果是今天的日记，复原一个虚拟项
                    mPreviews.set(deletePos, DiaryPreview.createVirtual(date));
                    notifyItemChanged(deletePos);
                } else {
                    mPreviews.remove(deletePos);
                    notifyItemRemoved(deletePos);
                }
            }));
        }

        return holder;
    }

    /**
     * 权重滑动动画
     */
    private void toggleWeightAnim(View view, float startWeight, float endWeight) {
        ValueAnimator va = ValueAnimator.ofFloat(startWeight, endWeight);
        va.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            view.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, currentValue));
            view.requestLayout();
        });
        va.start();
    }

    /**
     * 列表的展示项和数据内容的绑定，修改数据即可刷新UI
     */
    @Override
    public void onBindViewHolder(DiaryViewHolder holder, int position) {
        TextView dateText = holder.diaryItem.findViewWithTag("diary_date");
        TextView previewText = holder.diaryItem.findViewWithTag("diary_preview");
        // 设置字体
        dateText.setTypeface(FontLoader.laksFont);
        previewText.setTypeface(FontLoader.ldzsFont);
        // 取内容
        DiaryPreview mPreview = mPreviews.get(position);
        if (mPreview.getType() == DiaryPreview.VIEW_TYPE_VIRTUAL) {
            // 展示虚拟项
            previewText.setText(mPreview.getContent());
            previewText.setGravity(Gravity.CENTER);
        } else if (mPreview.getType() == DiaryPreview.VIEW_TYPE_NORMAL) {
            // 展示普通日记项
            dateText.setText(mPreview.getDate());
            String content = BLANK_GAP + mPreview.getContent();
            previewText.setText(content);
            previewText.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return mPreviews.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPreviews.get(position).getType();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout diaryItem;

        public DiaryViewHolder(View view) {
            super(view);
            diaryItem = view.findViewById(R.id.diary_list_item);
        }
    }

}
