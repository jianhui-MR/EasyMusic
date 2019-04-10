package com.rex.easymusic.videomodule.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rex.easymusic.videomodule.Bean.video;
import com.rex.easymusic.videomodule.R;
import com.rex.easymusic.videomodule.videoPlayer.SampleCoverVideo;

import java.util.List;

/**
 * Created by Rex on 2019/2/20
 */
public class videoAdapter extends RecyclerView.Adapter<videoAdapter.ViewHolder> {
    private Context mcontext;
    private List<video> videoList;
    public videoAdapter(Context context, List<video> videoList){
        mcontext=context;
        this.videoList=videoList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SampleCoverVideo coverVideo;
        private TextView title;
        private TextView artists;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverVideo=itemView.findViewById(R.id.gsyVideo);
            title=itemView.findViewById(R.id.video_title);
            artists=itemView.findViewById(R.id.video_Artist);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.video_item,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        video video=videoList.get(i);
        final SampleCoverVideo sampleCoverVideo=viewHolder.coverVideo;
        sampleCoverVideo.setUpLazy(video.getPlayUrl(),true,null,null,video.getVideoName());
        sampleCoverVideo.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleCoverVideo.startWindowFullscreen(mcontext, true, true);
            }
        });
        sampleCoverVideo.getBackButton().setVisibility(View.GONE);
        sampleCoverVideo.setAutoFullWithSize(true);
        sampleCoverVideo.setReleaseWhenLossAudio(false);
        sampleCoverVideo.setShowFullAnimation(true);
        sampleCoverVideo.setIsTouchWiget(false);
        sampleCoverVideo.setShowPauseCover(true);
        sampleCoverVideo.setPlayPosition(i);
        sampleCoverVideo.loadCoverImage(video.getCoverUrl(),R.drawable.empty_drawable);

        viewHolder.title.setText(video.getVideoName());
        viewHolder.artists.setText(video.getArtistName());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
}
