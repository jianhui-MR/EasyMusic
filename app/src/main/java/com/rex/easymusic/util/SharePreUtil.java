package com.rex.easymusic.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.rex.easymusic.Application.MusicApplication;

/**
 * Created by Rex on 2019/2/28
 */
public class SharePreUtil {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharePreUtil(Context context,String sharePreName){
        sharedPreferences=context.getSharedPreferences(sharePreName,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public boolean putBoolean(String key,boolean value){
        try {
            editor.putBoolean(key,value);
            editor.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean putString(String key,String value){
        try {
            editor.putString(key,value);
            editor.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String getString(String key){
        String value=sharedPreferences.getString(key,"");
        return value;
    }

    public boolean putInt(String key,int value){
        try {
            editor.putInt(key,value);
            editor.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public int getInt(String key){
        int value=sharedPreferences.getInt(key,-1);
        return value;
    }


    public boolean putFloat(String key,float value){
        try {
            editor.putFloat(key,value);
            editor.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public float getFloat(String key){
        float value=sharedPreferences.getFloat(key,-1);
        return value;
    }


    public boolean putLong(String key,long value){
        try {
            editor.putLong(key,value);
            editor.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public long getLong(String key){
        long value=sharedPreferences.getLong(key,-1);
        return value;
    }

}
