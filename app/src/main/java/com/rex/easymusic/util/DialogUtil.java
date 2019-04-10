package com.rex.easymusic.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.rex.easymusic.Interface.DialogNegativeButtonListener;
import com.rex.easymusic.Interface.DialogPositionButtonListener;
import com.rex.easymusic.Interface.OnItemClickListener;
import com.rex.easymusic.R;

public class DialogUtil {
    private  AlertDialog.Builder alertDialog;
    private  DialogPositionButtonListener positionButtonListener;
    private  DialogNegativeButtonListener negativeButtonListener;
    private ProgressDialog progressDialog;

    public DialogUtil() {
    }

    public void setPositionButtonListener(DialogPositionButtonListener listener) {
        positionButtonListener = listener;
    }
    public void setNegativeButtonListener(DialogNegativeButtonListener listener){
        negativeButtonListener=listener;
    }

    public void showAlertDialog(Activity activity,String message){
        showAlertDialog(activity,null,message);
    }
    public void showAlertDialog(Activity activity,String message,boolean Cancelable){
        showAlertDialog(activity,null,message,Cancelable);
    }
    public void showAlertDialog(Activity activity,String title,String message){
        showAlertDialog(activity,title,message,false);
    }
    public void showAlertDialog(Activity activity, String title, String message,boolean Cancelable){
        alertDialog=new AlertDialog.Builder(activity);
        if (title!=null)
            alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(Cancelable);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (positionButtonListener!=null)
                    positionButtonListener.onPositionButtonClick();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (negativeButtonListener!=null)
                    negativeButtonListener.onNegativeButtonClick();
            }
        });
        alertDialog.show();
    }


    public void showProgressDialog(Activity activity,String message){
        showProgressDialog(activity,message,false);
    }
    public void showProgressDialog(Activity activity,String message,Boolean Cancelable){
        showProgressDialog(activity,null,message,Cancelable);
    }
    public void showProgressDialog(Activity activity,String title,String message){
        showProgressDialog(activity,title,message,false);
    }
    public void showProgressDialog(Activity activity,String title,String message,Boolean Cancelable){
        progressDialog=new ProgressDialog(activity);
        if (title!=null)
            progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(Cancelable);
        progressDialog.show();
    }

    public void closeProgressDialog(){
        if (progressDialog!=null)
            progressDialog.dismiss();
    }
}
