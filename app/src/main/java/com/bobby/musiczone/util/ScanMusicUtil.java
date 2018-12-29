package com.bobby.musiczone.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.bobby.musiczone.entry.LocalMusic;

import org.litepal.crud.DataSupport;


/**
 * Created by Bobby on 2018/7/13.
 */

public class ScanMusicUtil {
    private Context context;
    public void query(Context mContext) {
        context=mContext;
        DataSupport.deleteAll(LocalMusic.class);
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocalMusic song = new LocalMusic();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                int AlbumId=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String albumArt=getAlbumArt(AlbumId);
                song.setAlbumName(albumName);
                song.setAlbumArt(albumArt);
                song.setTime(formatTime(duration));
                song.setName(name);
                song.setSrcPath(path);
                if (size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getName().contains("-")) {
                        String[] str = song.getName().split(" - ");
                        song.setSinger(str[0]);
                        song.setName(str[1].split(".m")[0]);
                    }
                }
                song.save();
            }
        }
        // 释放资源
        cursor.close();
    }

    //歌曲时间转换
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
    //获取专辑封面
    private String getAlbumArt(int album_id)

    {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = context.getContentResolver().query(  Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),  projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0)
        {  cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }

}
