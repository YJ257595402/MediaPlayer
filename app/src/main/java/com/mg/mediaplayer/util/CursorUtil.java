package com.mg.mediaplayer.util;

import android.database.Cursor;

import com.mg.utils.tool.LogUtil;

/**
 * Created by YJ on 2017/5/14.
 */

public class CursorUtil {
    public static void printCursor(Cursor cursor){
        if(cursor==null)return;
        LogUtil.e("CursorUtil", "共"+cursor.getCount()+"条数据");

        while(cursor.moveToNext()){
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String columnValue = cursor.getString(i);
                LogUtil.e("CursorUtil", columnName +" : "+columnValue);
            }
            LogUtil.e("CursorUtil", " =========================== ");
        }
    }
}
