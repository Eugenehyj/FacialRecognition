package com.example.user.facerecognition.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.facerecognition.adapters.FriendsMainAdapter;
import com.example.user.facerecognition.beans.PersonalBean;
import com.example.user.facerecognition.faceapi.FaceAction;
import com.example.user.facerecognition.util.ToChinese;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.List;

/**
 * Created by User on 2018/7/6.
 */

public class GetUserTask extends AsyncTask<String,Void,String> {

    private Context context;
    private List<PersonalBean> data;
    private String userId;
    private FriendsMainAdapter adapter;

    public GetUserTask(Context context, List<PersonalBean> data, String userId, FriendsMainAdapter adapter) {
        this.context = context;
        this.data = data;
        this.userId = userId;
        this.adapter = adapter;
    }

    public GetUserTask(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    protected String doInBackground(String... params) {
        return FaceAction.getUser(userId);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String isOk = new JsonParser().parse(result).getAsJsonObject().get("error_code").toString();
        if(isOk.equals("0")){
            result = new JsonParser().parse(result).getAsJsonObject().get("result").getAsJsonObject().get("user_list")
                    .getAsJsonArray().get(0).getAsJsonObject().get("user_info").toString().replace("\"","");
            String[] results = result.split(",");
            PersonalBean personalBean = new PersonalBean();
            if(results.length>1){
                personalBean.setName(results[0]);
                personalBean.setSex(results[1]);
                personalBean.setPhone(results[2]);
            }
            data.add(personalBean);
            adapter.notifyDataSetChanged();
        }else{
            String error_msg = new JsonParser().parse(result).getAsJsonObject().get("error_msg").toString();
            Toast.makeText(context,"错误代码："+isOk+" 错误信息："+error_msg,Toast.LENGTH_LONG).show();
        }

    }
}
