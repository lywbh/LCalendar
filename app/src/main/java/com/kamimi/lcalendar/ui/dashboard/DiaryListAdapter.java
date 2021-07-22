package com.kamimi.lcalendar.ui.dashboard;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.obj.DiaryPreview;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {

    private final RecyclerView recyclerView;
    private final LayoutInflater mInflater;

    /** 暴露列表数据给外部进行添加删除操作 */
    public final List<DiaryPreview> mPreviews;

    private final Typeface ldzsFont;
    private final Typeface laksFont;

    public DiaryListAdapter(Context context, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.mInflater = LayoutInflater.from(context);
        this.mPreviews = new ArrayList<>();
        ldzsFont = Typeface.createFromAsset(context.getAssets(), "fonts/ldzs.ttf");
        laksFont = Typeface.createFromAsset(context.getAssets(), "fonts/LaksOner.ttf");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.diary_list_item_holder, parent, false);
        view.getLayoutParams().height = recyclerView.getHeight() / 4;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView dateText = holder.item_tv.findViewWithTag("diary_date");
        TextView previewText = holder.item_tv.findViewWithTag("diary_preview");
        dateText.setTypeface(laksFont);
        dateText.setText(mPreviews.get(position).getDate());
        previewText.setTypeface(ldzsFont);
        previewText.setText(mPreviews.get(position).getContent());
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
