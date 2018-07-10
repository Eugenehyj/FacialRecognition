package com.example.user.facerecognition.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.user.facerecognition.adapters.TextViewBean;
import com.example.user.facerecognition.faceapi.FaceAction;
import com.example.user.facerecognition.fragments.RecognitionMainFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * Created by User on 2018/7/6.
 */

public class MatchTask extends AsyncTask<String,Void,String> {

    private byte[] datas;
    private Context context;
    private TextViewBean textViewBean;

    public MatchTask(byte[] datas, Context context, TextViewBean textViewBean) {
        this.datas = datas;
        this.context = context;
        this.textViewBean = textViewBean;
    }

    public MatchTask(byte[] datas) {
        this.datas = datas;
    }

    @Override
    protected String doInBackground(String... params) {
        return FaceAction.match(datas);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        JsonParser jp = new JsonParser();
        String isOk = jp.parse(result).getAsJsonObject().get("error_code").toString();
        if(isOk.equals("0")){
            //提取信息
            String r;
            r = jp.parse(result).getAsJsonObject().get("result").getAsJsonObject().get("user_list")
                    .getAsJsonArray().get(0).toString();
            System.out.println(r);
            String score = jp.parse(r).getAsJsonObject().get("score").toString();
            Float f = Float.parseFloat(score);
            if(f>=90){
                String[] results = jp.parse(r).getAsJsonObject().get("user_info").toString().replace("\"","").split(",");
                textViewBean.getTv_words().setText("这是你的朋友");
                if(results.length>1){
                    textViewBean.getTv_p_name().setText(results[0]);
                    textViewBean.getTv_p_sex().setText(results[1]);
                    textViewBean.getTv_p_phone().setText(results[2]);
                }
            }else if(f>=80){
                textViewBean.getTv_words().setText("这看起来好像你的朋友");
            }else{
                textViewBean.getTv_words().setText("并不是你朋友");
            }
            RecognitionMainFragment.dialog.dismiss();

        }else{
            String error_msg = new JsonParser().parse(result).getAsJsonObject().get("error_msg").toString();
            textViewBean.getTv_words().setText(error_msg);
            Toast.makeText(context,"错误代码："+isOk+" 错误信息："+error_msg,Toast.LENGTH_LONG).show();
            RecognitionMainFragment.dialog.dismiss();
        }
        System.out.println(result);
    }
}
