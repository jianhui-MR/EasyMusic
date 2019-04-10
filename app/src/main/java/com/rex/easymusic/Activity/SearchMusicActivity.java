package com.rex.easymusic.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.Enum.LoadStateEnum;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.R;
import com.rex.easymusic.adapter.OnlineMusicAdapter;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.Popup.MusicMoreInfoPopup;
import com.rex.easymusic.util.MusicInfoUtil;
import com.rex.easymusic.util.ViewUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchMusicActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    @BindView(R.id.onlineMusic_recyclerview)
    public RecyclerView onlineMusic_recyclerView;
    @BindView(R.id.ll_loading)
    public LinearLayout llLoading;
    @BindView(R.id.ll_load_fail)
    public LinearLayout llLoadFail;
    public   SearchView searchView;
    @BindView(R.id.smartRefreshLayout)
    public SmartRefreshLayout smartRefreshLayout;

    private static final String SEARCH_URL = "http://106.13.36.192:3000/search?keywords=%s&limit=15&offset=%s";

    public  List<OnlineMusic> onlineMusicList;
    public OnlineMusicAdapter adapter;
    private String keywords;
    private int  offset;
    private PlayerService service;
    private Unbinder unbinder;
    private final String TAG="SearchMusic";
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        unbinder=ButterKnife.bind(this);
        service=((MusicApplication)getApplication()).getPlayerService();
        initView();
        initSmartRefreshLayout();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    /**
     * 初始化页面视图
     */
    private void initView() {
        Toolbar mToolbar =  findViewById(R.id.toolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id 'toolbar'");
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        onlineMusicList=new ArrayList<>();
        adapter=new OnlineMusicAdapter(onlineMusicList);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (service.musicType==service.LOCAL)
                {
                    service.onlineMusicList=onlineMusicList;
                    service.position=position;
                    Intent intent=new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                    sendBroadcast(intent);
                }
                else if (service.onlineMusicList.get(service.position).getId()!=
                        onlineMusicList.get(position).getId())
                {
                    service.onlineMusicList=onlineMusicList;
                    service.position=position;
                    Intent intent=new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                    sendBroadcast(intent);
                }
            }
        });
        adapter.setClickMoreListener(new OnClickMoreListener() {
            @Override
            public void onMoreClick(int position) {
                MusicMoreInfoPopup musicMoreInfoPopup =new MusicMoreInfoPopup(SearchMusicActivity.this);
                musicMoreInfoPopup.setMorePopUp();
                musicMoreInfoPopup.showMorePopUp(onlineMusicList.get(position));
            }
        });
        onlineMusic_recyclerView.setLayoutManager(linearLayoutManager);
        onlineMusic_recyclerView.setAdapter(adapter);
    }
    private void initSmartRefreshLayout() {
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                searchOnlineMusic();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        ViewUtils.changeViewState(onlineMusic_recyclerView,llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                        adapter.notifyDataSetChanged();
                        try{
                            smartRefreshLayout.finishLoadMore();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_music, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded();
        searchView.setQueryHint(getString(R.string.search_tips));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onQueryTextSubmit(String query) {
        ViewUtils.changeViewState(onlineMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOADING);
        searchView.clearFocus();
        this.keywords=query;
        onlineMusicList.clear();
        reSearchOnlineMusic();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * 根据关键字搜索在线音乐
     */
    private void searchOnlineMusic()
    {
        if (keywords!=null)
        {
            offset++;
            HttpUtil.sendOkHttpRequest(String.format(SEARCH_URL,keywords,String.valueOf((offset-1)*15)),new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ViewUtils.changeViewState(onlineMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    loadSearchResult(response.body().string());
                }
            });
        }
    }

    private void reSearchOnlineMusic(){
        offset=0;
        searchOnlineMusic();
    }


    private void loadSearchResult(final String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("songs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
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
            }
            handler.obtainMessage(1).sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
