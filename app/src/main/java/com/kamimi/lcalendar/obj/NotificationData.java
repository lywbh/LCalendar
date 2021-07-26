package com.kamimi.lcalendar.obj;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class NotificationData {

    public enum NotifyType {
        SINGLE, // 单次
        DAILY   // 每日
    }

    /**
     * 日期，yyyy-MM-dd
     */
    private String date;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 通知时间，hh:m:ss
     */
    private String notifyTime;

    /**
     * 通知类型
     */
    private NotifyType notifyType;

    /**
     * 是否打开通知
     */
    private boolean notifyOn;

}
