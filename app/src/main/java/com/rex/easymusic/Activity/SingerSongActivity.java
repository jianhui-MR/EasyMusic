package com.rex.easymusic.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.Enum.LoadStateEnum;
import com.rex.easymusic.R;
import com.rex.easymusic.Popup.MusicMoreInfoPopup;
import com.rex.easymusic.util.ViewHandler;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.adapter.RankMusicAdapter;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.MusicInfoUtil;
import com.rex.easymusic.util.ViewUtils;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查看歌手所有单曲
 */
public class SingerSongActivity extends ToolbarActivity implements Runnable {

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
    @BindView(R.id.smartRefreshLayout)
    public SmartRefreshLayout smartRefreshLayout;

    private String ArtistsUrl="http://106.13.36.192:3000/artists?id=";
    private String singerId;
    private String PicUrl;
    private String singerName;
    private Handler handler;
    private RankMusicAdapter adapter;
    private PlayerService service;
    private JSONArray jsonArray;
    private List<OnlineMusic> onlineMusicList=new ArrayList<>();
    private int i=0;

    @Override
    public int setLayoutId() {
        return R.layout.activity_singer;
    }

    @Override
    protected void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=((MusicApplication)getApplication()).getPlayerService();
        initView();
        initRecyclerView();
        initHandler();
        initSmartRefreshLayout();
        loadSingerSong();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new ViewHandler(recyclerView,llLoading,llLoadFail,adapter,smartRefreshLayout);
    }

    /**
     * 初始化基础视图
     */
    private void initView(){
        singerId=getIntent().getStringExtra("singerId");
        PicUrl=getIntent().getStringExtra("picUrl");
        singerName=getIntent().getStringExtra("singer");
        getSupportActionBar().setTitle(singerName);
        Glide.with(this)
                .load(PicUrl)
                .into(imageView);
    }


    /**
     * 初始化recyclerView
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
            public void onMoreClick(int position) {
                MusicMoreInfoPopup musicMoreInfoPopup =new MusicMoreInfoPopup(SingerSongActivity.this);
                musicMoreInfoPopup.setMorePopUp();
                musicMoreInfoPopup.showMorePopUp(onlineMusicList.get(position));
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void initSmartRefreshLayout() {
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (adapter.getItemCount()==jsonArray.length()){
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
                else {
                    new Thread(SingerSongActivity.this).start();
                }
            }
        });
    }

    /**
     *加载歌曲
     */
    private void loadSingerSong(){
        ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOADING);
        HttpUtil.sendOkHttpRequest(ArtistsUrl+String.valueOf(singerId), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    jsonArray=new JSONObject(responseBody).getJSONArray("hotSongs");
                    loadOnlineMusic();
                } catch (JSONException e) {
                    ViewUtils.changeViewState(recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    e.printStackTrace();
                }

            }
        });
    }

    private void loadOnlineMusic(){
        while (i<jsonArray.length()){
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(i);
                OnlineMusic onlineMusic=new OnlineMusic();
                onlineMusic.setId(object.getInt("id"));
                MusicInfoUtil musicInfoUtil=new MusicInfoUtil(onlineMusic.getId());
                onlineMusic.setName(musicInfoUtil.getMusicName());
                onlineMusic.setAlbum(musicInfoUtil.getAlbumName());
                onlineMusic.setPicUrl(musicInfoUtil.getPicUrl());
                onlineMusic.setSinger(musicInfoUtil.getSinger());
                onlineMusic.setLrcUrl(MusicInfoUtil.getLrcUrl(onlineMusic.getId()));
                onlineMusic.setAudio(MusicInfoUtil.getAudioUrl(onlineMusic.getId()));
                onlineMusicList.add(onlineMusic);
                Log.e("TAG", "loadOnlineMusicSize:"+onlineMusicList.size() );
                i++;
                if (onlineMusicList.size()%20==0){
                    break;
                }
            } catch (JSONException e) {
                Log.e("TAG", "Exception" );
                e.printStackTrace();
            }
        }
        handler.sendEmptyMessage(1);
    }


    @Override
    public void run() {
        loadOnlineMusic();
    }
}
