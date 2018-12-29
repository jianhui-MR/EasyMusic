package com.bobby.musiczone.Manager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.bobby.musiczone.service.PlayerService;

import static android.content.Context.AUDIO_SERVICE;


public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager audioManager;
    private boolean isPausedByFocusLossTransient;
    private Intent intent;
    private PlayerService service;

    public AudioFocusManager(PlayerService service) {
        audioManager = (AudioManager) service.getSystemService(AUDIO_SERVICE);
        this.service=service;
    }

    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    intent=new Intent(service.PAUSEORPLAY_ACTION);
                    service.sendBroadcast(intent);
                }

                // 恢复音量
                service.playerEngine.setVolume(1f, 1f);

                isPausedByFocusLossTransient = false;
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                intent=new Intent(service.PAUSEORPLAY_ACTION);
                service.sendBroadcast(intent);
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                intent=new Intent(service.PAUSEORPLAY_ACTION);
                service.sendBroadcast(intent);
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                service.playerEngine.setVolume(0.5f, 0.5f);
                break;
        }
    }
}
