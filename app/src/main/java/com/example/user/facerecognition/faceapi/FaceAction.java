package com.example.user.facerecognition.faceapi;



import android.os.Environment;

import com.example.user.facerecognition.beans.ResultBean;
import com.example.user.facerecognition.util.Base64Util;
import com.example.user.facerecognition.util.FileUtil;
import com.example.user.facerecognition.util.GsonUtils;
import com.example.user.facerecognition.util.HttpUtil;
import com.example.user.facerecognition.util.TokenUtil;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人脸工具类
 */

public class FaceAction {

    public static String add(byte[] imgData,String userInfo,String user_id) {
        System.out.println(user_id);
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            String imgStr = Base64Util.encode(imgData);
            System.out.println(imgStr);
            //String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            Map<String, Object> map = new HashMap<>();
            map.put("image", imgStr);
            map.put("group_id", "localgroup_1");
            map.put("user_id", user_id);
            map.put("user_info", userInfo);
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = TokenUtil.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String match(byte[] image1) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            String imgStr = Base64Util.encode(image1);
            Map<String, Object> map = new HashMap<>();
            map.put("image", imgStr);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "localgroup_1");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = TokenUtil.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUser(String userId) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/get";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", userId);
            map.put("group_id", "localgroup_1");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = TokenUtil.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUsers() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", "localgroup_1");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = TokenUtil.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String deleteUser(String userId) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", "localgroup_1");
            map.put("user_id",userId);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = TokenUtil.accessToken;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
