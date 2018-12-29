package com.bobby.musiczone.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bobby.musiczone.util.TimerTask.TimerTakUtil;

public class NotificationRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TimerTakUtil.mTimer!=null)
        {
            TimerTakUtil.mTimer.cancel();
            Toast.makeText(context.getApplicationContext(),"定时关闭已取消",Toast.LENGTH_SHORT).show();
        }

    }
}
