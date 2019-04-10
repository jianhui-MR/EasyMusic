package com.rex.easymusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;
import com.rex.easymusic.Bean.RecommendNewSong;
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

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null)
            context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.rcv_recommandnewsong,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick((Integer)view.getTag());
                    Log.e("TAG", "onClick: "+(Integer)view.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(recommendNewSongList.get(position).getName());
        Glide.with(context)
                .load(recommendNewSongList.get(position).getPicUrl())
                .into(holder.img);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return recommendNewSongList.size();
    }

}
