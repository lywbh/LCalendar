package com.kamimi.lcalendar.obj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 通知时间，hh:mm
     */
    private String notifyTime;

    /**
     * 是否打开通知
     */
    private boolean notifyOn;

}
