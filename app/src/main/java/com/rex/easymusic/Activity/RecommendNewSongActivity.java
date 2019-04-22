package com.rex.easymusic.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rex.easymusic.Application.MusicApplication;
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
import com.bumptech.glide.Glide;

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

public class RecommendNewSongActivity extends ToolbarActivity {
    @BindView(R.id.img)
    public ImageView coverImg;
    @BindView(R.id.newSong_recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.ll_loading)
    public LinearLayout llLoading;
    @BindView(R.id.ll_load_fail)
    public LinearLayout llLoadFail;

    private String coverUrl;
    private String albumId;
    private RankMusicAdapter adapter;
    private List<OnlineMusic> onlineMusicList=new ArrayList<>();
    private PlayerService service;
    private Handler handler;
    private JSONArray jsonArray;

    private String AlbumUrl = "http://106.13.36.192:3000/album?id=";

    private String TAG="NewSongActivity";

    @Override
    public int setLayoutId() {
        return R.layout.activity_recommend_new_song;
    }

    @Override
    protected void setStatusBar(Activity activity) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = ((MusicApplication)getApplication()).getPlayerService();
        coverUrl = getIntent().getStringExtra("picUrl");
        albumId = getIntent().getStringExtra("albumId");
        getSupportActionBar().setTitle(getIntent().getStringExtra("musicName"));
        initRecyclerView();
        initHandler();
        loadNewSongs();
        Glide.with(this).load(coverUrl).into(coverImg);
    }


    /*----------------------------------------------------------------------------*/
    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler=new ViewHandler(recyclerView,llLoading,llLoadFail,adapter);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new RankMusicAdapter(onlineMusicList);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                service.position = position;
                service.onlineMusicList = onlineMusicList;
                Intent intent = new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                sendBroadcast(intent);
            }
        });

        adapter.setClickMoreListener(new OnClickMoreListener() {
            @Override
            public void onMoreClick(int position) {
                MusicMoreInfoPopup musicMoreInfoPopup =new MusicMoreInfoPopup(RecommendNewSongActivity.this);
                musicMoreInfoPopup.setMorePopUp();
                musicMoreInfoPopup.showMorePopUp(onlineMusicList.get(position));
            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void loadNewSongs() {
        try {
            HttpUtil.sendOkHttpRequest(AlbumUrl + albumId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.e("TAG", "onResponse: "+responseBody );
                    try {
                        jsonArray = new JSONObject(responseBody).getJSONArray("songs");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            OnlineMusic onlineMusic = new OnlineMusic();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            onlineMusic.setId(jsonObject.getInt("id"));
                            Log.e(TAG, "onResponse: "+onlineMusic.getId() );
                            MusicInfoUtil musicInfoUtil = new MusicInfoUtil(onlineMusic.getId());
                            onlineMusic.setName(musicInfoUtil.getMusicName());
                            onlineMusic.setAlbum(musicInfoUtil.getAlbumName());
                            onlineMusic.setPicUrl(musicInfoUtil.getPicUrl());
                            onlineMusic.setSinger(musicInfoUtil.getSinger());
                            onlineMusic.setLrcUrl(MusicInfoUtil.getLrcUrl(onlineMusic.getId()));
                            onlineMusic.setAudio(MusicInfoUtil.getAudioUrl(onlineMusic.getId()));
                            onlineMusicList.add(onlineMusic);
                            if (jsonArray.length()<20&&onlineMusicList.size()==jsonArray.length())
                                handler.sendEmptyMessage(1);
                            else if (onlineMusicList.size()==20)
                                handler.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
