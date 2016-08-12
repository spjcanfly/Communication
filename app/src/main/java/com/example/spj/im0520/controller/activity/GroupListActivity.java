package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.controller.adapter.GroupListAdapter;
import com.example.spj.im0520.model.Modle;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

//群组列表页面
public class GroupListActivity extends Activity {

    private ListView lv_group_list;
    private View headerView;
    private GroupListAdapter mGroupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();

    }

    private void initListener() {
        //listView 条目的点击事件
        lv_group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position <= 0) {
                    return;
                }
                //跳转到会话页面
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);

                //设置群id
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position-1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());
                //设置聊天的类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                startActivity(intent);
            }
        });
        //头布局的点击事件
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到新建群组的页面
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        //创建适配器
        mGroupListAdapter = new GroupListAdapter(GroupListActivity.this);
        //添加适配器到Listview
        lv_group_list.setAdapter(mGroupListAdapter);
        //刷新数据
//        getGroupFromServer();
    }

    private void getGroupFromServer() {
        //联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //到服务器获取群的信息
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    //刷新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGroupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
                            //提示
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //刷新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGroupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
                            //提示
                            Toast.makeText(GroupListActivity.this, "加载群信息失败："+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        lv_group_list = (ListView) findViewById(R.id.lv_group_list);
        //添加Listview的头布局
        headerView = View.inflate(this, R.layout.header_group_list, null);
        lv_group_list.addHeaderView(headerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新数据
        getGroupFromServer();
    }
}
