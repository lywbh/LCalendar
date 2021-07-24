package com.kamimi.lcalendar.obj;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiaryPreview {

    public static final int VIEW_TYPE_VIRTUAL = 1;  // 今天没有日记时，系统生成的空项
    public static final int VIEW_TYPE_NORMAL = 2;   // 普通日记项

    /**
     * 日记日期
     */
    private String date;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 类型 1-虚拟项 2-普通项
     */
    private int type;

    public static DiaryPreview createVirtual(String date) {
        return new DiaryPreview(date, "今天还没有写日记哟，点我开始吧~", VIEW_TYPE_VIRTUAL);
    }

    public static DiaryPreview createNormal(String date, String content) {
        return new DiaryPreview(date, content, VIEW_TYPE_NORMAL);
    }

}
