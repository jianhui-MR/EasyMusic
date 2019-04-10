package com.rex.easymusic.Activity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ScanMusicUtil;
import com.rex.easymusic.util.SharePreUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


/**
 * 闪屏Activity
 */
public class Splash_screenActivity extends ServiceActivity {
    private String loginUrl= ipAddressUtil.serviceIp+"/User/loginUser";
    private LottieAnimationView animationView;
    private SharePreUtil sharePreUtil;
    private String account;
    private String password;
    private FormBody formBody;
    private Handler handler;
    private Intent intent;
    private String TAG="闪屏";
    private String[] permissions={Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestRuntimePermissions();
        animationView=findViewById(R.id.splash_view);
        animationView.playAnimation();
        initHandler();
        login();
    }

    private void requestRuntimePermissions(){
        // 当手机系统大于 23 时，才有必要去判断权限是否获取
        List<String> permissionList=new ArrayList<>();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            for (String permission:permissions){
                if (!(ContextCompat.checkSelfPermission(this,permission)==PackageManager.PERMISSION_GRANTED)){
                    permissionList.add(permission);
                }
            }
            if (!permissionList.isEmpty()){
                ActivityCompat.requestPermissions(this,
                        permissionList.toArray(new String[permissionList.size()]),1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults.length>0){
                for (int i=0;i<grantResults.length;i++){
                    if (grantResults[i]==PackageManager.PERMISSION_GRANTED) {
                        if (permissions[i]==Manifest.permission.READ_EXTERNAL_STORAGE){
                            ScanMusicUtil scanMusicUtil =new ScanMusicUtil();
                            scanMusicUtil.query(this);
                        }
                        //权限通过
                    }else {
                        //权限请求没通过
                    }
                }
            }
        }
    }

    /**
     * 初始化handler
     */
    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.e("TAG", "handleMessage: "+msg.what );
                switch (msg.what){
                    //网络异常
                    case 1:
                        ToastUtils.show("网络异常");
                        intent=new Intent(Splash_screenActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    //登陆成功
                    case 2:
                        setAnimationListener(new AnimationCallback() {
                            @Override
                            public void onAnimationEnd() {
                                intent=new Intent(Splash_screenActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        break;
                    //登陆失败
                    case 3:
                        setAnimationListener(new AnimationCallback() {
                            @Override
                            public void onAnimationEnd() {
                                intent=new Intent(Splash_screenActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        break;

                }
            }
        };
    }


    /**
     * 自动登录
     */
    private void login(){
        sharePreUtil=new SharePreUtil(this,"User");
        account=sharePreUtil.getString("account");
        password=sharePreUtil.getString("password");

        formBody=new FormBody.Builder()
                .add("account",account)
                .add("password",password)
                .build();

        HttpUtil.sendOkHttpRequest(loginUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.obtainMessage(1).sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                Log.e("TAG", "onResponse: "+responseBody );
                try {
                    JSONObject jsonObject=new JSONObject(responseBody);
                    if (jsonObject.getInt("status")==0){
                        LoginActivity.userName=jsonObject.getString("userName");
                        LoginActivity.userAccount=jsonObject.getString("userAccount");
                        handler.obtainMessage(2).sendToTarget();
                    }
                    else
                        handler.obtainMessage(3).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 动画Callback
     */
    private interface AnimationCallback{
        void  onAnimationEnd();
    }

    private void setAnimationListener(AnimationCallback callback){
        Log.e(TAG, "setAnimationListener: "+"设置动画结束舰艇" );
        AnimationCallback mCallback;
        mCallback=callback;
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onAnimationEnd();
                Log.e(TAG, "setAnimationListener: "+"回调接口" );
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {//接口回调

            }
        });
    }

}
