package com.rex.easymusic.Popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.Bean.SongList;
import com.rex.easymusic.R;
import com.rex.easymusic.util.DialogUtil;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class CreateSongListPopup implements View.OnClickListener, Callback {
    private PopupWindow popupWindow;
    private Activity activity;
    private EditText et_songlistName;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private List<SongList> songLists;
    private String createSongListUrl= ipAddressUtil.serviceIp+"/type/addType";
    private String addToSongListUrl= ipAddressUtil.serviceIp+"/typeSongList/addToSongList";
    private FormBody body;
    private DialogUtil dialogUtil=new DialogUtil();
    private Handler handler;
    private OnlineMusic onlineMusic;

    public CreateSongListPopup(Activity activity, List<SongList> songLists, OnlineMusic onlineMusic) {
        this.songLists=songLists;
        this.activity = activity;
        this.onlineMusic=onlineMusic;
        initHandler();
        intiPopup();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        dialogUtil.closeProgressDialog();
                        ToastUtils.show("网络异常");
                        break;
                    case 1:
                        int typeId=(Integer) msg.obj;
                        addToSonglist(typeId);
                        break;
                    case 2:
                        dialogUtil.closeProgressDialog();
                        ToastUtils.show("添加失败");
                        break;
                    case 3:
                        dialogUtil.closeProgressDialog();
                        ToastUtils.show("添加成功");

                }
            }
        };
    }

    private void intiPopup(){
        View view=LayoutInflater.from(activity).inflate(R.layout.popup_create_songlist,null,false);
        popupWindow=new PopupWindow(view,RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.showPopupAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
                lp.alpha=1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        et_songlistName=view.findViewById(R.id.et_songList_name);
        tv_cancel=view.findViewById(R.id.tv_cancel);
        tv_confirm=view.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }
    public void showPopup(){
        popupWindow.showAtLocation(activity.getWindow().getDecorView(),Gravity.CENTER,RecyclerView.LayoutParams.WRAP_CONTENT
                , RecyclerView.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=0.35f;
        activity.getWindow().setAttributes(lp);
    }

    private void createNewSongList(String songListName){
        body=new FormBody.Builder()
                .add("userAccount",LoginActivity.userAccount)
                .add("type",songListName)
                .build();
        dialogUtil.showProgressDialog(activity,"正在加载...");
        HttpUtil.sendOkHttpRequest(createSongListUrl,body,CreateSongListPopup.this);
    }
    private void addToSonglist(int typeId){
        body=new FormBody.Builder()
                .add("typeId", String.valueOf(typeId))
                .add("songId", String.valueOf(onlineMusic.getId()))
                .add("songName",onlineMusic.getName())
                .add("singer",onlineMusic.getSinger())
                .add("coverUrl",onlineMusic.getPicUrl())
                .add("audioUrl",onlineMusic.getAudio())
                .add("lrcUrl",onlineMusic.getLrcUrl())
                .add("album",onlineMusic.getAlbum())
                .build();
        HttpUtil.sendOkHttpRequest(addToSongListUrl, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.obtainMessage(0).sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (new JSONObject(response.body().string()).getInt("status")==0){
                        handler.obtainMessage(3).sendToTarget();
                    }else {
                        handler.obtainMessage(2).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                popupWindow.dismiss();
                break;
            case R.id.tv_confirm:
                String songListName=et_songlistName.getText().toString();
                if (songListName.equals(""))
                    ToastUtils.show("歌单名字不能为空");
                else {
                    createNewSongList(songListName);
                }
                popupWindow.dismiss();
                break;
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        handler.obtainMessage(0).sendToTarget();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String responseBody=response.body().string();
        Log.e("TAG", "onResponse: "+responseBody );
        try {
            JSONObject jsonObject=new JSONObject(responseBody);
            if (jsonObject.getInt("status")==0){
                int typeId=jsonObject.getInt("typeId");
                handler.obtainMessage(1,typeId).sendToTarget();
            }else {
                handler.obtainMessage(2).sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
