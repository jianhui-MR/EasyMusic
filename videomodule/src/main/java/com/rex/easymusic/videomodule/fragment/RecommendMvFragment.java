package com.rex.easymusic.videomodule.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rex.easymusic.videomodule.Bean.video;
import com.rex.easymusic.videomodule.R;
import com.rex.easymusic.videomodule.Util.HttpUtil;
import com.rex.easymusic.videomodule.Util.VideoUtil;
import com.rex.easymusic.videomodule.adapter.videoAdapter;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Rex on 2019/2/20
 */
public class RecommendMvFragment extends Fragment {
    private View view;
    private List<video> videoList=new ArrayList<>();

    private RecyclerView recyclerView;
    private videoAdapter adapter;
    private Handler handler;

    private String MvUrl="http://106.13.36.192:4000/top/mv";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.mv_frag_layout,container,false);
        bindView();
        initHandler();
        initRecyclerView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.releaseAllVideos();
    }

    /*------------------------------------------------------------------------------*/
    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    /**
     * 控件绑定
     */
    private void bindView(){
        recyclerView=view.findViewById(R.id.video_recyclerView);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        if (videoList.size()!=30)
            videoList.clear();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new videoAdapter(getActivity(),videoList);
        recyclerView.setAdapter(adapter);

        /**
         * 设置滑动监听，在滑动时监听正在播放的视频是否在视图中，是则继续播放，不是则Release
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisibleItem, lastVisibleItem;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //找到第一个可见视图Item的位置
                firstVisibleItem=linearLayoutManager.findFirstVisibleItemPosition();
                //找到最后一个可见视图Item的位置
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();

                //大于等于0证明有视频在播放
                if (GSYVideoManager.instance().getPlayPosition()>=0){
                    //获取当前视频播放的位置
                    int position=GSYVideoManager.instance().getPlayPosition();
                    if (position < firstVisibleItem || position > lastVisibleItem) {
                        //如果滑出去了上面或下面停止视频播放
                        GSYVideoManager.releaseAllVideos();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        HttpUtil.sendOkHttpRequest(MvUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String responseBody=response.body().string();
                try {
                    JSONArray jsonArray=new JSONObject(responseBody).getJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        video e=new Gson().fromJson(jsonObject.toString(),video.class);
                        VideoUtil videoUtil=new VideoUtil(e.getId());
                        e.setPlayUrl(videoUtil.getGetVideoUrl());
                        videoList.add(e);
                        handler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
