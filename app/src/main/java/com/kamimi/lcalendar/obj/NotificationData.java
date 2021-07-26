package com.kamimi.lcalendar.obj;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class NotificationData {

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
     * 通知时间，hh:m:ss，如果为null表示没有开启通知
     */
    private String notifyTime;

}
