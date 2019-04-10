package com.rex.easymusic.util;

import android.content.Context;
import android.content.Intent;

import com.rex.easymusic.service.PlayerService;

import static com.rex.easymusic.fragment.BottomContainerFragment.loadFinishAction;

public class BoradCastUtil {
    public static void sendLoadOnlineMusicFinishBroadCast(Context context){
        Intent intent=new Intent(loadFinishAction);
        context.sendBroadcast(intent);
    }
}
