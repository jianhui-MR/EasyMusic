package com.rex.easymusic.service;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rex.easymusic.Activity.PlayMusicActivity;
import com.rex.easymusic.BroadCast.DownloadRecevier;
import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.Manager.AudioFocusManager;
import com.rex.easymusic.Manager.MediaSessionManager;
import com.rex.easymusic.MessageEvent.MessageEvent;
import com.rex.easymusic.PlayerEngine.PlayerEngine;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Bean.LocalMusic;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ipAddressUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class PlayerService extends Service {
    public List<LocalMusic> localMusicList=DataSupport.findAll(LocalMusic.class);
    public List<OnlineMusic> onlineMusicList;

    public final String PLAY_LOCALMUSIC_ACTION="PlayLocalMusic";
    public final String PLAY_ONLINEMUSIC_ACTION="PlayOnlineMusic";
    public final String PAUSEORPLAY_ACTION="PlayOrPauseMusic";
    public final String PREVIOUS_ACTION="PreviousMusic";
    public final String NEXT_ACTION="NextMusic";

    public final static String plyingAction="playing";
    public final static String pauseAction="pause";

    private final String TAG="playerService";

    //添加最近播放歌曲接口链接
    private final String addRecentlyPlay= ipAddressUtil.serviceIp+"/recentlyPlayed/addRecentlyPlayedSong";

    //音乐播放形式，在线或本地，默认为本地(0)
    public  int musicType=0;
    public  final int LOCAL=0;
    public  final int ONLINE=1;

    //播放模式
    public  int PLAY_MODE;
    public  final int LOOP_MODE=0;
    public  final int RANDOM_MODE=1;
    public  final int SINGLE_MODE=2;

    public PlayerEngine playerEngine;

    private PlayerService service;

    public  int position;
    public  boolean firstPlay=true;
    public AudioFocusManager audioFocusManager;
    public MediaSessionManager mediaSessionManager;
    private PlayerRecevier playerRecevier;
    private DownloadRecevier downloadRecevier;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private MyBinder myBinder;
    private FormBody formBody;

    /* 生命周期 ----------------------------------------------------------------- */

    @Override
    public void onCreate() {
        super.onCreate();
        service=this;
        RegisterBroadcast();
        this.startForeground(1,new Notification());

        //从preferences数据库中读取退出音乐前本地播放音乐的位置，播放模式
        preferences=getSharedPreferences("Position",MODE_PRIVATE);
        editor=preferences.edit();
        position=preferences.getInt("position",0);
        PLAY_MODE=preferences.getInt("PlayMode",0);

        localMusicList = DataSupport.findAll(LocalMusic.class);
        initPlayer();

        //获取音频焦点
        audioFocusManager = new AudioFocusManager(this);

        //设置mediaSession,用于锁屏交互
        mediaSessionManager=new MediaSessionManager(this);


        EventBus.getDefault().post(new MessageEvent("服务已开启"));


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSessionManager.release();
        stopForeground(true);
        unregisterReceiver(playerRecevier);
        unregisterReceiver(downloadRecevier);
    }

    /* 生命周期 --------------------------------------------------------------------- */

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        myBinder=new MyBinder();
        return myBinder;
    }

    public class MyBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }


    public PlayerService getService(){
        return service;
    }


    private void initPlayer(){
        playerEngine=new PlayerEngine(this);
        try {
            if (localMusicList.size()>0)
                playerEngine.setDataSource(localMusicList.get(position).getSrcPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 注册广播
     */
    private void RegisterBroadcast()
    {
        //注册监听用户点击播放音乐功能按钮广播
        playerRecevier=new PlayerRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PLAY_LOCALMUSIC_ACTION);
        filter.addAction(PLAY_ONLINEMUSIC_ACTION);
        filter.addAction(PAUSEORPLAY_ACTION);
        filter.addAction(PREVIOUS_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(playerRecevier,filter);

        //注册监听下载完成音乐广播
        downloadRecevier=new DownloadRecevier();
        IntentFilter downloadFilter=new IntentFilter();
        downloadFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadRecevier,downloadFilter);
    }


    /**
     * 创建继承广播的内部类，重写onReceive函数
     */
    class PlayerRecevier extends BroadcastReceiver {
        private Context mcontext;
        private Intent intent;

        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent broadcastIntent) {
            mcontext = context;
            String action = broadcastIntent.getAction();
            switch (action) {
                //播放本地音乐
                case PLAY_LOCALMUSIC_ACTION:
                    musicType = LOCAL;
                    playLocalMusic();
                    break;
                //播放在线音乐
                case PLAY_ONLINEMUSIC_ACTION:
                    musicType = ONLINE;
                    playOnlineMusic();
                    break;
                //暂停音乐
                case PAUSEORPLAY_ACTION:
                    pauseOrplay();
                    break;
                //下一首
                case NEXT_ACTION:
                    playNext();
                    break;
                //上一首
                case PREVIOUS_ACTION:
                    playPrevious();
                    break;

                case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                    if (playerEngine.isPlaying())
                        pauseOrplay();
                    break;
            }
        }
    }

    /**
     * 点击播放下一首音乐
     */
    public void playNext()
    {
        //判断当前列表是否为本地音乐列表
        if (musicType==LOCAL)
        {
            //循环播放模式
            if (PLAY_MODE==LOOP_MODE)
            {
                if (position==localMusicList.size()-1)
                    position=0;
                else
                    position += 1;
            }
            //随机播放模式
            else if (PLAY_MODE==RANDOM_MODE)
            {
                Random random=new Random();
                position=random.nextInt(localMusicList.size());
            }
            playLocalMusic();
        }
        //判断当前列表是否为网络音乐列表
        else if (musicType==ONLINE)
        {
            //判断播放模式
            if (PLAY_MODE==LOOP_MODE)
            {
                if (position==onlineMusicList.size()-1)
                    position=0;
                else
                    position += 1;
            }
            else if (PLAY_MODE==RANDOM_MODE)
            {
                Random random=new Random();
                position=random.nextInt(onlineMusicList.size());
            }
            playOnlineMusic();
        }
    }



    /**
     * 点击播放前一首音乐
     */
    private void playPrevious()
    {
        //判断当前是否为第一首歌曲
        if (position==0)
        {
            if (musicType==LOCAL)
            {
                position=localMusicList.size()-1;
                playLocalMusic();
            }
            else if (musicType==ONLINE)
            {
                position=onlineMusicList.size()-1;
                playOnlineMusic();
            }
        }
        else {
            position-=1;
            if (musicType==LOCAL)
            {
                playLocalMusic();
            }
            else if (musicType==ONLINE)
            {
                playOnlineMusic();
            }
        }
    }


    /**
     * 播放本地音乐
     */
    public void playLocalMusic() {
        try {
            firstPlay=false;
            editor.putInt("position",position);
            editor.putString("SongName",localMusicList.get(position).getName());
            editor.putString("Singer",localMusicList.get(position).getSinger());
            editor.putString("AlbumArt",localMusicList.get(position).getAlbumArt());
            editor.putInt("PlayMode",PLAY_MODE);
            editor.apply();
            playerEngine.reset();
            playerEngine.setDataSource(localMusicList.get(position).getSrcPath());
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 播放在线音乐
     */
    public void playOnlineMusic(){
        try {
            playerEngine.reset();
            playerEngine.setDataSource(onlineMusicList.get(position).getAudio());
            uploadAddRecentlyPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 点击播放或暂停按钮触发此方法
     */
    public void pauseOrplay() {
        if (!playerEngine.isPlaying()) {
            playerEngine.start();
        }
        else{
            playerEngine.pause();
        }
    }




    /**
     * 将播放的歌曲上传到服务器中
     */
    private void uploadAddRecentlyPlay(){
        formBody=new FormBody.Builder()
                .add("userAccount", String.valueOf(LoginActivity.userAccount))
                .add("songId", String.valueOf(onlineMusicList.get(position).getId()))
                .add("songName",onlineMusicList.get(position).getName())
                .add("singer",onlineMusicList.get(position).getSinger())
                .add("coverUrl",onlineMusicList.get(position).getPicUrl())
                .add("audioUrl",onlineMusicList.get(position).getAudio())
                .add("lrcUrl",onlineMusicList.get(position).getLrcUrl())
                .add("album",onlineMusicList.get(position).getAlbum())
                .build();
        HttpUtil.sendOkHttpRequest(addRecentlyPlay, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }

}
