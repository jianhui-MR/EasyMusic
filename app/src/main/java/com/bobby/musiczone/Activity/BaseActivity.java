package com.bobby.musiczone.Activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bobby.musiczone.service.PlayerService;

public class BaseActivity extends AppCompatActivity implements ServiceConnection{
    private PlayerService service;
    private final String TAG="BaseActivity";
    public static final String BindSuccess="BindService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this,PlayerService.class),
                this,Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    public PlayerService getService()
    {
        return service;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        PlayerService.MyBinder myBinder=(PlayerService.MyBinder) binder;
        service=myBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service=null;
    }
}
