package com.rex.easymusic.util;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.rex.easymusic.Enum.LoadStateEnum;
import com.rex.easymusic.fragment.BottomContainerFragment;
import com.rex.easymusic.service.PlayerService;
import com.rex.easymusic.util.ViewUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class ViewHandler extends Handler {
    private View success;
    private View Loading;
    private View LoadFail;
    private RecyclerView.Adapter adapter;
    private SmartRefreshLayout smartRefreshLayout;

    public ViewHandler(View success, View loading, View loadFail, RecyclerView.Adapter adapter) {
        this.success = success;
        this.Loading = loading;
        this.LoadFail = loadFail;
        this.adapter = adapter;
    }

    public ViewHandler(View success, View loading, View loadFail, RecyclerView.Adapter adapter, SmartRefreshLayout smartRefreshLayout) {
        this.success = success;
        this.Loading = loading;
        this.LoadFail = loadFail;
        this.adapter = adapter;
        this.smartRefreshLayout=smartRefreshLayout;
    }




    @Override
    public void handleMessage(Message msg) {
        Log.e("TAG", "handleMessage: "+msg.what );
        switch (msg.what){
            //加载成功
            case 1:
                ViewUtils.changeViewState(success, Loading, LoadFail, LoadStateEnum.LOAD_SUCCESS);
                adapter.notifyDataSetChanged();
                if (BottomContainerFragment.onlineMusicPlayListAdapter!=null)
                    BottomContainerFragment.onlineMusicPlayListAdapter.notifyDataSetChanged();
                if (smartRefreshLayout!=null)
                    smartRefreshLayout.finishLoadMore();
                break;

            //加载失败
            case 2:
                ViewUtils.changeViewState(success, Loading, LoadFail, LoadStateEnum.LOAD_FAIL);
                break;
        }
    }
}
