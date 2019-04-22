package com.rex.easymusic.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.BroadCast.NotificationRecevier;
import com.rex.easymusic.R;
import com.rex.easymusic.service.PlayerService;

import java.util.Calendar;
import java.util.Timer;

import static com.rex.easymusic.Activity.MainActivity.SetTimerTask;

public class TimerTakUtil {
    private Handler TimerHandler;
    private PlayerService service;
    public static Timer mTimer;
    private MyTimerTask myTimerTask;
    private Activity activity;
    public static NotificationManager notificationManager;
    private Bitmap bitmap;
    public TimerTakUtil(PlayerService service, Activity activity)
    {
        this.service=service;
        this.activity=activity;
        mTimer=new Timer();
    }
    @SuppressLint("HandlerLeak")
    public void  setTimerTask()
    {
        new TimePickerDialog(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int time=(hourOfDay*60+minute)*60*1000;
                startTimerTask(time);
                Toast.makeText(MusicApplication.getAppContext(),"定时设置成功,将在"+hourOfDay
                        +"小时"+minute+"分钟后暂停歌曲",Toast.LENGTH_SHORT).show();
                Calendar c = Calendar.getInstance();
                int hour=0;
                int min=c.get(Calendar.MINUTE)+minute;
                if (min>=60)
                {
                    hour+=1;
                    min-=60;
                }
                hour=hour+c.get(Calendar.HOUR_OF_DAY)+hourOfDay;
                setNotification(hour,min);
            }
        },0,0,true).show();
        TimerHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==SetTimerTask)
                {
                    Intent intent=new Intent(service.PAUSEORPLAY_ACTION);
                    service.sendBroadcast(intent);
                }
            }
        };
    }

    private void setNotification(int hour,int min){
        Intent clickIntent = new Intent(MusicApplication.getAppContext(), NotificationRecevier.class);
        PendingIntent Pi=PendingIntent.getBroadcast(MusicApplication.getAppContext(),0,clickIntent,0);
        notificationManager=(NotificationManager)MusicApplication.getAppContext().
                getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res=activity.getResources();
        if (bitmap!=null)
            bitmap.recycle();
        bitmap=BitmapFactory.decodeResource(res,R.mipmap.music_icon);
        Notification notification=new NotificationCompat.Builder(MusicApplication.getAppContext(),"channelId")
                .setSmallIcon(R.drawable.timer)
                .setLargeIcon(bitmap)
                .setContentTitle(String.format("将在%s:%s退出音乐",hour,min))
                .setContentIntent(Pi)
                .setAutoCancel(true)
                .setContentText("点击可取消定时关闭").build();
        notification.flags= Notification.FLAG_NO_CLEAR;
        notification.defaults=Notification.DEFAULT_VIBRATE;
        notificationManager.notify(1,notification);
    }

    private void startTimerTask(long time)
    {
        if (mTimer!=null)
        {
            if (myTimerTask!=null)
                myTimerTask.cancel();
        }
        myTimerTask=new MyTimerTask(TimerHandler);
        mTimer.schedule(myTimerTask,time);
    }
}
