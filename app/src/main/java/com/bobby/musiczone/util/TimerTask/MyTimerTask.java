package com.bobby.musiczone.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    private Handler TimerHandler;
    public MyTimerTask(Handler handler)
    {
        TimerHandler=handler;
    }
    @Override
    public void run() {
        Message msg = TimerHandler.obtainMessage();
        msg.what=0x001;
        msg.sendToTarget();
    }
}
