package com.rex.easymusic.Activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.service.PlayerService;

/**
 * 开启服务，绑定服务Activity
 */
public class ServiceActivity extends AppCompatActivity implements ServiceConnection{
    private PlayerService service;
    private final String TAG="ServiceActivity";
    public static final String BindSuccess="BindService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,PlayerService.class));
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
        ((MusicApplication)getApplication()).setPlayerService(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service=null;
    }
}
