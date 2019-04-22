package com.rex.easymusic.Popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rex.easymusic.Bean.SongList;
import com.rex.easymusic.EventBus.MessageEvent;
import com.rex.easymusic.R;
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

import static org.litepal.LitePalApplication.getContext;


public class EditSongListPopup implements View.OnClickListener {
    private SongList songList;
    private Activity activity;
    private PopupWindow popupWindow;
    private EditText et_songlistName;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private Handler handler;
    private TextView tv_title;
    private DialogUtil dialogUtil=new DialogUtil();
    private String updateSongListUrl=ipAddressUtil.serviceIp+"/type/updateType";
    private FormBody formBody;
    private InputMethodManager manager;

    public EditSongListPopup(SongList songList,Activity activity) {
        this.songList = songList;
        this.activity=activity;
        initHandler();
        intiPopup();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialogUtil.closeProgressDialog();
                switch (msg.what){
                    case 0:
                        ToastUtils.show("网络异常");
                        break;
                    case 1:
                        break;
                    case 2:
                        ToastUtils.show("修改失败");
                        break;
                    case 3:
                        EventBus.getDefault().post(new MessageEvent(2));
                        ToastUtils.show("修改成功");
                        break;

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
        tv_title=view.findViewById(R.id.tv_title);
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        tv_title.setText("编辑歌单信息");
        et_songlistName.setText(songList.getName());
        et_songlistName.setSelection(0,songList.getName().length());
        et_songlistName.setFocusable(true);
        et_songlistName.setFocusableInTouchMode(true);
        et_songlistName.requestFocus();
    }

    public void showPopup(){
        popupWindow.showAtLocation(activity.getWindow().getDecorView(),Gravity.CENTER,RecyclerView.LayoutParams.WRAP_CONTENT
                , RecyclerView.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=0.35f;
        activity.getWindow().setAttributes(lp);
        manager = ((InputMethodManager)et_songlistName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void updateSongList(String name){
        formBody=new FormBody.Builder()
                .add("typeId", String.valueOf(songList.getTypeId()))
                .add("type",name)
                .build();
        HttpUtil.sendOkHttpRequest(updateSongListUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    if (jsonObject.getInt("status")==0){
                        songList.setName(name);
                        handler.sendEmptyMessage(3);
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
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager.hideSoftInputFromWindow(et_songlistName.getWindowToken(),0);
        switch (v.getId()){
            case R.id.tv_cancel:
                popupWindow.dismiss();
                break;
            case R.id.tv_confirm:
                popupWindow.dismiss();
                dialogUtil.showProgressDialog(activity,"正在加载...");
                if (et_songlistName.getText().toString().equals(songList.getName()))
                    handler.sendEmptyMessage(4);
                updateSongList(et_songlistName.getText().toString());
        }
    }
}
