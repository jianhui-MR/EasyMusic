package com.bobby.musiczone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobby.musiczone.entry.OnlineMusic;
import com.bobby.musiczone.R;

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
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.song_name);
            singer=itemView.findViewById(R.id.song_singer);
            imageView=itemView.findViewById(R.id.more);
        }
    }
    class FootViewholder extends RecyclerView.ViewHolder
    {
        public FootViewholder(View itemView) {
            super(itemView);
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
        if (viewType==TYPE_FOOT)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.include_loading,parent,false);
            return new FootViewholder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.recyclerview_item,parent,false);
            ViewHolder holder=new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener!=null){
                        mItemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mclickMoreListener!=null)
                        mclickMoreListener.onMoreClick((Integer)v.getTag());
                }
            });
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder)
        {
            OnlineMusic Music=OnlineMusicList.get(position);
            ViewHolder viewHolderholder=(ViewHolder)holder;
            viewHolderholder.name.setText(Music.name);
            viewHolderholder.singer.setText(Music.artistsList.get(0).singer);
            viewHolderholder.itemView.setTag(position);
            viewHolderholder.imageView.setTag(position);
        }
        else
        {
            if (position==0)
                holder.itemView.setVisibility(View.GONE);
            else
                holder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1==getItemCount())
            return  TYPE_FOOT;
        else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return OnlineMusicList.size()+1;
    }
}
