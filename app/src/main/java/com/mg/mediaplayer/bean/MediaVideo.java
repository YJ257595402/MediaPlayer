package com.mg.mediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by YJ on 2017/5/15.
 */

public class MediaVideo extends MediaBean{

    private String title;


    public MediaVideo() {
    }

    /**
     * 根据获取的系统视屏，查询对应信息
     * @param cursor
     * @return
     */
    public static MediaVideo fromCursor(Cursor cursor) {
        MediaVideo videoBean = new MediaVideo();
        videoBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        videoBean.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        videoBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        videoBean.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        return videoBean;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "MediaVideo{" +
                "title='" + title + '\'' +
                '}';
    }
}
