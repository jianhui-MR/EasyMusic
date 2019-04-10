package com.rex.easymusic.Popup;

import android.app.Activity;
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
import com.rex.easymusic.Interface.OnClickMoreListener;
import com.rex.easymusic.R;

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
    public SongListMoreInfoPopup(SongList songList, Activity activity) {
        this.songList = songList;
        this.activity=activity;
        initPopup();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_edit_songList_info:
                break;
            case R.id.rl_delete_songList:
                break;
        }
    }
}
