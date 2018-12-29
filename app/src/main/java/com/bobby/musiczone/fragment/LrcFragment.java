package com.bobby.musiczone.fragment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.bobby.musiczone.Activity.BaseActivity;
import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.Lrc;
import com.bobby.musiczone.service.PlayerService;
import com.bobby.musiczone.util.HttpUtil;
import com.bobby.musiczone.util.LrcUtil;
import com.bobby.musiczone.widget.LrcView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class LrcFragment extends Fragment implements Runnable {

    @BindView(R.id.lrc_view)
    public  LrcView lrcView;
    @BindView(R.id.ScrollLrc)
    public ScrollView scrollView;


    private int currentPosition = 0;
    private  List<Lrc> mlrcList=null;
    private Handler handler;
    private playerRecevier playerRecevier;
    private Unbinder unbinder;
    private final String TAG="LrcFragment";
    private PlayerService service;
    public final int LOCAL=0;
    public final int ONLINE=1;

    /* 生命周期 */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.lrc_layout,container,false);
        unbinder=ButterKnife.bind(this,view);
        setScrollViewTouch();
        setLrc();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=PlayerService.getService();
        setHandler();
        registerBroadcast();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(playerRecevier);
//        unbinder.unbind();
    }
    /* 生命周期 */


    /**
     * Handler设置
     */
    @SuppressLint("HandlerLeak")
    private void setHandler() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        setCurrentPosition();
                        break;
                    case 2:
                        lrcView.setLrcList(mlrcList);
                        break;
                }
            }
        };
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        playerRecevier=new playerRecevier();
        IntentFilter filter=new IntentFilter();
        filter.addAction(PlayerService.plyingAction);
        filter.addAction(BaseActivity.BindSuccess);
        getContext().registerReceiver(playerRecevier,filter);
    }


    /**
     * 内部广播类，监听歌曲播放
     */
    class playerRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String  action=intent.getAction();
            switch (action){
                case PlayerService.plyingAction:
                    setLrc();
                    break;
            }
        }
    }

    /**
     * 对scrollView进行手势监听，监听按下后停止滚动，松手2.5秒后恢复自动滚动
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setScrollViewTouch(){
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action=motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        handler.removeCallbacks(LrcFragment.this);
                        handler.postDelayed(LrcFragment.this,2500);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 设置歌词
     */
    private void setLrc()
    {
        if (service.musicType==service.ONLINE)
        {
            HttpUtil.sendOkHttpRequest(service.onlineMusicList.get(service.position).lrcUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    lrcView.setLrcList(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String LrcContent=jsonObject.getJSONObject("lrc").getString("lyric");
                        mlrcList= LrcUtil.ParseLrc(LrcContent);
                        handler.sendEmptyMessage(2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            //开启线程滚动
            handler.post(this);
        }
        else{
            mlrcList=null;
            handler.sendEmptyMessage(2);
            handler.removeCallbacks(this);
        }

    }

    /**
     * 获取当前歌词正确位置
     */
    public void setCurrentPosition()
    {
        if (mlrcList==null)
            return;
        int currentMillis = service.playerEngine.getCurrentPosition();
        if (currentMillis < mlrcList.get(0).time) {
            currentPosition = 0;
        }
        else if (currentMillis> mlrcList.get(mlrcList.size()-1).time)
        {
            currentPosition=mlrcList.size()-1;
        }
        else {
            for (int i=0;i<mlrcList.size();i++)
            {
                if (currentMillis>=mlrcList.get(i).time&&currentMillis<=mlrcList.get(i+1).time)
                {
                    currentPosition=i;
                }
            }
        }
        //移动歌词
        scrollView.smoothScrollTo(0,(currentPosition)*150+20);
    }

    @Override
    public void run() {
        try {
            if (service.playerEngine.isPlaying())
            {
                handler.sendEmptyMessage(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        handler.postDelayed(this, 600);
    }
}
