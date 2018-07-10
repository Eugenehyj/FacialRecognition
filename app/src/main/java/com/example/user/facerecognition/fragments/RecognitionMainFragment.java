package com.example.user.facerecognition.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.user.facerecognition.AddPersonActivity;
import com.example.user.facerecognition.R;
import com.example.user.facerecognition.adapters.TextViewBean;
import com.example.user.facerecognition.task.AddTask;
import com.example.user.facerecognition.task.MatchTask;
import com.example.user.facerecognition.util.OpenTool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by User on 2018/7/3.
 */

public class RecognitionMainFragment extends Fragment {

    private Button btn_newOne;
    private Button btn_recognition;
    private TextViewBean holder;
    //private ImageView iv_p;
    public final int TYPE_TAKE_PHOTO = 1;//Uri获取类型判断
    public final int CODE_MATCH_PHOTO = 3;
    public final int CODE_TAKE_PHOTO = 1;//相机RequestCode
    public final int CODE_SELECT_IMAGE = 2;//相册RequestCode
    private Uri photoUri;
    private byte[] datas;
    public static ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recognition_main,container,false);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CODE_SELECT_IMAGE);
        requestPermissions(new String[]{Manifest.permission.CAMERA},TYPE_TAKE_PHOTO);
        //iv_p = (ImageView) view.findViewById(imageView);
        holder = new TextViewBean();
        holder.setTv_words((TextView) view.findViewById(R.id.tv_words));
        holder.setTv_p_name((TextView) view.findViewById(R.id.tv_p_name));
        holder.setTv_p_sex((TextView) view.findViewById(R.id.tv_p_sex));
        holder.setTv_p_phone((TextView) view.findViewById(R.id.tv_p_phone));
        btn_newOne = (Button) view.findViewById(R.id.btn_newOne);
        btn_newOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPersonActivity.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });
        btn_recognition = (Button) view.findViewById(R.id.btn_recognition);
        btn_recognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                builder.setTitle("选择获得图片的方式");
                //    指定下拉列表的显示数据
                final String[] way = {"拍摄", "相册"};
                //    设置一个下拉的列表选择项
                builder.setItems(way, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0){
                            openCamera(CODE_MATCH_PHOTO);
                        }else {
                            Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
                        }
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    private void openCamera(int way){
        OpenTool openTool = new OpenTool();
        if (Build.VERSION.SDK_INT >= 24) {
            //int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA);
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = openTool.get24MediaFileUri(TYPE_TAKE_PHOTO,getContext());
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            /*if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){

                return;
            }*/
            startActivityForResult(takeIntent, way);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = openTool.getMediaFileUri(TYPE_TAKE_PHOTO);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takeIntent, way);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_SELECT_IMAGE){
            if (resultCode == RESULT_OK) {
                dialog=ProgressDialog.show(getActivity(), "提示", "正在上传......");
                selectPic(data,requestCode);
            }
        }else if(requestCode==CODE_MATCH_PHOTO||requestCode==CODE_TAKE_PHOTO){
            if (resultCode == RESULT_OK) {
                dialog=ProgressDialog.show(getActivity(), "提示", "正在上传......");
                if (data != null) {
                    if (data.hasExtra("data")) {
                        Log.i("URI", "data is not null");
                        Bitmap bitmap = data.getParcelableExtra("data");
                        sendBitMap(bitmap,requestCode);
                        //iv_p.setImageBitmap(bitmap);//imageView即为当前页面需要展示照片的控件，可替换
                    }
                } else {
                    Log.i("URI", "Data is null");
                    if (Build.VERSION.SDK_INT >= 24){
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(photoUri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //iv_p.setImageBitmap(bitmap);
                        sendBitMap(bitmap,requestCode);
                    }else {
                        Bitmap bitmap = BitmapFactory.decodeFile(photoUri.getPath());
                        //iv_p.setImageBitmap(bitmap);
                        sendBitMap(bitmap,requestCode);
                    }
                }
            }
        }
    }

    //选择照片
    private void selectPic(Intent intent,int way) {
        Uri selectImageUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        //iv_p.setImageBitmap(bitmap);
        sendBitMap(bitmap,way);
    }

    private void sendBitMap(Bitmap bitmap,int way){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        datas = baos.toByteArray();
        System.out.println("bytes.length= " + (datas.length / 1024) + "KB");
        new MatchTask(datas,getContext(),holder).execute();
    }

}
