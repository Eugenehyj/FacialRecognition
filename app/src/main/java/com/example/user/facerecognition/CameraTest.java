package com.example.user.facerecognition;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraTest extends AppCompatActivity {

    private Button btn_test;
    private String mFilePath;
    private ImageView iv_test;
    public final int TYPE_TAKE_PHOTO = 1;//Uri获取类型判断
    public final int CODE_TAKE_PHOTO = 1;//相机RequestCode
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        btn_test = (Button) findViewById(R.id.btn_test);
        iv_test = (ImageView) findViewById(R.id.iv_test);

        mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();// 获取SD卡路径
        mFilePath = mFilePath + "/" + "temp.jpg";// 指定路径
        System.out.println("路径"+mFilePath);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= 24) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA);
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoUri = get24MediaFileUri(TYPE_TAKE_PHOTO);
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA},CODE_TAKE_PHOTO);
                    return;
                }
                startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
            } else {
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoUri = getMediaFileUri(TYPE_TAKE_PHOTO);
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
            }
            }
        });
    }

    public Uri getMediaFileUri(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "相册名字");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    /**
     * 版本24以上
     */
    public Uri get24MediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "face_temp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return FileProvider.getUriForFile(this, getPackageName()+".fileprovider", mediaFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra("data")) {
                            Log.i("URI", "data is not null");
                            Bitmap bitmap = data.getParcelableExtra("data");
                            iv_test.setImageBitmap(bitmap);//imageView即为当前页面需要展示照片的控件，可替换
                        }
                    } else {
                        Log.i("URI", "Data is null");
                        if (Build.VERSION.SDK_INT >= 24){
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            iv_test.setImageBitmap(bitmap);
                        }else {
                            Bitmap bitmap = BitmapFactory.decodeFile(photoUri.getPath());
                            iv_test.setImageBitmap(bitmap);
                        }
                    }
                }
                break;
        }
    }

}
