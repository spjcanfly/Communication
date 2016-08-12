package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.controller.adapter.GroupDetailAdapter;
import com.example.spj.im0520.model.Modle;
import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

//群详情页面
public class GroupDetailActivity extends Activity {

    private GridView gv_group_detail;
    private Button bt_group_detail;
    private EMGroup mGroup;
    private LocalBroadcastManager mLBM;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener(){

        @Override
        public void onAddMembers() {

            //跳到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);

            //传递群id
            intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());

            startActivityForResult(intent,2);


        }

        @Override
        public void onDeleteMember(final UserInfo userInfo) {

            //联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //去环信服务器注册
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(),userInfo.getHxid());
                        //刷新
                        getMembersFromServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            //获取返回的好友
            final String[] memberses = data.getExtras().getStringArray("members");
            //联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //去环信服务器添加好友邀请
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(),memberses);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送好友邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送好友邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private List<UserInfo> mUsers;
    private GroupDetailAdapter mGroupDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();

        initData();

        initListener();

    }

    private void initListener() {
        //gridview的触摸事件
        gv_group_detail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        //如果当前是删除模式，切换成正常模式
                        if (mGroupDetailAdapter.ismIsDeleteModel()) {

                            //切换回到正常模式
                            mGroupDetailAdapter.setmIsDeleteModel(false);
                            //通知刷新
                            mGroupDetailAdapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                }
                return false;

            }
        });
    }

    private void initData() {
        //获取传递过来的数据
        String groupId = getIntent().getExtras().getString(Constant.GROUP_ID);

        if(groupId == null) {
            //结束当前的页面
            finish();
            return;
        }else {
            //获取到该群的所有信息
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);

        }
           //button中群成员和群主不同显示
        initButtonDisplay();

        //初始化gridview
        initGridView();
    }

    private void initGridView() {
        //创建适配器
        //如果当前用户是群主，或者是公开的 ，就可以添加和删除群成员
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        mGroupDetailAdapter = new GroupDetailAdapter(this,isCanModify,mOnGroupDetailListener);

        //将适配器添加到gridview
        gv_group_detail.setAdapter(mGroupDetailAdapter);

        //刷新页面
        getMembersFromServer();
    }

    private void getMembersFromServer() {
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup groupFromServer = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    List<String> members = groupFromServer.getMembers();
                    if(members !=null && members.size()>=0) {
                        mUsers = new ArrayList<>();
                        for (String member : members){
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGroupDetailAdapter.refresh(mUsers);
                            Toast.makeText(GroupDetailActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initButtonDisplay() {

        //根据当前用户是否是群主
        if(EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {
            bt_group_detail.setText("解散群");
            //解散群按钮的点击事件
            bt_group_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //去服务器解散
                    Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //从环信服务器中解散该群
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                //发送解散群的广播
                                sendExitGroupBroadCastRecevier();
                                
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //结束当前页面
                                        finish();
                                        //提示
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //结束当前页面
                                        finish();
                                        //提示
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败："+e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else{
            bt_group_detail.setText("退群");
            bt_group_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());
                                //发送退群广播
                                sendExitGroupBroadCastRecevier();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //结束当前页面
                                        finish();
                                        
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //结束当前页面
                                        finish();

                                        Toast.makeText(GroupDetailActivity.this, "退群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
    //发送退群广播
    private void sendExitGroupBroadCastRecevier() {
        //获取广播的管理者
        mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);
        //传递群id
        intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());

        //发送退群广播
        mLBM.sendBroadcast(intent);

    }




    private void initView() {
        gv_group_detail = (GridView) findViewById(R.id.gv_group_detail);
        bt_group_detail = (Button) findViewById(R.id.bt_group_detail);
    }
}
