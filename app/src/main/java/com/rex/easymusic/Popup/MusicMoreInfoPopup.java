package com.rex.easymusic.Popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rex.easymusic.Interface.DialogPositionButtonListener;
import com.rex.easymusic.R;
import com.rex.easymusic.Bean.OnlineMusic;
import com.rex.easymusic.fragment.MusicFragment;
import com.rex.easymusic.util.DialogUtil;
import com.rex.easymusic.util.NetworkUtil;
import com.rex.easymusic.util.ipAddressUtil;

import java.io.File;


/**
 * 点击歌曲更多按钮从这里设置视图及点击事件
 */
public class MusicMoreInfoPopup implements View.OnClickListener {
    private PopupWindow popupWindow;
    private TextView Music_name;
    private TextView Singer;
    private TextView Album;
    private TextView title;
    private RelativeLayout Download;
    private Activity mActivity;
    private OnlineMusic onlineMusic;
    private RelativeLayout addToSongList;

    public MusicMoreInfoPopup(Activity activity)
    {
        mActivity=activity;
    }

    /**
     * 下载音乐
     * @param downloadUrl
     * @param name
     */
    private void downloadMusic(String downloadUrl,String name)
    {
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(name);
        request.setDescription("正在下载");
        File myFile=new File(Environment.getExternalStorageDirectory().getPath()+"/MusicZone");
        if (!myFile.exists())
            myFile.mkdir();
        File saveFile=new File(myFile,name);
        request.setDestinationUri(Uri.fromFile(saveFile));
        DownloadManager manager = (DownloadManager)mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    /**
     * 设置点击更多后弹出来的popup
     */
    public void setMorePopUp()
    {
        View moreView= LayoutInflater.from(mActivity).inflate(R.layout.popup_music_more_info,null,false);
        popupWindow=new PopupWindow(moreView,RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.showPopupAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=mActivity.getWindow().getAttributes();
                lp.alpha=1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });
        Music_name=moreView.findViewById(R.id.tv_Music_name);
        Singer=moreView.findViewById(R.id.tv_singer);
        Album=moreView.findViewById(R.id.tv_album);
        Download=moreView.findViewById(R.id.download);
        addToSongList=moreView.findViewById(R.id.rl_add_to_songList);
        title=moreView.findViewById(R.id.tv_title);
        Download.setOnClickListener(this);
        addToSongList.setOnClickListener(this);
    }

    /**
     * 显示popup
     * @param onlineMusic
     */
    @SuppressLint("SetTextI18n")
    public void showMorePopUp(OnlineMusic onlineMusic)
    {
        this.onlineMusic=onlineMusic;
        title.setText(String.format("歌曲：%s",onlineMusic.getName()));
        Music_name.setText(String.format("歌曲：%s",onlineMusic.getName()));
        Album.setText(String.format("专辑：%s",onlineMusic.getAlbum()));
        Singer.setText(String.format("歌手：%s",onlineMusic.getSinger()));
        popupWindow.showAtLocation(mActivity.getWindow().getDecorView(),Gravity.BOTTOM,RecyclerView.LayoutParams.WRAP_CONTENT
                , RecyclerView.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp=mActivity.getWindow().getAttributes();
        lp.alpha=0.35f;
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.download:
                popupWindow.dismiss();
                if(NetworkUtil.isWifiNet(mActivity))
                {
                    downloadMusic(onlineMusic.getAudio(),onlineMusic.getName());
                    Toast.makeText(mActivity.getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DialogUtil dialogUtil=new DialogUtil();
                    dialogUtil.showAlertDialog(mActivity,"当前使用的是移动网络,是否继续下载?",true);
                    dialogUtil.setPositionButtonListener(new DialogPositionButtonListener() {
                        @Override
                        public void onPositionButtonClick() {
                            downloadMusic(onlineMusic.getAudio(),onlineMusic.getName());
                            Toast.makeText(mActivity.getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.rl_add_to_songList:
                popupWindow.dismiss();
                AddToSongListPopup addToSongListPopup=new AddToSongListPopup(mActivity,MusicFragment.songLists,onlineMusic);
                addToSongListPopup.showPopup();
        }
    }
}
