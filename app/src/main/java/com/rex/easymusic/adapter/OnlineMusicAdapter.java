package com.rex.easymusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;

import java.util.List;

/**
 * Created by Bobby on 2018/7/14.
 */

public class OnlineMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OnlineMusic> OnlineMusicList;
    private Context context;
    private static final int TYPE_FOOT=0;
    private static final int TYPE_ITEM=1;

    private OnItemClickListener mItemClickListener;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    private OnClickMoreListener mclickMoreListener;
    public void setClickMoreListener(OnClickMoreListener clickMoreListener){
        mclickMoreListener=clickMoreListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView singer;
        ImageView img_more;
        ImageView img_cover;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.song_name);
            singer=itemView.findViewById(R.id.song_singer);
            img_more=itemView.findViewById(R.id.more);
            img_cover=itemView.findViewById(R.id.img_cover);
        }
    }
    public OnlineMusicAdapter(List<OnlineMusic> msongList)
    {
        OnlineMusicList=msongList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.rcv_onlinemusic,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null){
                    mItemClickListener.onItemClick((Integer) v.getTag());
                }
            }
        });
        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mclickMoreListener!=null)
                    mclickMoreListener.onMoreClick((Integer)v.getTag());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OnlineMusic music=OnlineMusicList.get(position);
        ViewHolder viewHolder=(ViewHolder)holder;
        viewHolder.name.setText(music.getName());
        viewHolder.singer.setText(music.getSinger());
        viewHolder.itemView.setTag(position);
        viewHolder.img_more.setTag(position);
        Glide.with(context)
                .load(music.getPicUrl())
                .placeholder(R.drawable.placeholder_disk_210)
                .into(viewHolder.img_cover);
    }


    @Override
    public int getItemCount() {
        return OnlineMusicList.size();
    }
}
