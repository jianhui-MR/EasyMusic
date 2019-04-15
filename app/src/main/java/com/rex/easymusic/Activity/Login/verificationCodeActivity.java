package com.rex.easymusic.Activity.Login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.rex.easymusic.Activity.ToolbarActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.widget.VerificationCodeInput;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 校验码验证界面
 * Created by Rex on 2019/3/5
 */
public class verificationCodeActivity extends ToolbarActivity {
    @BindView(R.id.verificationCodeInput)
    public VerificationCodeInput verificationCodeInput;
    @BindView(R.id.tips_a)
    public TextView tips_a;

    private String userEmail;
    private String verificationCode;
    private String account;
    private Intent intent;
    private Context context;

    @Override
    public int setLayoutId() {
        return R.layout.activity_verification_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        CodeInputComplete();
        getBundle();
        setToolbar();
    }

    private void setToolbar(){
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle("邮箱验证");
    }

    private void getBundle() {
        userEmail=getIntent().getExtras().getString("email");
        verificationCode=getIntent().getExtras().getString("verificationCode");
        account=getIntent().getExtras().getString("account");
        tips_a.setText(String.format("你的邮箱号:%s",userEmail));
    }

    private void CodeInputComplete() {
        verificationCodeInput.setFocusable(true);
        verificationCodeInput.setOnCompleteListener(content -> {
            if (content.equals(verificationCode)){
                intent=new Intent(context,ReSetPasswordActivity.class);
                intent.putExtra("account",account);
                startActivity(intent);
                finish();
            }else {
                ToastUtils.show("验证码错误");
                verificationCodeInput.cleanText();
            }
        });
    }
}
