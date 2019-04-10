package com.rex.easymusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;
import com.rex.easymusic.Bean.LocalMusic;

import java.util.List;

/**
 * Created by Bobby on 2018/7/26.
 * 点击右边播放列表弹出的popup列表recyclerView适配器(使用于本地音乐)
 */

public class LocalMusicPlayListAdapter extends RecyclerView.Adapter<LocalMusicPlayListAdapter.ViewHolder> {
    private List<LocalMusic> songList;
    private Context context;

    private OnItemClickListener mItemClickListener;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView singer;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.song_name);
            singer=itemView.findViewById(R.id.song_singer);
        }
    }
    public LocalMusicPlayListAdapter(List<LocalMusic> msongList)
    {
        songList=msongList;
    }

    @Override
    public LocalMusicPlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.rvc_playlist,parent,false);
        LocalMusicPlayListAdapter.ViewHolder holder=new LocalMusicPlayListAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null){
                    mItemClickListener.onItemClick((Integer) v.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(LocalMusicPlayListAdapter.ViewHolder holder, int position) {
        LocalMusic localMusic=songList.get(position);
        holder.name.setText(localMusic.getName());
        holder.singer.setText(localMusic.getSinger());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
