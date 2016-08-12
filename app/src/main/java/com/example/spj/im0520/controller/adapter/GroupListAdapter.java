package com.example.spj.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.spj.im0520.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/8.
 */
public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EMGroup> mGroups = new ArrayList<>();
    private View convertView;

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    public void refresh(List<EMGroup> groups) {
        //校验
        if (groups != null && groups.size() >= 0) {
            mGroups.clear();
            mGroups.addAll(groups);
        }
        //通知刷新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_group_list, null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_group_list_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //获取当前item的数据
        EMGroup emGroup = mGroups.get(position);
        //数据显示
        viewHolder.tvName.setText(emGroup.getGroupName());
        return convertView;
    }
    static class ViewHolder{
        TextView tvName;
    }
}
