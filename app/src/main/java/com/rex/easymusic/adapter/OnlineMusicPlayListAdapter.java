package com.rex.easymusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;

import java.util.List;

public class OnlineMusicPlayListAdapter extends RecyclerView.Adapter<OnlineMusicPlayListAdapter.ViewHolder> {
    private List<OnlineMusic> songList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView singer;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.song_name);
            singer=itemView.findViewById(R.id.song_singer);
        }
    }

    public OnlineMusicPlayListAdapter(List<OnlineMusic> msongList)
    {
        songList=msongList;
    }

    private OnItemClickListener mItemClickListener;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public OnlineMusicPlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.rvc_playlist,parent,false);
        OnlineMusicPlayListAdapter.ViewHolder holder=new OnlineMusicPlayListAdapter.ViewHolder(view);
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
    public void onBindViewHolder(OnlineMusicPlayListAdapter.ViewHolder holder, int position) {

        OnlineMusic onlineMusic=songList.get(position);
        holder.name.setText(onlineMusic.getName());
        holder.singer.setText(onlineMusic.getSinger());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
