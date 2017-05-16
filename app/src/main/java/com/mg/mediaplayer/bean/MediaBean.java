package com.mg.mediaplayer.bean;

import java.io.Serializable;

/**
 * Created by YJ on 2017/5/15.
 */

public class MediaBean implements Serializable{
    /**
     * 文件路径
     */
    String path;
    /**
     * 文件时长
     */
    long duration;
    /**
     * 文件大小
     */
    long size;

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "MediaBean{" +
                "path='" + path + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}
