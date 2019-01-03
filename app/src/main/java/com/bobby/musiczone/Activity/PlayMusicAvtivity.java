package com.bobby.musiczone.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.MyFragmentPagerAdapter;
import com.bobby.musiczone.entry.LocalMusic;
import com.bobby.musiczone.fragment.BottomContainerFragment;
import com.bobby.musiczone.fragment.LrcFragment;
import com.bobby.musiczone.fragment.MusicPicFragment;
import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.service.PlayerService;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlayMusicAvtivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,Runnable {
    private final String TAG="PlayMusicActivity";

    @BindView(R.id.play_music)
    public ImageButton play;
    @BindView(R.id.next_music)
    public ImageButton next;
    @BindView(R.id.previous_music)
    public ImageButton previous;
    @BindView(R.id.play_mode)
    public ImageButton play_mode;
    @BindView(R.id.playingmuisc_list)
    public ImageView music_list;
    @BindView(R.id.pic_lrc_viewpager)
    public ViewPager viewPager;
    @BindView(R.id.play_background)
    public ImageView play_background;
    @BindView(R.id.Seekbar)
    public SeekBar seekBar;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.seekbar_playingTime)
    public TextView seekbar_playing_time;
    @BindView(R.id.seekbar_MaxTime)
    public TextView seekbar_Max_time;
    @BindView(R.id.background_layout)
    public RelativeLayout backgroundLayout;


    private List<Fragment> fragmentList;
    public static String BACKGROUND_URL="http://bpic.588ku.com/back_pic/03/65/64/5057aece3ddb0d5.jpg!/fh/300/quality/90/unsharp/true/compress/true";
    public int Mode=0;
    public Intent intent;
    private Toast toast;
    private playerRecevier playerRecevier;
    private LocalMusic localMusic;
    private OnlineMusic onlineMusic;
    private PlayerService service;

    public final int LOOP_MODE=0;
    public final int RANDOM_MODE=1;
    public final int SINGLE_MODE=2;

    public final int LOCAL=0;
    public final int ONLINE=1;
    private Unbinder unbinder;

    /* 生命周期 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playmusic);
        unbinder=ButterKnife.bind(this);
        service=PlayerService.getService();
        initWidget();
//        setPlayerBuffering();
        registerBroadCast();
        new Thread(PlayMusicAvtivity.this).start();
    }

    @Override
    protected void onResume() {
        initView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(playerRecevier);
        unbinder.unbind();
    }
    /* 生命周期 */

    /**
     * 广播注册
     */
    private void registerBroadCast(){
        playerRecevier=new playerRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PlayerService.plyingAction);
        filter.addAction(PlayerService.pauseAction);
        registerReceiver(playerRecevier,filter);
    }

    class playerRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action=intent.getAction();
            switch (action){
                //歌曲进行切歌，视图要发生变化
                case PlayerService.plyingAction:
                    play.setImageResource(R.drawable.pause);
                    seekbar_Max_time.setText(formatTime(service.playerEngine.getDuration()));
                    seekBar.setMax(service.playerEngine.getDuration());
                    if (service.musicType==LOCAL){
                        localMusic=service.localMusicList.get(service.position);
                        Glide.with(PlayMusicAvtivity.this)
                                .load(BACKGROUND_URL)
                                .placeholder(R.drawable.background)
                                .bitmapTransform(new BlurTransformation(PlayMusicAvtivity.this,25,4))
                                .into(play_background);
                        toolbar.setTitle(localMusic.getName());
                    }
                    else if (service.musicType==ONLINE){
                        onlineMusic= service.onlineMusicList.get(service.position);
                        Glide.with(PlayMusicAvtivity.this)
                                .load(onlineMusic.getPicUrl())
                                .override(250,250)
                                .placeholder(R.drawable.background)
                                .bitmapTransform(new BlurTransformation(PlayMusicAvtivity.this,25,4))
                                .into(play_background);
                        toolbar.setTitle(onlineMusic.getName());
                    }
                    break;

                case PlayerService.pauseAction:
                    play.setImageResource(R.drawable.play);
                    break;
            }
        }
    }

    /**
     * 初始化控件
     */
    @SuppressLint({"HandlerLeak", "ShowToast"})
    private void initWidget()
    {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        toast=Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        play_mode.setOnClickListener(this);
        music_list.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        //背景透明度设置
        backgroundLayout.getBackground().setAlpha(100);

        //设置歌曲最大播放时间
        if (service.playerEngine.isPlaying())
            setSeekbar_Max_time();
    }

    /**
     * 点击进来渲染播放页面
     */
    @SuppressLint("NewApi")
    private void initView()
    {
        fragmentList=new ArrayList<>();
        fragmentList.add(new MusicPicFragment());
        fragmentList.add(new LrcFragment());
        MyFragmentPagerAdapter adapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);

        //判断当前音乐是否在播放，进行播放按钮的选择
        if (service.playerEngine.isPlaying()) {
            play.setImageResource(R.drawable.pause);
        }

        //本地播放进行的操作
        if (service.musicType==service.LOCAL)
        {
            Glide.with(PlayMusicAvtivity.this)
                    .load(BACKGROUND_URL)
                    .placeholder(R.drawable.background)
                    .crossFade()
                    .bitmapTransform(new BlurTransformation(PlayMusicAvtivity.this,25,4))
                    .into(play_background);
            toolbar.setTitle(service.localMusicList.
                    get(service.position).getName());
        }

        //在线播放进行的操作
        else if (service.musicType==service.ONLINE)
        {
            Glide.with(PlayMusicAvtivity.this)
                    .load(service.onlineMusicList.get(service.position).getPicUrl())
                    .override(250,250)
                    .placeholder(R.drawable.background)
                    .crossFade()
                    .bitmapTransform(new BlurTransformation(PlayMusicAvtivity.this,25,4))
                    .into(play_background);
            toolbar.setTitle(service.onlineMusicList.get(service.position).getName());
        }

        //判断播放模式，改变播放模式图标
        Mode=service.PLAY_MODE;
        switch (Mode)
        {
            case LOOP_MODE:
                play_mode.setImageResource(R.drawable.loop_playback);
                break;
            case RANDOM_MODE:
                play_mode.setImageResource(R.drawable.random_play);
                break;
            case SINGLE_MODE:
                play_mode.setImageResource(R.drawable.single_cycle);
                break;
        }
    }

    /**
     * 设置seekbar最大时间
     */
    private void setSeekbar_Max_time()
    {
        seekBar.setMax(service.playerEngine.getDuration());
        seekbar_Max_time.setText(formatTime(service.playerEngine.getDuration()));
    }

    /**
     * 毫秒 转 (分，秒)
     * @param time
     * @return
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.play_music:
                intent=new Intent(service.PAUSEORPLAY_ACTION);
                sendBroadcast(intent);
                break;
            case R.id.previous_music:
                intent=new Intent(service.PREVIOUS_ACTION);
                sendBroadcast(intent);
                break;
            case R.id.next_music:
                intent=new Intent(service.NEXT_ACTION);
                sendBroadcast(intent);
                break;
            case R.id.play_mode:
                Mode++;
                if (Mode==3)
                    Mode=0;
                service.PLAY_MODE=Mode;
                switch (Mode){
                    case LOOP_MODE:
                        toast.setText("循环播放");
                        toast.show();
                        play_mode.setImageResource(R.drawable.loop_playback);
                        break;
                    case RANDOM_MODE:
                        toast.setText("随机播放");
                        toast.show();
                        play_mode.setImageResource(R.drawable.random_play);
                        break;
                    case SINGLE_MODE:
                        toast.setText("单曲循环");
                        toast.show();
                        play_mode.setImageResource(R.drawable.single_cycle);
                        break;
                }
                break;
            case R.id.playingmuisc_list:
                BottomContainerFragment.popupWindow.showAsDropDown(getWindow().getDecorView(), Gravity.BOTTOM,0,1000);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
        {
            service.playerEngine.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 给seekbar设置缓冲条
     */
    public  void setPlayerBuffering(){
        service.playerEngine.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });
    }

    /**
     * 更新当前歌曲播放的时间进度
     */
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    if (seekBar!=null){
                        seekbar_playing_time.setText(formatTime(service.playerEngine.getCurrentPosition()));
                        seekBar.setProgress(service.playerEngine.getCurrentPosition());
                    }
                    break;
            }
        }
    };

    @Override
    public void run() {
        if (service.playerEngine.isPlaying());
        {
            handler.sendEmptyMessage(1);
        }
        handler.postDelayed(this,1000);
    }

}
