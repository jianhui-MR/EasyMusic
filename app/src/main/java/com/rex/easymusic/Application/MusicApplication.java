package com.rex.easymusic.Application;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.ScanMusicUtil;

import org.litepal.LitePalApplication;

public class MusicApplication extends LitePalApplication {

    private static Context mcontext;

    public static String TAG="MusicApplication";

    private PlayerService playerService;

    private String[] permissions;

    @Override
    public void onCreate() {
        super.onCreate();
        mcontext=getApplicationContext();
    }

    public static Context getAppContext(){
        return mcontext;
    }


    public PlayerService getPlayerService() {
        return playerService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    private void exitApplication(){
        System.exit(0);
    }

}
