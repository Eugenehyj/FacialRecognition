package com.example.user.facerecognition;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.user.facerecognition.adapters.FRFragmentPagerAdapter;
import com.example.user.facerecognition.fragments.FriendsMainFragment;
import com.example.user.facerecognition.fragments.RecognitionMainFragment;
import com.example.user.facerecognition.task.GetUserID;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager vp;
    private RadioGroup radioGroup;
    FRFragmentPagerAdapter adapter;
    private int jk = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = (ViewPager) findViewById(R.id.vp);
        adapter = new FRFragmentPagerAdapter(getSupportFragmentManager(),getFragments());
        vp.setAdapter(adapter);
        radioGroup = (RadioGroup) findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_recognition:
                        vp.setCurrentItem(0);
                        break;
                    case R.id.rb_friends:
                        if(jk==0){
                            new GetUserID(getApplicationContext(),FriendsMainFragment.data,FriendsMainFragment.adapter).execute();
                            jk++;
                        }
                        vp.setCurrentItem(1);
                        break;
                }
            }
        });
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        radioGroup.check(R.id.rb_recognition);
                        break;
                    case 1:
                        if(jk==0){
                            new GetUserID(getApplicationContext(),FriendsMainFragment.data,FriendsMainFragment.adapter).execute();
                            jk++;
                        }
                        radioGroup.check(R.id.rb_friends);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.getItem(0).onActivityResult(requestCode, resultCode, data);
    }

    private List<Fragment> getFragments(){
        RecognitionMainFragment rmf = new RecognitionMainFragment();
        FriendsMainFragment fmf = new FriendsMainFragment();
        List<Fragment> data = new ArrayList<Fragment>();
        data.add(rmf);data.add(fmf);
        return data;
    }
}
