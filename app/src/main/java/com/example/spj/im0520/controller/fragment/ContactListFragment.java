package com.example.spj.im0520.controller.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.spj.im0520.IMApplication;
import com.example.spj.im0520.R;
import com.example.spj.im0520.controller.activity.AddContactActivity;
import com.example.spj.im0520.controller.activity.ChatActivity;
import com.example.spj.im0520.controller.activity.GroupListActivity;
import com.example.spj.im0520.controller.activity.InviteActivity;
import com.example.spj.im0520.model.Modle;
import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.utils.Constant;
import com.example.spj.im0520.utils.SpUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by spj on 2016/8/3.
 */
//联系人页面
public class ContactListFragment extends EaseContactListFragment {

    private ImageView iv__head_invite_contact;
    private LinearLayout ll_head_contact_invite;
    private LocalBroadcastManager mLBM;

    //邀请信息变化的广播
    private BroadcastReceiver InviteChangedRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 联系人邀请信息的变化  || 群邀请信息的变化
            if (intent.getAction() == Constant.CONTACT_INVITE_CHANGED || intent.getAction() == Constant.GROUP_INVITE_CHANGED) {
                //显示红点
                iv__head_invite_contact.setVisibility(View.VISIBLE);
            }
        }
    };
    private BroadcastReceiver ContactChangedRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constant.CONTACT_CHANGED) {
                //刷新联系人页面
                refreshContact();
            }
        }
    };
    private String mHxid;
    private LinearLayout ll_head_contact_group;

    //处理简单的逻辑，findViewById这些的。

    @Override
    protected void initView() {
        super.initView();
        //把这个布局添加到这个布局的头上
        View view = View.inflate(getActivity(), R.layout.fragment_head_contact, null);
        listView.addHeaderView(view);
        titleBar.setRightImageResource(R.drawable.em_add);
        //点击加号后的操作
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });

        //红点处理
        iv__head_invite_contact = (ImageView) view.findViewById(R.id.iv_head_invite_contact);
        boolean isNewInvite = SpUtil.getInstance(IMApplication.getmContext()).getBoolean(SpUtil.IS_NEW_INVITE, false);

        //把红点是否显示
        iv__head_invite_contact.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //邀请信息条目的点击的事件
        ll_head_contact_invite = (LinearLayout) view.findViewById(R.id.ll_head_contact_invite);
        ll_head_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏红点
                iv__head_invite_contact.setVisibility(View.GONE);
                //修改sp的存储内容
                SpUtil.getInstance(IMApplication.getmContext()).save(SpUtil.IS_NEW_INVITE, false);
                //跳转到邀请列表的页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);

            }
        });

        //listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if(user == null) {
                    return;
                }
                //跳转到会话的界面
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                startActivity(intent);
            }
        });

        //群组条目的点击事件
        ll_head_contact_group = (LinearLayout) view.findViewById(R.id.ll_head_contact_group);
        ll_head_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到群组页面
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);
            }
        });
    }

    //处理比较复杂的逻辑
    @Override
    protected void setUpView() {
        super.setUpView();
        //注册绑定的listVIew
        registerForContextMenu(listView);
        // 从环信服务器获取联系人信息
        getContactsFromHxServer();
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        // 联系人邀请信息变化
        mLBM.registerReceiver(InviteChangedRecevier, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        // 联系变化
        mLBM.registerReceiver(ContactChangedRecevier, new IntentFilter(Constant.CONTACT_CHANGED));
        // 群邀请信息的变化
        mLBM.registerReceiver(InviteChangedRecevier,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //因为ContextMenuInfo是一个接口，它的实现类AdapterContextMenuInfo里面有position属性
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        //因为房数据的时候就是easeUser类型的
        EaseUser user = (EaseUser) listView.getItemAtPosition(position);
        if(user == null) {
            return;
        }
        mHxid = user.getUsername();
        //获取布局
        getActivity().getMenuInflater().inflate(R.menu.delete_contact, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //当前选择的是删除
        if (item.getItemId() == R.id.delete_contact) {
            //弹出对话框
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("删除");
            adb.setMessage("确认要删除" + mHxid + "?");
            adb.setPositiveButton("非常确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteContact();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除这个bitch成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            adb.setNegativeButton("后悔了", null);
            adb.show();

            //销毁此次事件
            return true;

        }
        return super.onContextItemSelected(item);
    }

    //删除联系人
    private void deleteContact() {
        //联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //服务器删除
                try {
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    //本地数据库
                    Modle.getInstance().getDbManager().getContactDao().deleteContactByHxId(mHxid);
                    Modle.getInstance().getDbManager().getInvitationDao().removeInvitation(mHxid);
                    //刷新列表
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshContact();
                        }
                    });


                } catch (HyphenateException e) {
                    e.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }

    private void getContactsFromHxServer() {
        //联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                try {//从服务器获得数据
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (hxids != null && hxids.size() >= 0) {
                        //本地数据
                        List<UserInfo> contacts = new ArrayList<>();
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }
                        Modle.getInstance().getDbManager().getContactDao().saveContacts(contacts, true);
                        
                        if(getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //页面刷新
                                refreshContact();
                                //获取联系人成功
                                Toast.makeText(getActivity(), "获取联系人成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取联系人失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void refreshContact() {
        //从本地数据库获取数据
        List<UserInfo> contacts = Modle.getInstance().getDbManager().getContactDao().getContacts();
        if (contacts != null && contacts.size() >= 0) {
            //设置数据
            Map<String, EaseUser> contactMap = new HashMap<>();
            for (UserInfo contact : contacts) {

                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactMap.put(contact.getHxid(), easeUser);

            }

            setContactsMap(contactMap);
            //通知刷新
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解广播注册
        mLBM.unregisterReceiver(ContactChangedRecevier);
        mLBM.unregisterReceiver(InviteChangedRecevier);
    }
}
