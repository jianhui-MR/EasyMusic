package com.bobby.musiczone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.RecommendNewSong;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Bobby on 2018/12/29
 */
public class RecommendNewSongAdapter extends RecyclerView.Adapter<RecommendNewSongAdapter.ViewHolder> {
    private List<RecommendNewSong> recommendNewSongList;
    private Context context;
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.recommendMusic_img);
            textView=itemView.findViewById(R.id.recommendMusic_name);
        }
    }

    public RecommendNewSongAdapter(List<RecommendNewSong> list){
        recommendNewSongList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null)
            context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.recommandnewsong_cyclerview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(recommendNewSongList.get(position).getName());
        Glide.with(context)
                .load(recommendNewSongList.get(position).getPicUrl())
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return recommendNewSongList.size();
    }

}
