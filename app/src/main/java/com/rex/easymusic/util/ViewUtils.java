package com.rex.easymusic.util;

import android.view.View;

import com.rex.easymusic.Enum.LoadStateEnum;

/**
 * 视图工具类
 * Created by Rex on 2019/1/14.
 */
public class ViewUtils {
    public static void changeViewState(View success, View loading, View fail, LoadStateEnum state) {
        if (success==null||loading==null||fail==null)
            return;
        try {
            switch (state) {
                case LOADING:
                    success.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    fail.setVisibility(View.GONE);
                    break;
                case LOAD_SUCCESS:
                    success.setVisibility(View.VISIBLE);
                    fail.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    break;
                case LOAD_FAIL:
                    success.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    fail.setVisibility(View.VISIBLE);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
