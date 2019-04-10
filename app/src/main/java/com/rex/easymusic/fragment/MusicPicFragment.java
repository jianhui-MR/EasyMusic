package com.rex.easymusic.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.R;
import com.rex.easymusic.service.PlayerService;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rex on 2018/9/13.
 * 专辑图片fragment
 */
public class MusicPicFragment extends Fragment {

    @BindView(R.id.rotate_disk_layout)
    public  RelativeLayout rotate_layout;
    @BindView(R.id.album)
    public  CircleImageView album;

    public  ObjectAnimator mRotateAnimation;
    private playerRecevier playerRecevier;
    private PlayerService service;
    private View view;
    private final String TAG="MusicPicFragment";
    private final int LOCAL=0;
    private final int ONLINE=1;
    private Unbinder unbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        service=((MusicApplication)getActivity().getApplication()).getPlayerService();
        registerBroadCast();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_music_pic,container,false);
        unbinder=ButterKnife.bind(this,view);
        rotateAnim();
        return view;
    }

    @Override
    public void onResume() {
        initView();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(playerRecevier);
//        unbinder.unbind();
    }

    private void registerBroadCast()
    {
        playerRecevier=new playerRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PlayerService.plyingAction);
        filter.addAction(PlayerService.pauseAction );
        getContext().registerReceiver(playerRecevier,filter);
    }


    @SuppressLint("NewApi")
    class playerRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action=intent.getAction();
            switch (action){
                case PlayerService.plyingAction:
                    if (service.musicType==LOCAL){
                        if (service.localMusicList.get(service.position).getAlbumArt()!=null)

                            album.setImageBitmap(BitmapFactory.decodeFile(
                                    service.localMusicList.get(service.position).getAlbumArt()));
                        else
                            album.setImageResource(R.drawable.albumart);
                    }

                    else if (service.musicType==ONLINE){
                        if (album!=null){
                            Glide.with(getActivity())
                                    .load(service.onlineMusicList.get(service.position).getPicUrl())
                                    .placeholder(R.drawable.albumart)
                                    .dontAnimate()
                                    .thumbnail( 0.5f )
                                    .into(album);
                        }

                    }

                    mRotateAnimation.resume();
                    break;
                case PlayerService.pauseAction:
                    Log.e(TAG, "onReceive: 暂停音乐封面" );
                    mRotateAnimation.pause();
                    break;
            }
        }
    }

    @SuppressLint("NewApi")
    private void initView()
    {
        if (!service.playerEngine.isPlaying())
            mRotateAnimation.pause();
        switch (service.musicType)
        {
            case LOCAL:
                if (service.localMusicList.get(service.position).getAlbumArt()!=null)
                    album.setImageBitmap(BitmapFactory.decodeFile(
                            service.localMusicList.get(service.position).getAlbumArt()));
                else
                    album.setImageResource(R.drawable.albumart);
                break;
            case ONLINE:
                Glide.with(getContext())
                        .load(service.onlineMusicList.get(service.position).getPicUrl())
                        .dontAnimate()
                        .placeholder(R.drawable.albumart)
                        .into(album);
                break;
        }
    }

    public void rotateAnim() {
        mRotateAnimation = ObjectAnimator.ofFloat(rotate_layout, "rotation", 0, 359);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(25 * 1000);
        mRotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimation.start();
    }
}
