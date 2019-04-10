package com.rex.easymusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rex.easymusic.Bean.SongList;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;

import java.util.List;

public class SongListAdapter  extends RecyclerView.Adapter<SongListAdapter.ViewHolder>  {

    List<SongList> songLists;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnClickMoreListener onClickMoreListener;

    public void setOnClickMoreListener(OnClickMoreListener onClickMoreListener) {
        this.onClickMoreListener = onClickMoreListener;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SongListAdapter(List<SongList> songLists) {
        this.songLists = songLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView img_cover;
        ImageView img_more;
        public ViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tv_songListName);
            img_cover=itemView.findViewById(R.id.cover);
            img_more=itemView.findViewById(R.id.img_more);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null)
            context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.rcv_songlist,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.onItemClick((Integer)v.getTag());
            }
        });
        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickMoreListener!=null)
                    onClickMoreListener.onMoreClick((Integer)v.getTag());
            }
        });
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(songLists.get(position).getName());
        Glide.with(context)
                .load(songLists.get(position).getCoverUrl())
                .placeholder(R.drawable.placeholder_disk_210)
                .error(R.drawable.placeholder_disk_210)
                .into(holder.img_cover);
        holder.itemView.setTag(position);
        holder.img_more.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }
}
