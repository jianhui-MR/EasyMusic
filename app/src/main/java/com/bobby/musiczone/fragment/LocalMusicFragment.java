package com.bobby.musiczone.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bobby.musiczone.Activity.BaseActivity;
import com.bobby.musiczone.Activity.PlayMusicAvtivity;
import com.bobby.musiczone.Manager.DecorationManager;
import com.bobby.musiczone.MainActivity;
import com.bobby.musiczone.R;
import com.bobby.musiczone.adapter.LocalMusicAdapter;
import com.bobby.musiczone.adapter.OnItemClickListener;
import com.bobby.musiczone.entry.LocalMusic;
import com.bobby.musiczone.service.PlayerService;


import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bobby.musiczone.MainActivity.QUERYSONG_ACTION;

/**
 * Created by Bobby on 2018/7/13.
 */

public class LocalMusicFragment extends Fragment{

    @BindView(R.id.songs_recyclerview)
    public RecyclerView songs_recyclerview;
    @BindView(R.id.empty_Textview)
    public TextView empty_tv;

    public  View view;
    private LocalMusicAdapter adapter;
    private PlayerService service;
    private Unbinder unbinder;

    public List<LocalMusic> songList;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.localmusic_layout,container,false);
        unbinder=ButterKnife.bind(this,view);
        service=PlayerService.getService();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        QuerySongBroadCast querySongBroadCast=new QuerySongBroadCast();
        IntentFilter filter=new IntentFilter();
        filter.addAction(QUERYSONG_ACTION);
        getActivity().registerReceiver(querySongBroadCast,filter);

        sacnSongs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * 扫描歌曲
     */
    public void sacnSongs()
    {
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
                        Intent intent=new Intent(getActivity(), PlayMusicAvtivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        service.position=position;
                        Intent intent=new Intent(service.PLAY_LOCALMUSIC_ACTION);
                        getActivity().sendBroadcast(intent);
                    }

                }
            });
            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
            songs_recyclerview.setLayoutManager(layoutManager);
            songs_recyclerview.setAdapter(adapter);
            songs_recyclerview.addItemDecoration(new DecorationManager(getContext(),DecorationManager.VERTICAL_LIST));
        }
    }
    class QuerySongBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case QUERYSONG_ACTION:
                    sacnSongs();
                    break;
            }

        }
    }
}
