package com.rex.easymusic.Popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Bean.SongList;
import com.rex.easymusic.EventBus.MessageEvent;
import com.rex.easymusic.R;
import com.rex.easymusic.adapter.AddToSongListAdapter;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.util.DialogUtil;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Rex on 2019/3/22
 */
public class AddToSongListPopup implements View.OnClickListener, Callback {
    private PopupWindow popupWindow;
    private Activity activity;
    private LinearLayout create_songList;
    private RecyclerView recyclerView;
    private AddToSongListAdapter adapter;
    private List<SongList> songLists;
    private OnlineMusic onlineMusic;
    private String addToSongListUrl= ipAddressUtil.serviceIp+"/typeSongList/addToSongList";
    private FormBody body;
    private DialogUtil dialogUtil=new DialogUtil();
    private Handler handler;

    public AddToSongListPopup(Activity activity, List<SongList> songLists, OnlineMusic onlineMusic) {
        this.activity = activity;
        this.songLists=songLists;
        this.onlineMusic=onlineMusic;
        initHandler();
        setPopupWindow();
        initRecyclerView();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        dialogUtil.closeProgressDialog();
                        ToastUtils.show("网络异常");
                        break;
                    case 1:
                        dialogUtil.closeProgressDialog();
                        ToastUtils.show("添加成功");
                        break;
                    case 2:
                        dialogUtil.closeProgressDialog();
                        ToastUtils.show("添加失败");
                        break;

                }
            }
        };
    }

    public void setPopupWindow(){
        View view=LayoutInflater.from(activity).inflate(R.layout.popup_add_to_songlist,null,false);
        popupWindow=new PopupWindow(view,RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.showPopupScale);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
                lp.alpha=1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        create_songList=view.findViewById(R.id.create_songList);
        create_songList.setOnClickListener(this);
        recyclerView=view.findViewById(R.id.recyclerView);
    }

    private void initRecyclerView(){
        adapter=new AddToSongListAdapter(songLists);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                body=new FormBody.Builder()
                        .add("typeId", String.valueOf(songLists.get(position).getTypeId()))
                        .add("songId", String.valueOf(onlineMusic.getId()))
                        .add("songName",onlineMusic.getName())
                        .add("singer",onlineMusic.getSinger())
                        .add("coverUrl",onlineMusic.getPicUrl())
                        .add("audioUrl",onlineMusic.getAudio())
                        .add("lrcUrl",onlineMusic.getLrcUrl())
                        .add("album",onlineMusic.getAlbum())
                        .build();
                dialogUtil.showProgressDialog(activity,"正在加载...");
                HttpUtil.sendOkHttpRequest(addToSongListUrl,body,AddToSongListPopup.this);
                popupWindow.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
    }

    public void showPopup(){
        popupWindow.showAtLocation(activity.getWindow().getDecorView(),Gravity.CENTER,RecyclerView.LayoutParams.WRAP_CONTENT
                , RecyclerView.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=0.35f;
        activity.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_songList:
                popupWindow.dismiss();
                CreateSongListPopup popup=new CreateSongListPopup(activity,songLists,onlineMusic);
                popup.showPopup();
                break;
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            if (new JSONObject(response.body().string()).getInt("status")==0){
                handler.obtainMessage(1).sendToTarget();
            }else {
                handler.obtainMessage(2).sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
