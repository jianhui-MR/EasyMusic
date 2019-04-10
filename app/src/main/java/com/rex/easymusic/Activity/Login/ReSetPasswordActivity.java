package com.rex.easymusic.Activity.Login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.rex.easymusic.Activity.ToolbarActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.Md5Util;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * 重设密码界面
 * Created by Rex on 2019/3/5
 */
public class ReSetPasswordActivity extends ToolbarActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.et_userPassword)
    public EditText et_userPassword;
    @BindView(R.id.et_userPasswordAgain)
    public EditText et_userPasswordAgain;

    private ProgressDialog dialog;
    private static final String modifyPwdUrl= ipAddressUtil.serviceIp+"/User/modifyPassword";
    private FormBody formBody;
    private String userAccount;
    private Handler handler;


    @Override
    public int setLayoutId() {
        return R.layout.activity_re_set_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initHandler();
        getBundle();
    }

    private void initToolbar() {
        toolbar.setTitle("重新设置密码");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what){
                    case 1:
                        ToastUtils.show(getString(R.string.network_abnormal));
                        break;
                    case 2:
                        ToastUtils.show(getString(R.string.modifyPwdSuccess));
                        finish();
                        break;
                    case 3:
                        ToastUtils.show("密码修改失败");
                }
            }
        };
    }


    private void getBundle(){
        userAccount=getIntent().getStringExtra("account");
    }


    private void showProgressDialog(){
        dialog=new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在加载");
        dialog.show();
    }

    private void modifyPassword(String pwd){
        showProgressDialog();
        formBody=new FormBody.Builder()
                .add("account",userAccount)
                .add("password", Md5Util.md5Password(pwd))
                .build();
        HttpUtil.sendOkHttpRequest(modifyPwdUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    if (jsonObject.getString("status").equals("0"))
                        handler.sendEmptyMessage(2);
                    else
                        handler.sendEmptyMessage(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.bt_confirm)
    public void clickConfirm(){
        if (!(et_userPassword.getText().toString().length()<6&&et_userPasswordAgain.getText().length()<6)){
            if (et_userPassword.getText().toString().equals(et_userPasswordAgain.getText().toString())){
                modifyPassword(et_userPassword.getText().toString());
            }else
                ToastUtils.show("两次输入的密码不一致");
        }else {
            ToastUtils.show("密码不能少于6位数");
        }
    }
}
