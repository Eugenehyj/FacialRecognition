package com.example.user.facerecognition.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.facerecognition.adapters.FriendsMainAdapter;
import com.example.user.facerecognition.beans.PersonalBean;
import com.example.user.facerecognition.faceapi.FaceAction;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.List;

/**
 * Created by User on 2018/7/6.
 */

public class GetUserID extends AsyncTask<String,Void,String> {

    private Context context;
    private List<PersonalBean> data;
    private FriendsMainAdapter adapter;

    public GetUserID(Context context, List<PersonalBean> data, FriendsMainAdapter adapter) {
        this.context = context;
        this.data = data;
        this.adapter = adapter;
    }

    public GetUserID(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        return FaceAction.getUsers();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String isOk = new JsonParser().parse(result).getAsJsonObject().get("error_code").toString();
        if(isOk.equals("0")){
            //提取信息
            JsonArray list;
            list = new JsonParser().parse(result).getAsJsonObject().get("result").getAsJsonObject().get("user_id_list")
                    .getAsJsonArray();
            //System.out.println(list.get(1));
            int len = list.size();
            //循环查询
            for(int i=0;i<len;i++){
                new GetUserTask(context,data,list.get(i).toString().replace("\"",""),adapter).execute();
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else{
            String error_msg = new JsonParser().parse(result).getAsJsonObject().get("error_msg").toString();
            Toast.makeText(context,"错误代码："+isOk+" 错误信息："+error_msg,Toast.LENGTH_LONG).show();
        }
        System.out.println(result);


    }
}
