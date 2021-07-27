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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationListAdapter extends PileLayout.Adapter {

    private final Context context;

    /**
     * 操作这个即可控制列表项内容
     */
    private List<NotificationData> dataList;

    private final PileLayout pileLayout;

    public NotificationListAdapter(Context context, PileLayout pileLayout) {
        this.context = context;
        this.pileLayout = pileLayout;

        // 初始化数据
        SharedPreferences notificationSp = this.context.getSharedPreferences("LCalendarNotificationSp", Context.MODE_PRIVATE);
        //notificationSp.edit().remove("LCalendarNotificationSp").commit();
        Set<String> jsonStrSet = notificationSp.getStringSet("LCalendarNotificationSp", new HashSet<>());
        this.dataList = jsonStrSet.stream()
                .map(jsonStr -> JSON.toJavaObject(JSONObject.parseObject(jsonStr), NotificationData.class))
                .sorted(Comparator.comparing(NotificationData::getDate))
                .collect(Collectors.toList());
    }

    /**
     * 刷新UI
     */
    public void reloadDataList(List<NotificationData> dataList) {
        this.dataList = dataList;
        pileLayout.notifyDataSetChanged();
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

}
