package com.rex.easymusic.Manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Bean.LocalMusic;
import com.rex.easymusic.service.PlayerService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MediaSession管理器
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
    private MediaControllerCompat mediaController;
    private Bitmap albumBitmap;
    private Handler handler=new Handler();

    public MediaSessionManager(PlayerService playService) {
        mPlayService = playService;
        setupMediaSession();
        try {
            mediaController=new MediaControllerCompat(playService,mMediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        if (albumBitmap != null)
            albumBitmap.recycle();
        //判断歌曲封面是否为空值
        if (song.getAlbumArt()==null) {
            //回收bitmap,防止OOM
            albumBitmap = null;
        }
        else
            albumBitmap=BitmapFactory.decodeFile(song.getAlbumArt());

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,song.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getSinger())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbumName())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,albumBitmap);
        mMediaSession.setMetadata(metaData.build());
        updatePlaybackState();
    }

    public void updateONLINEMetaData(OnlineMusic music)
    {
        if (music==null)
        {
            mMediaSession.setMetadata(null);
            return;
        }
        new Thread(){
            @Override
            public void run() {
                Log.e(TAG, "updateONLINEMetaData: "+music.getPicUrl() );
                HttpURLConnection connection;
                try {
                    URL picUrl=new URL(music.getPicUrl());
                    connection=(HttpURLConnection)picUrl.openConnection();
                    InputStream is = connection.getInputStream();
                    //回收bitmap,防止OOM
                    if (albumBitmap!=null)
                        albumBitmap.recycle();
                    albumBitmap=BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE,music.getName())
                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getSinger())
                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum())
                                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,albumBitmap);

                            mMediaSession.setMetadata(metaData.build());
                            updatePlaybackState();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
