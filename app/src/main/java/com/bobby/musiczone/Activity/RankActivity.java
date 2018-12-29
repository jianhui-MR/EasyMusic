package com.bobby.musiczone.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bobby.musiczone.Enum.LoadStateEnum;
import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.OnClickMoreListener;
import com.bobby.musiczone.adapter.OnItemClickListener;
import com.bobby.musiczone.adapter.RankMusicAdapter;
import com.bobby.musiczone.fragment.RankMusicFragment;
import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.entry.RankMusic;
import com.bobby.musiczone.service.PlayerService;
import com.bobby.musiczone.util.HttpUtil;
import com.bobby.musiczone.util.PoPupwindowUtil;
import com.bobby.musiczone.util.ViewUtils;
import com.google.gson.Gson;

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

public class RankActivity extends BaseActivity{

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

    private String Music_URL;
    public final List<OnlineMusic> rankMusicList=new ArrayList<>();
    private RankMusicAdapter adapter;
    private String Lrc_URL= "http://music.163.com/api/song/lyric?id=";
    private String Audio_URL="http://music.163.com/song/media/outer/url?id=";
    public static View view;
    private PlayerService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= LayoutInflater.from(this).inflate(R.layout.rankmusic_layout,null,false);
        setContentView(view);
        ButterKnife.bind(this);
        service=PlayerService.getService();
        setToolbar();
        LoadRanKMusic(Music_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void LoadRanKMusic(String URL)
    {
        ViewUtils.changeViewState(rankMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOADING);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rankMusic_recyclerView.setLayoutManager(linearLayoutManager);
        rankMusic_recyclerView.setNestedScrollingEnabled(false);
        HttpUtil.sendOkHttpRequest(URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(body).getJSONObject("playlist").getJSONArray("tracks");
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object =jsonArray.getJSONObject(i);
                        RankMusic rankMusic=new Gson().fromJson(object.toString(),RankMusic.class);
                        OnlineMusic onlineMusic=new OnlineMusic();
                        onlineMusic.id=rankMusic.id;
                        onlineMusic.name=rankMusic.name;
                        onlineMusic.album=new OnlineMusic.album();
                        onlineMusic.album.albumname=rankMusic.album.albumName;
                        onlineMusic.picUrl=rankMusic.album.picUrl;
                        onlineMusic.artistsList=rankMusic.artistList;
                        onlineMusic.lrcUrl=Lrc_URL+rankMusic.id+"&lv=-1&kv=-1";
                        onlineMusic.audio=Audio_URL+rankMusic.id+".mp3";
                        rankMusicList.add(onlineMusic);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter=new RankMusicAdapter(rankMusicList);
                            adapter.setItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    if (service.position!=position)
                                    {
                                        service.position=position;
                                        service.onlineMusicList=rankMusicList;
                                        Intent intent=new Intent(service.PLAY_ONLINEMUSIC_ACTION);
                                        sendBroadcast(intent);
                                    }
                                }
                            });
                            adapter.setClickMoreListener(new OnClickMoreListener() {
                                @SuppressLint("NewApi")
                                @Override
                                public void onMoreClick(int position) {
                                    PoPupwindowUtil poPupwindowUtil=new PoPupwindowUtil(RankActivity.this);
                                    poPupwindowUtil.setMorePopUpWindow();
                                    poPupwindowUtil.showPopupwindow(rankMusicList.get(position),view);
                                }
                            });
                            rankMusic_recyclerView.setAdapter(adapter);
                            ViewUtils.changeViewState(rankMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                        }
                    });
                } catch (JSONException e) {
                    ViewUtils.changeViewState(rankMusic_recyclerView, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    e.printStackTrace();
                }
            }
        });
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
}
