package com.rex.easymusic.Popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rex.easymusic.Bean.SongList;
import com.rex.easymusic.EventBus.MessageEvent;
import com.rex.easymusic.Interface.DialogPositionButtonListener;
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.R;
import com.rex.easymusic.fragment.MusicFragment;
import com.rex.easymusic.util.DialogUtil;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Rex on 2019/3/29
 */
public class SongListMoreInfoPopup implements View.OnClickListener {
    private SongList songList;
    private Activity activity;
    private PopupWindow popupWindow;
    WindowManager.LayoutParams layoutParams;
    private TextView songListName;
    private RelativeLayout rl_edit_info;
    private RelativeLayout rl_delete_songList;
    private String deleteSongListUrl=ipAddressUtil.serviceIp+"/type/deleteType";
    private DialogUtil dialogUtil=new DialogUtil();
    private Handler handler;

    public SongListMoreInfoPopup(SongList songList, Activity activity) {
        this.songList = songList;
        this.activity=activity;
        initPopup();
        initHandler();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialogUtil.closeProgressDialog();
                switch (msg.what){
                    case 0:
                        ToastUtils.show("网络异常");
                        break;
                    case 1:
                        MusicFragment.songLists.remove(songList);
                        EventBus.getDefault().post(new MessageEvent(2));
                        ToastUtils.show("删除成功");
                        break;
                    case 2:
                        ToastUtils.show("删除失败");
                }
            }
        };
    }

    private void initPopup() {
        View view=LayoutInflater.from(activity).inflate(R.layout.popup_songlist_more_info,null,false);
        popupWindow=new PopupWindow(view,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout
                .LayoutParams.WRAP_CONTENT,true);
        popupWindow.setAnimationStyle(R.style.showPopupAnimation);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layoutParams=activity.getWindow().getAttributes();
                layoutParams.alpha=1f;
                activity.getWindow().setAttributes(layoutParams);
            }
        });
        popupWindow.showAtLocation(activity.getWindow().getCurrentFocus(),Gravity.BOTTOM,
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        songListName=view.findViewById(R.id.songList_name);
        rl_edit_info=view.findViewById(R.id.rl_edit_songList_info);
        rl_delete_songList=view.findViewById(R.id.rl_delete_songList);
        rl_edit_info.setOnClickListener(this);
        rl_delete_songList.setOnClickListener(this);
    }

    public void showPopup(){
        songListName.setText(String.format("歌单：%s",songList.getName()));
        popupWindow.showAtLocation(activity.getWindow().getCurrentFocus(),Gravity.BOTTOM,
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams=activity.getWindow().getAttributes();
        layoutParams.alpha=0.35f;
        activity.getWindow().setAttributes(layoutParams);
    }

    private void deleteSongList(int id){
        FormBody body=new FormBody.Builder()
                .add("typeId", String.valueOf(id))
                .build();
        HttpUtil.sendOkHttpRequest(deleteSongListUrl, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("status")==0){
                        handler.sendEmptyMessage(1);
                    }
                    else
                        handler.sendEmptyMessage(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_edit_songList_info:
                popupWindow.dismiss();
                EditSongListPopup editSongListPopup=new EditSongListPopup(songList,activity);
                editSongListPopup.showPopup();
                break;
            case R.id.rl_delete_songList:
                popupWindow.dismiss();
                dialogUtil.showAlertDialog(activity,"提示","删除歌单后无法恢复，确定删除？");
                dialogUtil.setPositionButtonListener(new DialogPositionButtonListener() {
                    @Override
                    public void onPositionButtonClick() {
                        dialogUtil.showProgressDialog(activity,"正在加载");
                        deleteSongList(songList.getTypeId());
                    }
                });
                break;
        }
    }
}
