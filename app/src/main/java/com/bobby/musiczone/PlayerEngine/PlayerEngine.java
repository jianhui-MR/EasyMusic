package com.bobby.musiczone.PlayerEngine;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.bobby.musiczone.service.PlayerService;

import java.io.IOException;

import static com.bobby.musiczone.service.PlayerService.pauseAction;
import static com.bobby.musiczone.service.PlayerService.plyingAction;

public class PlayerEngine extends MediaPlayer implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener {
    private PlayerService service;
    private Intent intent;
    private final String TAG="PlayerEngine";
    private boolean firstPlay=true;
    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        super.setDataSource(path);
        this.prepareAsync();
    }

    public PlayerEngine() {
        super();
        service=PlayerService.getService();
        this.setOnPreparedListener(this);
        this.setOnCompletionListener(this);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        service.audioFocusManager.requestAudioFocus();
        service.mediaSessionManager.updatePlaybackState();
        if (service.musicType==service.LOCAL)
            service.mediaSessionManager.updateLOCALMetaData(service.localMusicList.get(service.position));
        else if (service.musicType==service.ONLINE)
            service.mediaSessionManager.updateONLINEMetaData(service.onlineMusicList.get(service.position));

        intent=new Intent(plyingAction);
        service.sendBroadcast(intent);

        Log.e(TAG, "start: 音乐开始播放" );
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        Log.e(TAG, "pause: 音乐暂停" );
        service.mediaSessionManager.updatePlaybackState();
        service.sendBroadcast(new Intent(pauseAction));
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
        Log.e(TAG, "prepareAsync: 异步准备");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!firstPlay)
            mp.start();
        firstPlay=false;

        Log.e(TAG, "prepareAsync: 准备完毕，开始播放");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        service.playNext();
    }
}
