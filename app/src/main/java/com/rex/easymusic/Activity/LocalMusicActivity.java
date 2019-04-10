package com.rex.easymusic.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.Bean.LocalMusic;
import com.rex.easymusic.Manager.DecorationManager;
import com.rex.easymusic.R;
import com.rex.easymusic.adapter.LocalMusicAdapter;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.service.PlayerService;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;

public class LocalMusicActivity extends ToolbarActivity {

    @BindView(R.id.recyclerView)
    public RecyclerView songs_recyclerView;
    @BindView(R.id.empty_Textview)
    public TextView empty_tv;

    public View view;
    private LocalMusicAdapter adapter;
    private PlayerService service;
    public List<LocalMusic> songList;
    private Context context;
    private Intent intent;

    @Override
    public int setLayoutId() {
        return R.layout.activity_localmusic_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        service=((MusicApplication)getApplication()).getPlayerService();
        initRecyclerView();
        setToolbar();
    }

    private void setToolbar() {
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("本地音乐");
        }
    }


    private void initRecyclerView(){
        songList= DataSupport.findAll(LocalMusic.class);
        if (songList.size()==0)
        {
            empty_tv.setVisibility(View.VISIBLE);
        }
        else
        {
            service.localMusicList=songList;
            empty_tv.setVisibility(View.GONE);
            adapter=new LocalMusicAdapter(songList);
            //本地音乐点击事件
            adapter.setItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (service.position==position)
                    {
                        intent=new Intent(context, PlayMusicActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        service.position=position;
                        intent=new Intent(service.PLAY_LOCALMUSIC_ACTION);
                        sendBroadcast(intent);
                    }

                }
            });
            LinearLayoutManager layoutManager=new LinearLayoutManager(context);
            songs_recyclerView.setLayoutManager(layoutManager);
            songs_recyclerView.setAdapter(adapter);
            songs_recyclerView.addItemDecoration(new DecorationManager(context,DecorationManager.VERTICAL_LIST));
        }
    }
}
