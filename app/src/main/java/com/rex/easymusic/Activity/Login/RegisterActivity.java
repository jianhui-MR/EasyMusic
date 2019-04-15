package com.rex.easymusic.Activity.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rex.easymusic.Activity.MainActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.Md5Util;
import com.rex.easymusic.util.SharePreUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;
import com.rex.easymusic.widget.VerificationCodeInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


/**
 * 注册界面
 * Created by Rex on 2019/2/22
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, VerificationCodeInput.Listener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.userAccount)
    public EditText userAccount;
    @BindView(R.id.accountPassword)
    public EditText userPassword;
    @BindView(R.id.email)
    public EditText accountEmail;
    @BindView(R.id.userName)
    public EditText userName;
    @BindView(R.id.next_step)
    public Button nextStep_button;
    @BindView(R.id.fill_in_user_information)
    public LinearLayout fillInUserInformation;
    @BindView(R.id.emailVerify)
    public RelativeLayout emailVerify;
    @BindView(R.id.tips_a)
    public TextView tips_a;
    @BindView(R.id.verificationCodeInput)
    public VerificationCodeInput verificationCodeInput;


    private int Tag=1;
    private final int fillInformation=1;
    private final int Email_verification=2;

    private final String getVerificationCodeUrl= ipAddressUtil.serviceIp+"/User/getVerificationCode";
    private final String registerUserUrl=ipAddressUtil.serviceIp+"/User/registerUser";

    private String verificationCode;
    private FormBody formBody;
    private Handler handler;
    private Intent intent;

    private Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        unbinder=ButterKnife.bind(this);
        setToolbar();
        bindView();
        initHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        ToastUtils.show(getString(R.string.network_abnormal));
                        break;
                    case 2:
                        ToastUtils.show(getString(R.string.registerSuccess));
                        intent=new Intent(RegisterActivity.this,MainActivity.class);
                        LoginActivity.userAccount=userAccount.getText().toString();
                        LoginActivity.userName=userName.getText().toString();
                        startActivity(intent);
                        SharePreUtil sharePreUtil = new SharePreUtil(RegisterActivity.this, "User");
                        sharePreUtil.putString("account",userAccount.getText().toString());
                        sharePreUtil.putString("password", String.valueOf(msg.obj));
                        finish();
                        break;
                }
            }
        };
    }


    private void bindView() {
        nextStep_button.setOnClickListener(this);
        verificationCodeInput.setOnCompleteListener(this);
    }


    private void setToolbar(){
        toolbar.setTitle(getString(R.string.register_newUser));
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 获取校验码，并对用户输入的信息做了验证
     * @return
     */
    private Boolean getVerificationCode(){
        if (userAccount.getText().toString().length()<6) {
            ToastUtils.show("用户账号不能低于6位");
            return false;
        }
        else if (userPassword.getText().toString().length()<6){
            ToastUtils.show("用户密码不能低于6位");
            return false;
        }
        else if (!(userName.getText().toString().length()>0)){
            ToastUtils.show("请输入用户昵称");
            return false;
        }
        else if (!checkEmailFormat(accountEmail.getText().toString())){
            ToastUtils.show("请输入正确的邮箱格式");
            return false;
        }


        formBody=new FormBody.Builder()
                .add("email",accountEmail.getText().toString())
                .build();
        HttpUtil.sendOkHttpRequest(getVerificationCodeUrl, formBody, new Callback() {
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
                    verificationCode=jsonObject.getString("verificationCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return true;
    }

    private void registerUser() {
        String md5pwd=Md5Util.md5Password(userPassword.getText().toString());
        formBody=new FormBody.Builder()
                .add("account",userAccount.getText().toString())
                .add("password",md5pwd)
                .add("name",userName.getText().toString())
                .add("email",accountEmail.getText().toString())
                .build();
        HttpUtil.sendOkHttpRequest(registerUserUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.obtainMessage(1).sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    if (jsonObject.getInt("status")==0){
                        handler.obtainMessage(2,md5pwd).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     *检查Email 格式（正则表达式）
     * @param content
     * @return
     */
    private boolean checkEmailFormat(String content){
        /*
         * " \w"：匹配字母、数字、下划线。等价于'[A-Za-z0-9_]'。
         * "|"  : 或的意思，就是二选一
         * "*" : 出现0次或者多次
         * "+" : 出现1次或者多次
         * "{n,m}" : 至少出现n个，最多出现m个
         * "$" : 以前面的字符结束
         */
        String REGEX="^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$";
        Pattern p = Pattern.compile(REGEX);
        Matcher matcher=p.matcher(content);

        return matcher.matches();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //返回键判断处理
            case android.R.id.home:
                if (Tag==fillInformation)
                    finish();
                else if (Tag==Email_verification){
                    emailVerify.setVisibility(View.GONE);
                    fillInUserInformation.setVisibility(View.VISIBLE);
                    Tag=fillInformation;
                    getSupportActionBar().setTitle(getString(R.string.register_newUser));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_step:
                if (getVerificationCode()){
                    Tag=Email_verification;
                    tips_a.setText("你的邮箱号:"+accountEmail.getText());
                    fillInUserInformation.setVisibility(View.GONE);
                    emailVerify.setVisibility(View.VISIBLE);
                    verificationCodeInput.requestFocus();
                    getSupportActionBar().setTitle("进行邮箱验证");
                }
                break;
        }
    }

    @Override
    public void onComplete(String code) {
        if (verificationCode.equals(code)){
            registerUser();
        }else {
            ToastUtils.show("验证码错误");
            verificationCodeInput.cleanText();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Tag==fillInformation)
            finish();
        else if (Tag==Email_verification){
            emailVerify.setVisibility(View.GONE);
            fillInUserInformation.setVisibility(View.VISIBLE);
            Tag=fillInformation;
            getSupportActionBar().setTitle(getString(R.string.register_newUser));
        }
    }
}
