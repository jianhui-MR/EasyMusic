package com.rex.easymusic.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rex.easymusic.Activity.FavouriteMusicActivity;
import com.rex.easymusic.Activity.LocalMusicActivity;
import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.Activity.RecentPlayActivity;
import com.rex.easymusic.Activity.SongListActivity;
import com.rex.easymusic.Bean.SongList;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.EventBus.MessageEvent;
import com.rex.easymusic.Popup.CreateSongListPopup;
import com.rex.easymusic.Popup.SongListMoreInfoPopup;
import com.rex.easymusic.R;
import com.rex.easymusic.adapter.SongListAdapter;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ipAddressUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * 我的音乐Fragment
 * Created by Rex on 2018/7/13.
 */

public class MusicFragment extends Fragment {

    @BindView(R.id.localMusic)
    public RelativeLayout localMusic_layout;
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;


    public  View view;
    private PlayerService service;
    private Unbinder unbinder;
    private Intent intent;
    private final String getSongListUrl= ipAddressUtil.serviceIp+"/type/getType";
    private SongListAdapter adapter;
    public static List<SongList> songLists;
    private FormBody formBody;
    private Handler handler;
    private Context context=getActivity();
    private SongListMoreInfoPopup songListMoreInfoPopup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_music_layout,container,false);
        unbinder=ButterKnife.bind(this,view);
        initHandler();
        initRecyclerView();
        loadSongList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reFreshSongLists(MessageEvent messageEvent){
        if (messageEvent.getMessage()==2){
            adapter.notifyDataSetChanged();
        }else if (messageEvent.getMessage()==3){
            songLists.clear();
            loadSongList();
        }
    }


    private void initRecyclerView(){
        songLists=new ArrayList<>();
        adapter=new SongListAdapter(songLists);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                intent=new Intent(getActivity(),SongListActivity.class);
                intent.putExtra("name",songLists.get(position).getName());
                intent.putExtra("id",songLists.get(position).getTypeId());
                startActivity(intent);
            }
        });
        adapter.setOnClickMoreListener(new OnClickMoreListener() {
            @Override
            public void onMoreClick(int position) {
                songListMoreInfoPopup=new SongListMoreInfoPopup(songLists.get(position),getActivity());
                songListMoreInfoPopup.showPopup();
            }
        });
    }

    private void loadSongList(){
        formBody=new FormBody.Builder()
                .add("userAccount",LoginActivity.userAccount)
                .build();
        HttpUtil.sendOkHttpRequest(getSongListUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("typeList");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        SongList songList=new SongList();
                        songList.setName(jsonObject.getString("type"));
                        songList.setTypeId(jsonObject.getInt("typeId"));
                        songList.setCoverUrl(jsonObject.getString("coverUrl"));
                        songLists.add(songList);
                    }
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                        adapter.notifyDataSetChanged();
                        Log.e("TAG", String.valueOf(songLists.size()));
                        break;
                }
            }
        };
    }


    @OnClick(R.id.localMusic)
    public void onClickLocalMusic(){
        intent=new Intent(getActivity(),LocalMusicActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.recentlyPlay)
    public void onClickRecentPlay(){
        intent=new Intent(getActivity(),RecentPlayActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.favouriteMusic)
    public void onClickFavouriteMusic(){
        intent=new Intent(getActivity(),FavouriteMusicActivity.class);
        startActivity(intent);
    }

    @OnClick (R.id.img_createSongList)
    public void createSongList(){
        CreateSongListPopup popup=new CreateSongListPopup(getActivity(),songLists);
        popup.showPopup();
    }
}
