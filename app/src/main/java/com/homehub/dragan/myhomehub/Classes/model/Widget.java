package com.homehub.dragan.myhomehub.Classes.model;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;

public class Widget extends Entity {

    @SerializedName("appWidgetId")
    public Integer appWidgetId;

    public static Widget getInstance(Entity entity, int appWidgetId) {
        Widget widget = CommonUtil.inflate(CommonUtil.deflate(entity), Widget.class);
        widget.appWidgetId = appWidgetId;
        return widget;
    }

    public static Widget getInstance(Cursor cursor) {
        Widget widget = CommonUtil.inflate(cursor.getString(cursor.getColumnIndex("RAW_JSON")), Widget.class);
        return widget;
    }

}