package com.bobby.musiczone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.LocalMusic;

import java.util.List;

/**
 * Created by Bobby on 2018/7/26.
 * 点击右边播放列表弹出的popup列表recyclerView适配器
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
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
            name=itemView.findViewById(R.id.song_name2);
            singer=itemView.findViewById(R.id.song_singer2);
        }
    }
    public PlayListAdapter(List<LocalMusic> msongList)
    {
        songList=msongList;
    }

    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.playlist_item,parent,false);
        PlayListAdapter.ViewHolder holder=new PlayListAdapter.ViewHolder(view);
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
    public void onBindViewHolder(PlayListAdapter.ViewHolder holder, int position) {
        LocalMusic song=songList.get(position);
        holder.name.setText(song.getName());
        holder.singer.setText(song.getSinger());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
