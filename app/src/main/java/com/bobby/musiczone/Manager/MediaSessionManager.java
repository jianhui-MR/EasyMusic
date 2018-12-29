package com.bobby.musiczone.Manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.entry.LocalMusic;
import com.bobby.musiczone.service.PlayerService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bobby on 2018/7/19.
 */

public class MediaSessionManager {
    private static final String TAG = "MediaSessionManager";
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private PlayerService mPlayService;
    private MediaSessionCompat mMediaSession;
    private Bitmap AlbumBitmap;

    public MediaSessionManager(PlayerService playService) {
        mPlayService = playService;
        setupMediaSession();
    }

    /**
     * 初始化并激活MediaSession
     */
    private void setupMediaSession() {
        mMediaSession = new MediaSessionCompat(mPlayService, TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setCallback(callback);
        mMediaSession.setActive(true);
    }

    /**
     * 更新播放状态，播放/暂停/拖动进度条时调用
     */
    public void updatePlaybackState() {
        int state = (mPlayService.playerEngine.isPlaying()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mMediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, 0, 0)
                        .build());
    }

    /**
     * 更新正在播放的音乐信息，切换歌曲时调用
     */
    public void updateLOCALMetaData(LocalMusic song) {
        if (song == null) {
            mMediaSession.setMetadata(null);
            return;
        }
        if (AlbumBitmap != null)
            AlbumBitmap.recycle();
        //判断歌曲封面是否为空值
        if (song.getAlbumArt()==null) {
            //回收bitmap,防止OOM
            AlbumBitmap = null;
        }
        else
            AlbumBitmap=BitmapFactory.decodeFile(song.getAlbumArt());

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,song.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getSinger())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbumName())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,AlbumBitmap);
        mMediaSession.setMetadata(metaData.build());
    }

    public void updateONLINEMetaData(OnlineMusic music)
    {
        if (music==null)
        {
            mMediaSession.setMetadata(null);
            return;
        }
        //回收bitmap,防止OOM
        if (AlbumBitmap != null)
            AlbumBitmap.recycle();
        AlbumBitmap = null;
        try {
            URL picUrl=new URL(music.picUrl);
            Log.e(TAG, "updateONLINEMetaData: "+music.picUrl );
            HttpURLConnection connection;
            connection=(HttpURLConnection)picUrl.openConnection();
//            connection.setDoInput(true);
            InputStream is = connection.getInputStream();
            AlbumBitmap=BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,music.name)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.artistsList.get(0).singer)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.album.albumname)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,AlbumBitmap);

        mMediaSession.setMetadata(metaData.build());
    }

    /**
     * 释放MediaSession，退出播放器时调用
     */
    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            Intent intent=new Intent(mPlayService.PAUSEORPLAY_ACTION);
            mPlayService.sendBroadcast(intent);
        }

        @Override
        public void onPause() {
            Intent intent=new Intent(mPlayService.PAUSEORPLAY_ACTION);
            mPlayService.sendBroadcast(intent);
        }

        @Override
        public void onSkipToNext() {
           Intent intent=new Intent(mPlayService.NEXT_ACTION);
           mPlayService.sendBroadcast(intent);
        }

        @Override
        public void onSkipToPrevious() {
            Intent intent=new Intent(mPlayService.PREVIOUS_ACTION);
            mPlayService.sendBroadcast(intent);
        }

        @Override
        public void onStop() {
            //mPlayService.stop();
        }

        @Override
        public void onSeekTo(long pos) {
            //mPlayService.seekTo((int) pos);
        }
    };
}
