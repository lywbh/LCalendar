package com.kamimi.lcalendar.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.FontLoader;
import com.stone.pile.libs.PileLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kotlin.collections.SetsKt;

public class NotificationListAdapter extends PileLayout.Adapter {

    private final Context context;

    private final PileLayout pileLayout;

    /**
     * 操作这个即可控制列表项内容
     */
    private final List<NotificationData> dataList;

    public NotificationListAdapter(Context context, PileLayout pileLayout) {
        this.context = context;
        this.pileLayout = pileLayout;
        this.dataList = new ArrayList<>();
    }

    /**
     * 刷新列表
     */
    public void reloadDataList() {
        // 提醒数据库
        SharedPreferences notificationSp = context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        Set<String> s = SetsKt.setOf(JSONObject.toJSONString(NotificationData.builder().date("2021-7-25").title("标题一").content("内容哟~~~~~SDsdsdsd").notifyTime("15:30:15").build()),
                JSONObject.toJSONString(NotificationData.builder().date("2021-7-25").title("标题二").content("阿迪达斯哥哥哥我各位各位说对不起发布偶尔无纺布").notifyTime("15:30:15").build()));
        notificationSp.edit().putStringSet("LCalendarNotificationSp", s).commit();
        Set<String> jsonStrSet = notificationSp.getStringSet("LCalendarNotificationSp", new HashSet<>());
        // 插入列表项
        dataList.clear();
        for (String jsonStr : jsonStrSet) {
            NotificationData data = JSON.toJavaObject(JSONObject.parseObject(jsonStr), NotificationData.class);
            dataList.add(data);
        }
        // TODO 这个到底要不要
        // pileLayout.notifyDataSetChanged();
    }

    @Override
    public int getLayoutId() {
        return R.layout.notification_item;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void bindView(View view, int index) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        }

        TextView dateView = view.findViewWithTag("notification_date");
        //TextView switchView = view.findViewWithTag("notification_switch");
        TextView titleView = view.findViewWithTag("notification_title");
        TextView contentView = view.findViewWithTag("notification_content");
        dateView.setTypeface(FontLoader.ldzsFont);
        titleView.setTypeface(FontLoader.ldzsFont);
        contentView.setTypeface(FontLoader.ldzsFont);
        NotificationData dataItem = dataList.get(index);
        dateView.setText(dataItem.getDate());
        //switchView.setText(dataItem.getNotifyTime());
        titleView.setText(dataItem.getTitle());
        contentView.setText(dataItem.getContent());
    }

    @Override
    public void displaying(int position) {
        NotificationData data = dataList.get(position);
        Log.v(String.valueOf(position), JSONObject.toJSONString(data));
    }

    static class ViewHolder {
    }

}
