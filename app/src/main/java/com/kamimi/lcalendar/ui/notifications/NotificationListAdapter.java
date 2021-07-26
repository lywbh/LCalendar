package com.kamimi.lcalendar.ui.notifications;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.obj.NotificationData;
import com.kamimi.lcalendar.utils.FontLoader;
import com.stone.pile.libs.PileLayout;

import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends PileLayout.Adapter {

    /**
     * 操作这个即可控制列表项内容
     */
    private final List<NotificationData> dataList;

    public NotificationListAdapter() {
        dataList = new ArrayList<>();
        dataList.add(NotificationData.builder().date("2021-7-25").title("视频会议").content("啥hi打活动却无法报复却费服不服偶尔放不确定机器吧额分贝舞of各部分群殴负担不起微积分表哦国标").build());
        dataList.add(NotificationData.builder().date("2021-7-26").title("12345").content("啥hi打活动却无法报复却费服不服偶尔放不确定机器吧额分贝舞of各部分群殴负担不起微积分表哦国标").build());
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
    }

}
