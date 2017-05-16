package com.mg.mediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by YJ on 2017/5/15.
 */

public class MediaAudio extends MediaBean{
    /**
     * 演唱者
     */
    private String artist;
    /**
     * 音乐名
     */
    private String title;
    /**
     * 专辑图片
     */
    private String album;

    public MediaAudio() {
    }

    /**
     * 根据获取的系统视屏，查询对应信息
     *
     * @param cursor
     * @return
     */
    public static MediaAudio fromCursor(Cursor cursor) {
        MediaAudio mediaAudio = new MediaAudio();
        mediaAudio.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        mediaAudio.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        mediaAudio.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        mediaAudio.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
//        mediaAudio.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));

        return mediaAudio;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getArtist() {
        return artist;
    }


    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }


    public String getAlbum() {
        return album;
    }

    @Override
    public String toString() {
        return "AudioContent{" +
                "size=" + size +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", path='" + path + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
