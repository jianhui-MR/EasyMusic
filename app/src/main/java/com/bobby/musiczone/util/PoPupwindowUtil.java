package com.bobby.musiczone.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bobby.musiczone.R;
import com.bobby.musiczone.entry.OnlineMusic;

import java.io.File;

public class PoPupwindowUtil implements View.OnClickListener {
    private PopupWindow popupWindow;
    private TextView Music_name;
    private TextView Singer;
    private TextView Album;
    private LinearLayout Download;
    private Activity mactivity;
    private OnlineMusic onlineMusic;
    public PoPupwindowUtil(Activity activity)
    {
        mactivity=activity;
    }
    public void setMorePopUpWindow()
    {
        View moreView= LayoutInflater.from(mactivity).inflate(R.layout.more_popupwindow,null,false);
        popupWindow=new PopupWindow(moreView,1000,
                RecyclerView.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp=mactivity.getWindow().getAttributes();
                lp.alpha=1f;
                mactivity.getWindow().setAttributes(lp);
            }
        });
        Music_name=moreView.findViewById(R.id.Music_name);
        Singer=moreView.findViewById(R.id.Singer);
        Album=moreView.findViewById(R.id.album);
        Download=moreView.findViewById(R.id.download);
        Download.setOnClickListener(this);
    }
    private void downloadMusic(String downladUrl,String name)
    {
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(downladUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(name);
        request.setDescription("正在下载");
        File myFile=new File(Environment.getExternalStorageDirectory().getPath()+"/MusicZone");
        if (!myFile.exists())
            myFile.mkdir();
        File saveFile=new File(myFile,name);
        request.setDestinationUri(Uri.fromFile(saveFile));
        DownloadManager manager = (DownloadManager)mactivity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    @SuppressLint("SetTextI18n")
    public void showPopupwindow(OnlineMusic monlineMusic, View view)
    {
        onlineMusic=monlineMusic;
        Music_name.setText("歌曲:"+onlineMusic.name);
        Album.setText("专辑:"+onlineMusic.album.albumname);
        Singer.setText("歌手:"+onlineMusic.artistsList.get(0).singer);
        popupWindow.showAtLocation(view,Gravity.CENTER,RecyclerView.LayoutParams.WRAP_CONTENT
                , RecyclerView.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp=mactivity.getWindow().getAttributes();
        lp.alpha=0.35f;
        mactivity.getWindow().setAttributes(lp);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.download:
                popupWindow.dismiss();
                if(NetworkUtil.isWifiNet(mactivity))
                {
                    downloadMusic(onlineMusic.audio,onlineMusic.name);
                    Toast.makeText(mactivity.getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(mactivity);
                    dialog.setMessage("当前使用的是移动网络,是否继续下载?");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadMusic(onlineMusic.audio,onlineMusic.name);
                            Toast.makeText(mactivity.getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
                break;
        }
    }
}
