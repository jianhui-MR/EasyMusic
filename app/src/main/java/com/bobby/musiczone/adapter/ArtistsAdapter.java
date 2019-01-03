package com.bobby.musiczone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.Artist;
import com.bumptech.glide.Glide;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 热门歌手ArtistsAdapter适配器
 */
public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder> {
    private List<Artist> mArtistList;
    private Context context;

    public ArtistsAdapter(List<Artist> artistList){
        mArtistList=artistList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView artistsName;
        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.artist_img);
            artistsName=itemView.findViewById(R.id.artist_name);
        }
    }


    private OnItemClickListener mItemClickListener;
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null) {
            context = parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.artist_recyclerview,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick((Integer)v.getTag());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.artistsName.setText(mArtistList.get(position).getName());
        Glide.with(context)
                .load(mArtistList.get(position).getImgUrl())
                .dontAnimate()
                .into(holder.circleImageView);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

}
