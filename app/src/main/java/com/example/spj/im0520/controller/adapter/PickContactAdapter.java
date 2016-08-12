package com.example.spj.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.spj.im0520.R;
import com.example.spj.im0520.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/8.
 */
public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPickContacts = new ArrayList<>();
    private List<String> mExistingMembers = new ArrayList<>();

    public PickContactAdapter(Context context, List<PickContactInfo> pickContacts,List<String> existingMembers) {
        mContext = context;
        if (pickContacts != null && pickContacts.size() >= 0) {
            mPickContacts.clear();
            mPickContacts.addAll(pickContacts);

        }
        //获取群中已经存在的成员集合
        if(existingMembers != null && existingMembers.size()>0) {
            mExistingMembers.clear();
            mExistingMembers.addAll(existingMembers);
        }
    }

    //获取选中的联系人
    public List<String> getAddMembers(){
            //准备一个返回数据的集合
        List<String> members = new ArrayList<>();
        for(PickContactInfo contact : mPickContacts){
              //查找已经选中的联系人
            if(contact.isChecked()) {
                members.add(contact.getUserInfo().getName());
            }
        }
        return members;
    }

    @Override
    public int getCount() {
        return mPickContacts == null ? 0 : mPickContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPickContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_pick_contact,null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_pick_contact_name);
            viewHolder.isChecked= (CheckBox) convertView.findViewById(R.id.cb_pick_contact);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //2.获得当前的item数据
        PickContactInfo pickContactInfo = mPickContacts.get(position);
        //3.设置显示数据
        viewHolder.tvName.setText(pickContactInfo.getUserInfo().getName());
        viewHolder.isChecked.setChecked(pickContactInfo.isChecked());

        //获取选中的联系人
        if(mExistingMembers.contains(pickContactInfo.getUserInfo().getHxid()) ) {
            //更改显示
            viewHolder.isChecked.setChecked(true);
            //更改数据
            pickContactInfo.setIsChecked(true);
        }

        return convertView;
    }

    static class ViewHolder{
        TextView tvName;
        CheckBox isChecked;
    }
}
