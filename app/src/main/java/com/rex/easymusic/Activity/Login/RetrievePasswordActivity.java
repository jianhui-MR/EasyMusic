package com.rex.easymusic.Activity.Login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.rex.easymusic.Activity.ToolbarActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.DialogUtil;
import com.rex.easymusic.util.HttpUtil;
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
 * 找回密码界面
 * Created by Rex on 2019/3/5
 */
public class RetrievePasswordActivity extends ToolbarActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.userAccount)
    public EditText userAccount;
    @BindView(R.id.next_step)
    public Button next;

    public final String retrievePasswordUrl= ipAddressUtil.serviceIp+"/User/retrievePassword";
    private FormBody formBody;
    private Handler handler;
    private Intent intent;
    private String userEmail;
    private String verificationCode;
    private DialogUtil dialogUtil;

    @Override
    public int setLayoutId() {
        return R.layout.activity_retrieve_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogUtil=new DialogUtil();
        initHandler();
        initToolbar();
    }

    @Override
    protected void initToolbar() {
        toolbar.setTitle("找回密码");
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
                dialogUtil.closeProgressDialog();
                switch (msg.what){
                    case 1:
                        ToastUtils.show(getString(R.string.network_abnormal));
                        break;
                    case 2:
                        intent=new Intent(RetrievePasswordActivity.this,verificationCodeActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("email",userEmail);
                        bundle.putString("verificationCode",verificationCode);
                        bundle.putString("account",userAccount.getText().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        break;
                    case 3:
                        ToastUtils.show("输入的账号不存在");
                        break;
                }
            }
        };
    }

    /**
     * 验证信息
     */
    private void RetrievePassword() {
        showProgressDialog();
        formBody=new FormBody.Builder()
                .add("account",userAccount.getText().toString())
                .build();
        HttpUtil.sendOkHttpRequest(retrievePasswordUrl, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //网络异常
                handler.obtainMessage(1).sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                Log.e("TAG", "onResponse: "+responseBody );
                try {
                    JSONObject jsonObject=new JSONObject(responseBody);
                    if (jsonObject.getInt("status")==0){
                        userEmail=jsonObject.getString("userEmail");
                        verificationCode=jsonObject.getString("verificationCode");
                        //账号存在，校验码验证
                        handler.obtainMessage(2).sendToTarget();
                    }else {
                        //账号不存在
                        handler.obtainMessage(3).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showProgressDialog(){
        dialogUtil.showProgressDialog(this,"提示","正在加载...");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @OnClick(R.id.next_step)
    public void next(){
        RetrievePassword();
    }

}
