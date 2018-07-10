package com.example.user.facerecognition.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.facerecognition.R;
import com.example.user.facerecognition.adapters.FriendsMainAdapter;
import com.example.user.facerecognition.beans.PersonalBean;
import com.example.user.facerecognition.task.DeleteTask;
import com.example.user.facerecognition.task.GetUserID;
import com.example.user.facerecognition.task.GetUserTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2018/7/3.
 */

public class FriendsMainFragment extends Fragment {

    private ListView lv_friends;
    public static List<PersonalBean> data;
    public static FriendsMainAdapter adapter;
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_main,container,false);

        lv_friends = (ListView) view.findViewById(R.id.lv_friends);

        data = new ArrayList<PersonalBean>();
        adapter = new FriendsMainAdapter(data,getContext());
        lv_friends.setAdapter(adapter);

        lv_friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                v = lv_friends.getChildAt(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                //    指定下拉列表的显示数据
                final String[] way = {"删除"};
                //    设置一个下拉的列表选择项
                builder.setItems(way, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0){
                            TextView textView = (TextView) v.findViewById(R.id.tv_phone);
                            String userId = textView.getText().toString();
                            new DeleteTask(userId,getContext()).execute();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        return view;
    }

    public void refresh() {
        onCreate(null);
    }

    public void initFragment(){
        new GetUserID(getContext(),data,adapter).execute();
    }
}
