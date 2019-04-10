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
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;

import java.util.List;

/**
 * Created by Rex on 2019/3/22
 */
public class AddToSongListAdapter extends RecyclerView.Adapter<AddToSongListAdapter.ViewHolder> {

    List<SongList> songLists;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AddToSongListAdapter(List<SongList> songLists) {
        this.songLists = songLists;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tv_songListName);
            imageView=itemView.findViewById(R.id.cover);
        }
    }
    @Override
    public AddToSongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null)
            context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.rcv_add_to_songlist,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.onItemClick((Integer) v.getTag());
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddToSongListAdapter.ViewHolder holder, int position) {
        holder.textView.setText(songLists.get(position).getName());
        Glide.with(context)
                .load(songLists.get(position).getCoverUrl())
                .placeholder(R.drawable.placeholder_disk_210)
                .error(R.drawable.placeholder_disk_210)
                .into(holder.imageView);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }

}
