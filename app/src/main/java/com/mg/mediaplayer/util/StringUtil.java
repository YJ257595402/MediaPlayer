package com.mg.mediaplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.mg.utils.tool.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by YJ on 2017/5/14.
 */

public class StringUtil {
    /**
     * 将long类型的时间转为01:22:33
     *
     * @param duration
     * @return
     */
    public static String formatVideoDuration(long duration) {
        int HOUR = 1000 * 60 * 60;//1小时
        int MINUTE = 1000 * 60;//1分钟
        int SECOND = 1000;//1秒
        int remainTime = 0;
        //1.算出多少小时
        int hour = (int) (duration / HOUR);//算出多少小时
        remainTime = (int) (duration % HOUR);//得到算完小时后剩余的时间

        //2.拿算完小时后剩余的时间，再算分钟
        int minute = remainTime / MINUTE;//算分钟
        remainTime = remainTime % MINUTE;//得到算完分钟后剩余的时间

        //3.拿算完分钟后剩余的时间，再算秒
        int second = remainTime / SECOND;//算秒

        if (hour == 0) {
            //显示22:33
            return String.format("%02d:%02d", minute, second);
        } else {
            //显示01:22:33
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }

    /**
     * 获得格式化之后的系统时间：00:00:00
     *
     * @return
     */
    public static String formatSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 去掉文件后缀：将滴答.mp3转为滴答
     *
     * @param audioName
     * @return
     */
    public static String formatAudioName(String audioName) {
        return audioName.substring(0, audioName.lastIndexOf("."));
    }

    /**
     * 获取视频的缩略图
     *
     * @param path
     * @return
     */
    public static Bitmap getVideoThumbnail(String path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            bitmap = retriever.getFrameAtTime();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取专辑图片
     *
     * @param path
     * @return
     */
    public static Bitmap getAudioAlbum(String path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            byte[] buffer = retriever.getEmbeddedPicture();
            if (buffer == null)
                return null;
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
                LogUtil.e("900","---------------------jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj----------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
     /* public static Bitmap getAudioThumbnail(String path) {
        Bitmap bitmap = null;
        getAl
        return bitmap;
    }

    int album_id = currentCursor.getInt(currentCursor
.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
String albumArt = getAlbumArt(album_id);
Bitmap bm = null;
if (albumArt == null) {
mImageView.setBackgroundResource(R.drawable.staring);
} else {
bm = BitmapFactory.decodeFile(albumArt);
BitmapDrawable bmpDraw = new BitmapDrawable(bm);
mImageView.setImageDrawable(bmpDraw);
}
private String getAlbumArt(int album_id) {
String mUriAlbums = "content://media/external/audio/albums";
String[] projection = new String[] { "album_art" };
Cursor cur = this.getContentResolver().query(
Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
projection, null, null, null);
String album_art = null;
if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
cur.moveToNext();
album_art = cur.getString(0);
}
cur.close();
cur = null;
return album_art;
}
     */
}
