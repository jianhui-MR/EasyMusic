package com.rex.easymusic.Activity.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rex.easymusic.Activity.MainActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.Md5Util;
import com.rex.easymusic.util.SharePreUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * 登陆界面
 * Created by Rex on 2019/2/22
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.accountName)
    public EditText account;
    @BindView(R.id.accountPassword)
    public EditText password;
    @BindView(R.id.login)
    public Button loginButton;
    @BindView(R.id.register_newUser)
    public Button registerButton;
    @BindView(R.id.forget_password)
    public TextView forgetPassword;

    private Handler handler;
    public static String userName;
    public static String userAccount;
    private SharePreUtil sharePreUtil;
    private Unbinder unbinder;
    private Intent intent;
    private String loginUrl = ipAddressUtil.serviceIp + "/User/loginUser";


    /*----------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder=ButterKnife.bind(this);
        bindView();
        initHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /*-----------------------------------------------------------------------*/

    /**
     * 控件绑定
     */
    private void bindView(){
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        sharePreUtil=new SharePreUtil(LoginActivity.this,"User");
        account.setText(sharePreUtil.getString("account"));
        account.setSelection(sharePreUtil.getString("account").length());
    }

    /**
     * 初始化handler
     */
    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    //网络异常
                    case 1:
                        ToastUtils.show("网络异常");
                        break;
                    //登陆成功
                    case 2:
                        ToastUtils.show("登陆成功");
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    //登陆失败
                    case 3:
                        ToastUtils.show("用户名或密码错误");
                        break;

                }
            }
        };
    }

    /**
     * 登陆请求
     */
    public void login(){
        String md5Psw=Md5Util.md5Password(password.getText().toString());
        FormBody formBody=new FormBody.Builder()
                .add("account",account.getText().toString())
                .add("password",md5Psw)
                .build();
        HttpUtil.sendOkHttpRequest(loginUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(responseBody);
                    if (jsonObject.getInt("status")==0){
                        userName=jsonObject.getString("userName");
                        userAccount=jsonObject.getString("userAccount");

                        sharePreUtil=new SharePreUtil(LoginActivity.this,"User");
                        sharePreUtil.putString("account",account.getText().toString());
                        sharePreUtil.putString("password",md5Psw);

                        handler.sendEmptyMessage(2);
                    }
                    else
                        handler.sendEmptyMessage(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                login();
                break;
            case R.id.register_newUser:
                intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forget_password:
                Intent intent=new Intent(LoginActivity.this,RetrievePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
