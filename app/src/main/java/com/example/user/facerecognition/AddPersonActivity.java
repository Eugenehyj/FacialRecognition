package com.example.user.facerecognition;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.user.facerecognition.task.AddTask;
import com.example.user.facerecognition.task.MatchTask;
import com.example.user.facerecognition.util.OpenTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPersonActivity extends AppCompatActivity {

    private ImageButton ib_img;
    private EditText et_name;
    private EditText et_sex;
    private EditText et_phone;
    private Button btn_send;
    private Button btn_reset;
    public final int TYPE_TAKE_PHOTO = 1;//Uri获取类型判断
    public final int CODE_TAKE_PHOTO = 1;//相机RequestCode
    public final int CODE_SELECT_IMAGE = 2;//相册RequestCode
    private Uri photoUri;
    private byte[] datas;
    private Bitmap bitmap = null;
    public static ProgressDialog addDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        ib_img = (ImageButton) findViewById(R.id.ib_img);
        et_name = (EditText) findViewById(R.id.et_name);
        et_sex = (EditText) findViewById(R.id.et_sex);
        et_phone = (EditText) findViewById(R.id.et_phone);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInfo = et_name.getText()+","+et_sex.getText()+","+et_phone.getText();
                if(bitmap!=null){
                    if(!et_phone.getText().toString().equals("")&&et_phone.getText().toString()!=null){
                        addDialog = ProgressDialog.show(AddPersonActivity.this, "提示", "正在上传......");
                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendBitMap(bitmap,userInfo);
                    /*Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);*/
                        //getParent().finish();
                    }else
                        Toast.makeText(getApplicationContext(),"请输入手机",Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getApplicationContext(),"请上传图片",Toast.LENGTH_LONG).show();
            }
        });

        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_img.setImageBitmap(null);
                et_name.setText("");
                et_sex.setText("");
                et_phone.setText("");
            }
        });

        ib_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPersonActivity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
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
                            openCamera();
                        }else {
                            Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
                        }
                    }
                });
                builder.show();

            }
        });
    }

    private void openCamera(){
        OpenTool openTool = new OpenTool();
        if (Build.VERSION.SDK_INT >= 24) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = openTool.get24MediaFileUri(TYPE_TAKE_PHOTO,getApplicationContext());
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},CODE_TAKE_PHOTO);
                return;
            }
            startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = openTool.getMediaFileUri(TYPE_TAKE_PHOTO);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_SELECT_IMAGE){
            if (resultCode == RESULT_OK) {
                selectPic(data,requestCode);
            }
        }else if(requestCode==CODE_TAKE_PHOTO){
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("data")) {
                        Log.i("URI", "data is not null");
                        bitmap = data.getParcelableExtra("data");
                        ib_img.setImageBitmap(small(bitmap));//imageView即为当前页面需要展示照片的控件，可替换
                    }
                } else {
                    Log.i("URI", "Data is null");
                    if (Build.VERSION.SDK_INT >= 24){
                        bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        ib_img.setImageBitmap(small(bitmap));
                    }else {
                        bitmap = BitmapFactory.decodeFile(photoUri.getPath());
                        ib_img.setImageBitmap(small(bitmap));
                    }
                }
            }
        }
    }

    private Bitmap getFixed(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = 10;
        int newHeight = 10;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap1 = bitmap;
        Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
        return bitmap1;
    }

    //选择照片
    private void selectPic(Intent intent,int way) {
        Uri selectImageUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        bitmap = BitmapFactory.decodeFile(picturePath);
        ib_img.setImageBitmap(getFixed(bitmap));
    }

    private void sendBitMap(Bitmap bitmap,String userInfo){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        datas = baos.toByteArray();
        System.out.println("bytes.length= " + (datas.length / 1024) + "KB");
        new AddTask(datas,userInfo,et_phone.getText().toString(),AddPersonActivity.this).execute();
    }

    public void finishThis(){
        finish();
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.05f,0.05f);  //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
