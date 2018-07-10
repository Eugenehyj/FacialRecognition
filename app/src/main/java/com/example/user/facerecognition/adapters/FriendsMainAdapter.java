package com.example.user.facerecognition.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.facerecognition.R;
import com.example.user.facerecognition.beans.PersonalBean;

import java.util.List;

/**
 * Created by User on 2018/7/3.
 */

public class FriendsMainAdapter extends BaseAdapter {

    private List<PersonalBean> data;
    private Context context;

    public FriendsMainAdapter(List<PersonalBean> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend,parent,false);
            holder = new Holder();
            holder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.tv_name.setText(data.get(position).getName());
        holder.tv_sex.setText(data.get(position).getSex());
        holder.tv_phone.setText(data.get(position).getPhone()+"");

        return convertView;
    }

    class Holder{
        ImageView iv_photo;
        TextView tv_name;
        TextView tv_sex;
        TextView tv_phone;
    }
}
