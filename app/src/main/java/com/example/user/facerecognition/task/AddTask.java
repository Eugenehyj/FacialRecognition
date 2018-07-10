package com.example.user.facerecognition.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.facerecognition.AddPersonActivity;
import com.example.user.facerecognition.MainActivity;
import com.example.user.facerecognition.faceapi.AuthService;
import com.example.user.facerecognition.faceapi.FaceAction;
import com.google.gson.JsonParser;

/**
 * Created by User on 2018/7/6.
 */

public class AddTask extends AsyncTask<String,Void,String> {

    private byte[] datas;
    private String userInfo = "withInfo";
    private String user_id;
    private Context context;

    public AddTask(byte[] datas) {
        this.datas = datas;
    }

    public AddTask(byte[] datas, String userInfo, String user_id) {
        this.datas = datas;
        this.userInfo = userInfo;
        this.user_id = user_id;
    }

    public AddTask(byte[] datas, String userInfo, String user_id, Context context) {
        this.datas = datas;
        this.userInfo = userInfo;
        this.user_id = user_id;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        return FaceAction.add(datas,userInfo,user_id);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String isOk = new JsonParser().parse(result).getAsJsonObject().get("error_code").toString();
        if(isOk.equals("0")){
            Toast.makeText(context,"上传成功",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context,MainActivity.class);
            context.startActivity(intent);
        }else{
            String error_msg = new JsonParser().parse(result).getAsJsonObject().get("error_msg").toString();
            Toast.makeText(context,"错误代码："+isOk+" 错误信息："+error_msg,Toast.LENGTH_LONG).show();
            AddPersonActivity.addDialog.dismiss();
        }
    }
}
