package com.rex.easymusic.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.Enum.LoadStateEnum;
import com.rex.easymusic.R;
import com.rex.easymusic.util.ViewHandler;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.adapter.RankMusicAdapter;
import com.rex.easymusic.fragment.RankMusicFragment;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.Popup.MusicMoreInfoPopup;
import com.rex.easymusic.util.MusicInfoUtil;
import com.rex.easymusic.util.ViewUtils;
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
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RankActivity extends AppCompatActivity implements Runnable {

    @BindView(R.id.rank_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.RankMusic_recyclerview)
    public RecyclerView rankMusic_recyclerView;
    @BindView(R.id.ll_loading)
    public LinearLayout llLoading;
    @BindView(R.id.ll_load_fail)
    public LinearLayout llLoadFail;
    @BindView(R.id.img)
    public ImageView imageView;
    @BindView(R.id.smartRefreshLayout)
    public SmartRefreshLayout smartRefreshLayout;

    private String Music_URL;
    public final List<OnlineMusic> onlineMusicList=new ArrayList<>();
    private RankMusicAdapter adapter;
    private PlayerService service;
    private Handler handler;
    private JSONArray jsonArray;
    private int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankmusic);
        ButterKnife.bind(this);
        service=((MusicApplication)getApplication()).getPlayerService();
        setToolbar();
        initRankMusicRecycler();
        initSmartRefreshLayout();
        initHandler();
        LoadRanKMusic(Music_URL);
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
                  new Thread(RankActivity.this).start();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*--------------------------------------------------------------------*/

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new ViewHandler(rankMusic_recyclerView,llLoading,llLoadFail,adapter,smartRefreshLayout);
    }

    private void setToolbar()
    {
        Bundle bundle=getIntent().getExtras();
        String action=bundle.getString("RankType");
        switch (action)
        {
            case RankMusicFragment.Acg_Action:
                toolbar.setTitle("云音乐ACG榜");
                Music_URL=RankMusicFragment.AcgMusic_URL;
                imageView.setImageResource(R.drawable.acgbang);
                break;
            case RankMusicFragment.Billboard_Action:
                toolbar.setTitle("云音乐Billboard榜");
                Music_URL=RankMusicFragment.BillboardMusic_URL;
                imageView.setImageResource(R.drawable.billboard);
                break;
            case RankMusicFragment.Hot_Action:
                toolbar.setTitle("云音乐热歌榜");
                Music_URL=RankMusicFragment.HotMusic_URL;
                imageView.setImageResource(R.drawable.regebang);
                break;
            case RankMusicFragment.New_Action:
                toolbar.setTitle("云音乐新歌榜");
                Music_URL=RankMusicFragment.NewMusic_URL;
                imageView.setImageResource(R.drawable.xingebang);
                break;
            case RankMusicFragment.Surge_Action:
                toolbar.setTitle("云音乐飙升榜");
                Music_URL=RankMusicFragment.SurgeMusic_URL;
                imageView.setImageResource(R.drawable.biaoshengbang);
                break;
            case RankMusicFragment.Original_Action:
                toolbar.setTitle("云音乐原创榜");
                Music_URL=RankMusicFragment.OriginalMusic_URL;
                imageView.setImageResource(R.drawable.yuanchuangbang);
                break;
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRankMusicRecycler(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rankMusic_recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new RankMusicAdapter(onlineMusicList);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (service.position!=position)
                {
                    service.position=position;
                    service.onlineMusicList=onlineMusicList;
                    Intent intent=new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                    sendBroadcast(intent);
                }
            }
        });
        adapter.setClickMoreListener(new OnClickMoreListener() {
            @SuppressLint("NewApi")
            @Override
            public void onMoreClick(int position) {
                MusicMoreInfoPopup musicMoreInfoPopup =new MusicMoreInfoPopup(RankActivity.this);
                musicMoreInfoPopup.setMorePopUp();
                musicMoreInfoPopup.showMorePopUp(onlineMusicList.get(position));
            }
        });
        rankMusic_recyclerView.setAdapter(adapter);
    }

    private void LoadRanKMusic(String URL)
    {
        ViewUtils.changeViewState(rankMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOADING);
        HttpUtil.sendOkHttpRequest(URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                try {
                    jsonArray=new JSONObject(body).getJSONObject("playlist").getJSONArray("tracks");
                    loadOnlineMusic();
                } catch (JSONException e) {
                    ViewUtils.changeViewState(rankMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        loadOnlineMusic();
    }
}
