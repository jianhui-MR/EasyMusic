package com.bobby.musiczone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.bobby.musiczone.Enum.LoadStateEnum;
import com.bobby.musiczone.adapter.OnClickMoreListener;
import com.bobby.musiczone.adapter.OnItemClickListener;
import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.OnlineMusicAdapter;
import com.bobby.musiczone.service.PlayerService;
import com.bobby.musiczone.util.HttpUtil;
import com.bobby.musiczone.util.MorePoPupUtil;
import com.bobby.musiczone.util.MusicInfoUtil;
import com.bobby.musiczone.util.ViewUtils;
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
import okhttp3.FormBody;
import okhttp3.Response;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    @BindView(R.id.onlineMusic_recyclerview)
    public RecyclerView onlineMusic_recyclerview;
    @BindView(R.id.ll_loading)
    public LinearLayout llLoading;
    @BindView(R.id.ll_load_fail)
    public LinearLayout llLoadFail;
    public   SearchView searchView;

    private static final String SEARCH_URL = "http://106.13.36.192:3000/search";


    public  List<OnlineMusic> onlineMusicList;
    public OnlineMusicAdapter adapter;
    private String query;
    private int lastVisibleItem;
    private int  offset;
    private PlayerService service;
    private Unbinder unbinder;
    private FormBody formBody;
    private final String TAG="SearchMusic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_music);
        unbinder=ButterKnife.bind(this);
        service=PlayerService.getService();
        initView();
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
            public void onMoreClick(int posiiton) {
                MorePoPupUtil morePoPupUtil=new MorePoPupUtil(SearchMusicActivity.this);
                morePoPupUtil.setMorePopUp();
                morePoPupUtil.showMorePopUp(onlineMusicList.get(posiiton));
            }
        });
        onlineMusic_recyclerview.setAdapter(adapter);
        onlineMusic_recyclerview.setLayoutManager(linearLayoutManager);
        onlineMusic_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==adapter.getItemCount())
                    searchOnlineMusic();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
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
        ViewUtils.changeViewState(onlineMusic_recyclerview, llLoading, llLoadFail, LoadStateEnum.LOADING);
        this.query=query;
        reSearchOnlineMusic();
        onlineMusicList.clear();
        return false;
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
        if (query!=null)
        {
            offset++;
            formBody=new FormBody.Builder()
                    .add("keywords",query)
                    .add("limit","15")
                    .add("offset", String.valueOf((offset-1)*15))
                    .build();

            Log.e(TAG, "searchOnlineMusic: "+String.valueOf((offset-1)*15));

            HttpUtil.sendOkHttpRequest(SEARCH_URL,formBody,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ViewUtils.changeViewState(onlineMusic_recyclerview, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewUtils.changeViewState(onlineMusic_recyclerview, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                    adapter.notifyDataSetChanged();

                    Log.e(TAG, "run: "+JsonData );
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewUtils.changeViewState(onlineMusic_recyclerview, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                }
            });
            e.printStackTrace();
        }
    }
}
