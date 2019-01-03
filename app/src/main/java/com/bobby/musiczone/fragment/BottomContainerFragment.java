package com.bobby.musiczone.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bobby.musiczone.Activity.BaseActivity;
import com.bobby.musiczone.Activity.RankActivity;
import com.bobby.musiczone.Activity.SearchMusicActivity;
import com.bobby.musiczone.MessageEvent.MessageEvent;
import com.bobby.musiczone.Splash_screenActivity;
import com.bobby.musiczone.Manager.DecorationManager;
import com.bobby.musiczone.Activity.PlayMusicAvtivity;
import com.bobby.musiczone.MainActivity;
import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.OnItemClickListener;
import com.bobby.musiczone.adapter.PlayListAdapter;
import com.bobby.musiczone.entry.LocalMusic;
import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.service.PlayerService;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Bobby on 2018/7/17.
 */

public class BottomContainerFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.albumn_pic)
    public  ImageView album_pic;
    @BindView(R.id.play_btn)
    public  ImageView play_btn;
    @BindView(R.id.song)
    public  TextView song_name;
    @BindView(R.id.singer)
    public  TextView singer_name;
    @BindView(R.id.playlist_btn)
    public ImageView music_list;
    @BindView(R.id.music_control_layout)
    public LinearLayout bottom_layout;


    public static PopupWindow popupWindow;
    private Intent intent;
    private View view;
    private SharedPreferences preferences;
    private RecyclerView songs_recyclerView;
    private PlayListAdapter playListAdapter;
    private List<LocalMusic> songList;
    private playerRecevier playerRecevier;
    private LocalMusic localMusic;
    private OnlineMusic onlineMusic;
    private Unbinder unbinder;
    private PlayerService service;

    private final String TAG="BottomContainerFragment";

    public static int Activity;

    /* 生命周期 */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences=getContext().getSharedPreferences("Position",Context.MODE_PRIVATE);
        songList= DataSupport.findAll(LocalMusic.class);
        EventBus.getDefault().register(this);
        service=PlayerService.getService();
        registerBrodcast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.bottom_music_layout,container,false);
        unbinder=ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (service!=null){
            setPoPupWindow();
            setWidget();
            setRecyclerview();
            initBottomContainer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        getActivity().unregisterReceiver(playerRecevier);
        EventBus.getDefault().unregister(this);
    }

    /* 生命周期 */

    /**
     * 注册广播
     */
    private void registerBrodcast(){
        playerRecevier=new playerRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PlayerService.plyingAction);
        filter.addAction(PlayerService.pauseAction);
        filter.addAction(BaseActivity.BindSuccess);
        getContext().registerReceiver(playerRecevier,filter);
    }

    /**
     * 配置广播接收到广播后的操作
     */
    class playerRecevier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
           String  action=intent.getAction();
           switch (action){
               case PlayerService.plyingAction:
                   if (service.musicType==service.LOCAL){
                       localMusic=service.localMusicList.get(service.position);
                       song_name.setText(localMusic.getName());
                       singer_name.setText(localMusic.getSinger());
                       play_btn.setImageResource(R.drawable.pause_btn);

                       if (localMusic.albumArt!=null)
                           album_pic.setImageBitmap(BitmapFactory.decodeFile(
                                   localMusic.albumArt));
                       else
                           album_pic.setImageResource(R.drawable.placeholder_disk_210);
                   }
                   else if (service.musicType==service.ONLINE){
                       onlineMusic= service.onlineMusicList.get(service.position);
                       song_name.setText(onlineMusic.getName());
                       singer_name.setText(onlineMusic.getSinger());
                       play_btn.setImageResource(R.drawable.pause_btn);
                       Glide.with(getContext())
                               .load(onlineMusic.getPicUrl())
                               .placeholder(R.drawable.albumart)
                               .error(R.drawable.albumart)
                               .into(album_pic);
                   }
                   break;

               case PlayerService.pauseAction:
                   play_btn.setImageResource(R.drawable.play_btn);
                   break;
           }
        }
    }

    /**
     * 第一次进入主界面，bottomfragment界面可能比service启动要快,此时在生命周期获取不到service
     * 利用eventbus进行通信
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initBottom(MessageEvent messageEvent) {
        Log.e(TAG, "initBottom: 初始化bottom" );
        service=PlayerService.getService();
        setPoPupWindow();
        setWidget();
        setRecyclerview();
        initBottomContainer();
    }

    private void setWidget()
    {
        music_list.setOnClickListener(this);
        play_btn.setOnClickListener(this);
        bottom_layout.setOnClickListener(this);
        play_btn.setImageResource(service.playerEngine.isPlaying()? R.drawable.pause_btn:R.drawable.play_btn);

        if (songList!=null&&service.musicType==service.LOCAL)
        {
            if (preferences.getInt("position",-1)!=-1)
            {
                song_name.setText(preferences.getString("SongName","0"));
                singer_name.setText(preferences.getString("Singer","1"));
                album_pic.setImageBitmap(BitmapFactory.decodeFile(preferences.getString("AlbumArt","1")));
            }
        }
        else if (service.musicType==service.ONLINE)
        {
            song_name.setText(service.onlineMusicList.get(service.position).getName());
            singer_name.setText(service.onlineMusicList.get(service.position).getSinger());
            Glide.with(getContext())
                    .load(service.onlineMusicList.get(service.position).getPicUrl())
                    .into(album_pic);
        }
    }
    private void setPoPupWindow()
    {
        View contentview=LayoutInflater.from(getContext()).inflate(R.layout.playlist_popupwindow,null,false);
        songs_recyclerView=contentview.findViewById(R.id.recyclerview2);
        songs_recyclerView.setHorizontalFadingEdgeEnabled(false);
        songs_recyclerView.addItemDecoration(new DecorationManager(getContext(),DecorationManager.VERTICAL_LIST));
        popupWindow=new PopupWindow(contentview, RecyclerView.LayoutParams.MATCH_PARENT,
                1100,true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.showPopupAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                lp.alpha=1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 设置歌曲列表显示
     */
    private void setRecyclerview()
    {
        playListAdapter =new PlayListAdapter(songList);
        playListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                service.position=position;
                Intent intent=new Intent(service.PLAY_LOCALMUSIC_ACTION);
                getActivity().sendBroadcast(intent);
            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        songs_recyclerView.setLayoutManager(layoutManager);
        songs_recyclerView.setAdapter(playListAdapter);
    }

    /**
     * 点击程序进来以后初始化底部状态栏
     */
    private void initBottomContainer()
    {
        if (service.musicType==service.LOCAL){
            if (service.localMusicList==null)
                return;
            song_name.setText(service.localMusicList.get(service.position).getName());

            Log.e(TAG, "initBottomContainer: "+ service.localMusicList.get(service.position).getName());
            singer_name.setText(service.localMusicList.get(service.position).getSinger());
            if (service.localMusicList.get(service.position).albumArt!=null)
                album_pic.setImageBitmap(BitmapFactory.decodeFile(
                        service.localMusicList.get(service.position).albumArt));
            else
                album_pic.setImageResource(R.drawable.placeholder_disk_210);
        }
        else if (service.musicType==service.ONLINE){
            song_name.setText(service.onlineMusicList.get(service.position).getName());
            singer_name.setText(service.onlineMusicList.get(service.position).getSinger());
            Glide.with(getContext())
                    .load(service.onlineMusicList.get(service.position).getPicUrl())
                    .placeholder(R.drawable.albumart)
                    .error(R.drawable.albumart)
                    .into(album_pic);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.play_btn:
                intent=new Intent(service.PAUSEORPLAY_ACTION);
                getContext().sendBroadcast(intent);
                break;
            case R.id.music_control_layout:
                intent=new Intent(getActivity(),PlayMusicAvtivity.class);
                startActivity(intent);
                break;
            case R.id.playlist_btn:
                popupWindow.showAsDropDown(getActivity().getWindow().getDecorView(),Gravity.BOTTOM,0,1000);
                WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                lp.alpha=0.35f;
                getActivity().getWindow().setAttributes(lp);
                break;
        }
    }
}
