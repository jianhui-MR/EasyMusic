package com.rex.easymusic.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.R;
import com.rex.easymusic.adapter.MyFragmentPagerAdapter;
import com.rex.easymusic.Bean.LocalMusic;
import com.rex.easymusic.fragment.BottomContainerFragment;
import com.rex.easymusic.fragment.LrcFragment;
import com.rex.easymusic.fragment.MusicPicFragment;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.service.PlayerService;
import com.bumptech.glide.Glide;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

import static com.rex.easymusic.util.ScanMusicUtil.formatTime;

public class PlayMusicActivity extends ToolbarActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,Runnable {

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

    private final String deleteFavouriteMusicUrl= ipAddressUtil.serviceIp+"/favouriteMusic/deleteFavouriteMusic";

    private final String addFavouriteMusicUrl= ipAddressUtil.serviceIp+"/favouriteMusic/addFavouriteMusic";

    //检测是否是用户喜爱的歌曲
    private final String checkIsFavouriteMusicUrl=ipAddressUtil.serviceIp+"/favouriteMusic/isFavouriteMusic";

    public int Mode=0;
    public Intent intent;
    private Toast toast;
    private playerReceiver playerReceiver;
    private LocalMusic localMusic;
    private OnlineMusic onlineMusic;
    private PlayerService service;

    public FormBody formBody;

    public static final int LOOP_MODE=0;
    public static final int RANDOM_MODE=1;
    public static final int SINGLE_MODE=2;

    public final int LOCAL=0;
    public final int ONLINE=1;
    private Unbinder unbinder;
    private Handler favouriteMsuciHandler;
    private Handler seekBarHandler;

    private Menu menu;


    public static Boolean isFavourite=false;

    @Override
    public int setLayoutId() {
        return R.layout.activity_playmusic;
    }

    @Override
    protected void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=((MusicApplication)getApplication()).getPlayerService();
        initWidget();
//        setPlayerBuffering();
        initSeekBarHandler();
        registerBroadCast();
        new Thread(PlayMusicActivity.this).start();

        Log.e(TAG, "onCreate" );
    }

    @SuppressLint("HandlerLeak")
    private void initSeekBarHandler() {
        seekBarHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (seekBar != null) {
                            seekbar_playing_time.setText(formatTime(service.playerEngine.getCurrentPosition()));
                            seekBar.setProgress(service.playerEngine.getCurrentPosition());
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart" );
        super.onStart();
    }
    @Override
    protected void onResume() {
        initView();
        Log.e(TAG, "onResume" );
        if (menu!=null){
            if (service.musicType==service.LOCAL)
            {
                menu.findItem(R.id.favouriteMusic).setVisible(false);
            }else {
                menu.findItem(R.id.favouriteMusic).setVisible(true);
            }
        }

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
        unregisterReceiver(playerReceiver);
    }

    /* ---------------------------------------------------------------------------- */

    /**
     * 广播注册
     */
    private void registerBroadCast(){
        playerReceiver=new playerReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PlayerService.plyingAction);
        filter.addAction(PlayerService.pauseAction);
        registerReceiver(playerReceiver,filter);
    }

    class playerReceiver extends BroadcastReceiver {
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
                        Glide.with(PlayMusicActivity.this)
                                .load(BACKGROUND_URL)
                                .placeholder(R.drawable.background)
                                .bitmapTransform(new BlurTransformation(PlayMusicActivity.this,25,4))
                                .into(play_background);
                        getSupportActionBar().setTitle(service.localMusicList.
                                get(service.position).getName());
                        getSupportActionBar().setSubtitle(service.localMusicList.
                                get(service.position).getSinger());
                    }
                    else if (service.musicType==ONLINE){
                        onlineMusic= service.onlineMusicList.get(service.position);
                        Glide.with(PlayMusicActivity.this)
                                .load(onlineMusic.getPicUrl())
                                .override(250,250)
                                .placeholder(R.drawable.background)
                                .bitmapTransform(new BlurTransformation(PlayMusicActivity.this,25,4))
                                .into(play_background);
                        getSupportActionBar().setTitle(service.onlineMusicList.
                                get(service.position).getName());
                        getSupportActionBar().setSubtitle(service.onlineMusicList.
                                get(service.position).getSinger());
                        checkIsFavouriteMusic();
                    }
                    break;

                case PlayerService.pauseAction:
                    play.setImageResource(R.drawable.play);
                    break;
            }
        }
    }

    private int getStatusHeight(){
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 初始化控件
     */
    @SuppressLint({"HandlerLeak", "ShowToast"})
    private void initWidget()
    {
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
            Glide.with(PlayMusicActivity.this)
                    .load(BACKGROUND_URL)
                    .placeholder(R.drawable.background)
                    .crossFade()
                    .bitmapTransform(new BlurTransformation(PlayMusicActivity.this,25,4))
                    .into(play_background);
            getSupportActionBar().setTitle(service.localMusicList.
                    get(service.position).getName());
            getSupportActionBar().setSubtitle(service.localMusicList.
                    get(service.position).getSinger());
        }

        //在线播放进行的操作
        else if (service.musicType==service.ONLINE)
        {
            Glide.with(PlayMusicActivity.this)
                    .load(service.onlineMusicList.get(service.position).getPicUrl())
                    .override(250,250)
                    .placeholder(R.drawable.background)
                    .crossFade()
                    .bitmapTransform(new BlurTransformation(PlayMusicActivity.this,25,4))
                    .into(play_background);
            getSupportActionBar().setTitle(service.onlineMusicList.get(service.position).getName());
            getSupportActionBar().setSubtitle(service.onlineMusicList.get(service.position).getSinger());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_music_menu,menu);
        Log.e(TAG, "onCreateOptionsMenu" );
        this.menu=menu;
        initFavouriteMsuciHandler();
        if (service.musicType==service.LOCAL)
        {
            menu.findItem(R.id.favouriteMusic).setVisible(false);
        }else {
            menu.findItem(R.id.favouriteMusic).setVisible(true);
            checkIsFavouriteMusic();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.favouriteMusic:
                if (isFavourite) {
                    deleteFromFavouriteMusic();
                    menu.findItem(R.id.favouriteMusic).setIcon(R.drawable.no_favourite);
                    isFavourite=false;
                }
                else{
                    addToFavouriteMusic();
                    menu.findItem(R.id.favouriteMusic).setIcon(R.drawable.favourite);
                    isFavourite=true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    public void deleteFromFavouriteMusic(){
        formBody=new FormBody.Builder()
                .add("userAccount",LoginActivity.userAccount)
                .add("songId", String.valueOf(service.onlineMusicList.get(service.position).getId()))
                .build();
        HttpUtil.sendOkHttpRequest(deleteFavouriteMusicUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }

    public void addToFavouriteMusic(){
        formBody=new FormBody.Builder()
                .add("userAccount",LoginActivity.userAccount)
                .add("songId", String.valueOf(service.onlineMusicList.get(service.position).getId()))
                .add("songName",service.onlineMusicList.get(service.position).getName())
                .add("singer",service.onlineMusicList.get(service.position).getSinger())
                .add("coverUrl",service.onlineMusicList.get(service.position).getPicUrl())
                .add("audioUrl",service.onlineMusicList.get(service.position).getAudio())
                .add("lrcUrl",service.onlineMusicList.get(service.position).getLrcUrl())
                .add("album",service.onlineMusicList.get(service.position).getAlbum())
                .build();
        HttpUtil.sendOkHttpRequest(addFavouriteMusicUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }

    /**
     * 检查当前所播放的歌曲是否是用户喜爱的歌曲
     */
    private void checkIsFavouriteMusic(){
        formBody=new FormBody.Builder()
                .add("userAccount",LoginActivity.userAccount)
                .add("songId", String.valueOf(service.onlineMusicList.get(service.position).getId()))
                .build();
        HttpUtil.sendOkHttpRequest(checkIsFavouriteMusicUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    if (new JSONObject(responseBody).getBoolean("isFavouriteMusic")) {
                        PlayMusicActivity.isFavourite = true;
                        favouriteMsuciHandler.obtainMessage(2).sendToTarget();
                    }
                    else {
                        PlayMusicActivity.isFavourite = false;
                        favouriteMsuciHandler.obtainMessage(3).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
     * 给seekBar设置缓冲条
     */
    public void setPlayerBuffering(){
        service.playerEngine.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.e(TAG, "onBufferingUpdate: "+percent );
                seekBar.setSecondaryProgress(percent);
            }
        });
    }

    /**
     * 更新当前歌曲播放的时间进度
     */
    @SuppressLint("HandlerLeak")
    private void initFavouriteMsuciHandler(){
        favouriteMsuciHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 2:
                    menu.findItem(R.id.favouriteMusic).setIcon(R.drawable.favourite);
                        break;
                    case 3:
                    menu.findItem(R.id.favouriteMusic).setIcon(R.drawable.no_favourite);
                        break;

                }
            }
        };
    }

    @Override
    public void run() {
        if (service.playerEngine.isPlaying());
        {
            seekBarHandler.sendEmptyMessage(1);
        }
        seekBarHandler.postDelayed(this,1000);
    }
}
