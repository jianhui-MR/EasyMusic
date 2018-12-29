package com.bobby.musiczone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.RecommendMusicList;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecommendMusicListAdapter extends RecyclerView.Adapter<RecommendMusicListAdapter.ViewHolder> {
    private List<RecommendMusicList> recommendMusicLists;
    private Context context;
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.recommendMusicList_img);
            name=itemView.findViewById(R.id.recommendMusicList_name);
        }
    }

    public RecommendMusicListAdapter(List<RecommendMusicList> Lists){
        recommendMusicLists =Lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null)
            context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.recommendmusiclist_cyclerview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(recommendMusicLists.get(position).getName());
        Glide.with(context)
                .load(recommendMusicLists.get(position).getPicUrl())
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return recommendMusicLists.size();
    }
}
