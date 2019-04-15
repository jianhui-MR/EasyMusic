package com.rex.easymusic.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rex.easymusic.util.TimerTask.TimerTakUtil;

public class NotificationRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TimerTakUtil.mTimer!=null)
        {
            TimerTakUtil.mTimer.cancel();
            TimerTakUtil.notificationManager.cancel(1);
            Toast.makeText(context.getApplicationContext(),"定时关闭已取消",Toast.LENGTH_SHORT).show();
        }

    }
}
