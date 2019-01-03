package com.bobby.musiczone.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bobby.musiczone.Enum.LoadStateEnum;
import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.OnClickMoreListener;
import com.bobby.musiczone.adapter.OnItemClickListener;
import com.bobby.musiczone.adapter.RankMusicAdapter;
import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.service.PlayerService;
import com.bobby.musiczone.util.HttpUtil;
import com.bobby.musiczone.util.MusicInfoUtil;
import com.bobby.musiczone.util.ViewUtils;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * 查看歌手所有单曲
 */
public class SingerSongActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.img)
    public ImageView imageView;
    @BindView(R.id.singerMusic_recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.ll_loading)
    public LinearLayout llLoading;
    @BindView(R.id.ll_load_fail)
    public LinearLayout llLoadFail;

    private String ArtistsUrl="http://106.13.36.192:3000/artists";
    private String singerId;
    private String PicUrl;
    private String singerName;
    private FormBody body;
    private Unbinder unbinder;
    private Handler handler;
    private RankMusicAdapter adapter;
    private PlayerService service;
    private List<OnlineMusic> onlineMusicList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        unbinder=ButterKnife.bind(this);
        service=PlayerService.getService();
        initHandler();
        initView();
        initRecyclerView();
        loadSingerSong();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
               switch (msg.what){
                   case 1:
                       adapter.notifyDataSetChanged();
                       ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                       break;
               }
            }
        };
    }

    private void initView(){
        singerId=getIntent().getStringExtra("singerId");
        PicUrl=getIntent().getStringExtra("picUrl");
        singerName=getIntent().getStringExtra("singer");
        toolbar.setTitle(singerName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Glide.with(this)
                .load(PicUrl)
                .into(imageView);
    }


    /**
     * 初始化recyclerview
     */
    private void initRecyclerView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        adapter=new RankMusicAdapter(onlineMusicList);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                service.position=position;
                service.onlineMusicList=onlineMusicList;
                Intent intent=new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                sendBroadcast(intent);
            }
        });

        adapter.setClickMoreListener(new OnClickMoreListener() {
            @Override
            public void onMoreClick(int posiiton) {

            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     *加载歌曲
     */
    private void loadSingerSong(){
        body=new FormBody
                .Builder()
                .add("id",String.valueOf(singerId))
                .build();
        ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOADING);
        HttpUtil.sendOkHttpRequest(ArtistsUrl, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("hotSongs");
                    for (int i=0;i<jsonArray.length();i++){
                        OnlineMusic onlineMusic=new OnlineMusic();
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        onlineMusic.setId(jsonObject.getInt("id"));
                        MusicInfoUtil musicInfoUtil=new MusicInfoUtil(onlineMusic.getId());
                        onlineMusic.setName(musicInfoUtil.getMusicName());
                        onlineMusic.setAlbum(musicInfoUtil.getAlbumName());
                        onlineMusic.setPicUrl(musicInfoUtil.getPicUrl());
                        onlineMusic.setSinger(musicInfoUtil.getSinger());
                        onlineMusic.setLrcUrl(MusicInfoUtil.getLrcUrl(onlineMusic.getId()));
                        onlineMusic.setAudio(MusicInfoUtil.getAudioUrl(onlineMusic.getId()));
                        onlineMusicList.add(onlineMusic);
                    }
                   handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    e.printStackTrace();
                }

            }
        });
    }


}
