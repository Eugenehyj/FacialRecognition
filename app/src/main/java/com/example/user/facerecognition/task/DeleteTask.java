package com.example.user.facerecognition.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.facerecognition.MainActivity;
import com.example.user.facerecognition.faceapi.FaceAction;
import com.example.user.facerecognition.fragments.FriendsMainFragment;
import com.google.gson.JsonParser;

/**
 * Created by User on 2018/7/6.
 */

public class DeleteTask extends AsyncTask<String,Void,String> {

    private String userId;
    private Context context;

    public DeleteTask(String userId, Context context) {
        this.userId = userId;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        return FaceAction.deleteUser(userId);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String isOk = new JsonParser().parse(result).getAsJsonObject().get("error_code").toString();
        if(isOk.equals("0")){
            FriendsMainFragment.data.clear();
            new GetUserID(context,FriendsMainFragment.data,FriendsMainFragment.adapter).execute();
            Toast.makeText(context,"删除成功",Toast.LENGTH_LONG).show();
        }else{
            String error_msg = new JsonParser().parse(result).getAsJsonObject().get("error_msg").toString();
            Toast.makeText(context,"错误代码："+isOk+" 错误信息："+error_msg,Toast.LENGTH_LONG).show();
        }
    }
}
