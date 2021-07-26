package com.kamimi.lcalendar.ui.notifications;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kamimi.lcalendar.R;
import com.kamimi.lcalendar.obj.NotificationData;
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
        TextView titleView = view.findViewWithTag("notification_title");
        titleView.setText(dataList.get(index).getTitle());
    }

    @Override
    public void displaying(int position) {
        NotificationData data = dataList.get(position);
        Log.v("啊", data.toString());
    }

}
