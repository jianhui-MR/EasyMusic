package com.bobby.musiczone.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.ArtistsAdapter;
import com.bobby.musiczone.adapter.RecommendMusicListAdapter;
import com.bobby.musiczone.adapter.RecommendNewSongAdapter;
import com.bobby.musiczone.entry.Artist;
import com.bobby.musiczone.entry.RecommendMusicList;
import com.bobby.musiczone.entry.RecommendNewSong;
import com.bobby.musiczone.util.HttpUtil;
import com.google.gson.Gson;

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
 * 发现页Fragment
 */
public class DiscoveryFragment extends Fragment {
    @BindView(R.id.artist_recyclerView)
    public RecyclerView artistRecyclerView;
    @BindView(R.id.musicList_recyclerView)
    public RecyclerView recommendMusicListRecycler;
    @BindView(R.id.recommendNewMusic_recyclerView)
    public RecyclerView recommendNewMusicRecycler;

    private final String HotSingerUrl="http://106.13.36.192:3000/top/artists";
    private final String RecommendMusicListUrl="http://106.13.36.192:3000/personalized";
    private final String RecommandNewMusicUrl="http://106.13.36.192:3000/personalized/newsong";
    private final String TAG="DiscoveryFragment";
    private View view;
    private Unbinder unbinder;

    private ArtistsAdapter artistsAdapter;
    private RecommendMusicListAdapter recommendMusicListAdapter;
    private RecommendNewSongAdapter recommendNewSongAdapter;
    private List<Artist> artistList;
    private List<RecommendMusicList> recommendMusicLists;
    private List<RecommendNewSong> recommendNewSongList;
    private FormBody body;
    private Handler handler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.discovery,container,false);
        unbinder=ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHandler();
    }

    @Override
    public void onStart() {
        super.onStart();
        initArtists();
        loadArtists();
        initRecommendMusicList();
        loadRecommendMusicList();
        initRecommendNewMusic();
        loadRecommendNewMusic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /*----------------------------------------------------------------------------------------------*/

    /**
     * 设置handler
     */
    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        artistsAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        recommendMusicListAdapter.notifyDataSetChanged();
                        break;
                    case 3:
                        recommendNewSongAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    /**
     * 对热门歌手做控件初始化
     */
    private void initArtists(){
        artistList=new ArrayList<>();
        artistsAdapter=new ArtistsAdapter(artistList);
        artistRecyclerView.setAdapter(artistsAdapter);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),15);
        artistRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 加载热门歌手
     */
    private void loadArtists(){
        body=new FormBody.Builder()
                .add("offset",String.valueOf(0))
                .add("limit",String.valueOf(30))
                .build();
        HttpUtil.sendOkHttpRequest(HotSingerUrl, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("artists");
                    JSONObject jsonObject;
                    for (int i=0;i<jsonArray.length();i++){
                        jsonObject=jsonArray.getJSONObject(i);
                        Artist artist=new Gson().fromJson(jsonObject.toString(),Artist.class);
                        artistList.add(artist);
                    }
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 对推荐歌单做控件初始化
     */
    private void initRecommendMusicList(){
        recommendMusicLists=new ArrayList<>();
        recommendMusicListAdapter=new RecommendMusicListAdapter(recommendMusicLists);
        recommendMusicListRecycler.setAdapter(recommendMusicListAdapter);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        recommendMusicListRecycler.setLayoutManager(layoutManager);
    }

    /**
     * 加载推荐歌单
     */
    private void loadRecommendMusicList(){
        body=new FormBody.Builder()
                .add("limit",String.valueOf(6))
                .build();
        HttpUtil.sendOkHttpRequest(RecommendMusicListUrl,body,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("result");
                    JSONObject jsonObject;
                    for (int i=0;i<jsonArray.length();i++){
                        jsonObject=jsonArray.getJSONObject(i);
                        RecommendMusicList e=new Gson().fromJson(jsonObject.toString(),RecommendMusicList.class);
                        recommendMusicLists.add(e);
                    }
                    handler.sendEmptyMessage(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化推荐新音乐
     */
    private void initRecommendNewMusic(){
        recommendNewSongList=new ArrayList<>();
        recommendNewSongAdapter=new RecommendNewSongAdapter(recommendNewSongList);
        recommendNewMusicRecycler.setAdapter(recommendNewSongAdapter);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        recommendNewMusicRecycler.setLayoutManager(layoutManager);
    }

    /**
     * 加载推荐新音乐
     */
    private void loadRecommendNewMusic(){
        HttpUtil.sendOkHttpRequest(RecommandNewMusicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("result");
                    JSONObject jsonObject;
                    for (int i=0;i<6;i++){
                        jsonObject=jsonArray.getJSONObject(i);
                        RecommendNewSong e=new Gson().fromJson(jsonObject.toString(),RecommendNewSong.class);
                        recommendNewSongList.add(e);
                    }
                    handler.sendEmptyMessage(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
