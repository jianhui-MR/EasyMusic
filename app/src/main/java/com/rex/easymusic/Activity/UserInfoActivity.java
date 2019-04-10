package com.rex.easymusic.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.R;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.StringAndBitmapUtil;
import com.rex.easymusic.util.TimeUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends ToolbarActivity implements View.OnClickListener {
    private PopupWindow selectImgPopUp;
    private  View popupView;
    private MultipartBody multipartBody;
    private Handler handler;
    private Bitmap headBitmap;
    private final int REQUEST_CODE_CAMERA=1;
    private final int REQUEST_CODE_ALBUM=2;
    private final String setHeadSculptureUrl= ipAddressUtil.serviceIp+"/User/setUserHeadSculpture";
    public  final String getHeadSculptureUrl= ipAddressUtil.serviceIp+"/User/getUserHeadSculpture?account=";

    @BindView(R.id.head_photo)
    public CircleImageView headImg;
    @BindView(R.id.tv_account)
    public TextView account;
    @BindView(R.id.tv_name)
    public TextView name;

    @Override
    public int setLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle("个人信息");
        initPopup();
        initHandler();
        initInfo();
    }

    /**
     * 初始化用户信息
     */
    private void initInfo() {
        Glide.with(this)
                .load(getHeadSculptureUrl+LoginActivity.userAccount)
                .dontAnimate()
                .error(R.mipmap.cat)
                .placeholder(R.mipmap.cat)
                .into(headImg);

        account.setText(LoginActivity.userAccount);
        name.setText(LoginActivity.userName);
    }

    /**
     * 初始化Popup
     */
    private void initPopup(){
        popupView=LayoutInflater.from(this).inflate(R.layout.popup_select_img,null,false);
        selectImgPopUp=new PopupWindow(popupView,RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT,true);
        selectImgPopUp.setOutsideTouchable(true);
        selectImgPopUp.setTouchable(true);
        selectImgPopUp.setAnimationStyle(R.style.showPopupAnimation);
        selectImgPopUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getWindow().getAttributes().alpha=1f;
                getWindow().setAttributes(getWindow().getAttributes());
            }
        });
        popupView.findViewById(R.id.camera).setOnClickListener(this);
        popupView.findViewById(R.id.album).setOnClickListener(this);
        popupView.findViewById(R.id.cancel).setOnClickListener(this);

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
                        ToastUtils.show("更改头像成功");
                        headImg.setImageBitmap(headBitmap);
                        break;
                    case 3:
                        ToastUtils.show("更改头像失败");
                        break;
                }
            }
        };
    }

    private View getParentView(){
        return LayoutInflater.from(this).inflate(R.layout.activity_user_info,null,false);
    }

    /**
     * 更改头像
     */
    @OnClick(R.id.head_photo)
    public void modifyHeadImg(){
        selectImgPopUp.showAtLocation(getParentView(),Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0,0);
        getWindow().getAttributes().alpha=0.6f;
        getWindow().setAttributes(getWindow().getAttributes());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera:
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
                startActivityForResult(openCameraIntent, REQUEST_CODE_CAMERA);
                selectImgPopUp.dismiss();
                break;
            case R.id.album:
                Intent openAlbumIntent = new Intent(Intent.ACTION_PICK); //系统常量， 启动相机的关键
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, REQUEST_CODE_ALBUM);
                selectImgPopUp.dismiss();
                break;
            case R.id.cancel:
                selectImgPopUp.dismiss();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //相机
            case REQUEST_CODE_CAMERA:
                if ( resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    updateUserHeadPhoto(bitmap);
                }
                break;

            //相册
            case REQUEST_CODE_ALBUM:
                if ( resultCode == RESULT_OK) {
//                   data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        updateUserHeadPhoto(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Log.e("TAG", "onActivityResult: "+data.getData() );

                }
                break;
        }
    }

    /**
     * 上传头像到后端服务器中
     * @param bitmap
     */
    private void updateUserHeadPhoto(Bitmap bitmap) {
        headBitmap=bitmap;
        File file=new File(String.format(Environment.getExternalStorageDirectory()+"/EasyMusic/headSculpture/%s.jpg",LoginActivity.userAccount));
        try{
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if (!file.exists()){
                    file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        RequestBody body= RequestBody.create(MediaType.parse("multipart/form-data"),file);
        String fileName=file.getName();
        multipartBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("account", LoginActivity.userAccount)
                .addFormDataPart("headSculpture",fileName,body)
                .build();
        HttpUtil.sendOkHttpRequest(setHeadSculptureUrl, multipartBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                Log.e("TAG", "onResponse: "+responseBody);
                try {
                    if (new JSONObject(responseBody).getInt("status")==0){
                        handler.sendEmptyMessage(2);
                    }else {
                        handler.sendEmptyMessage(3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
