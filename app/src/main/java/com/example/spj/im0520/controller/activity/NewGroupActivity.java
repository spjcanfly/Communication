package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.model.Modle;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

public class NewGroupActivity extends Activity {

    private EditText et_new_group_name;
    private EditText et_new_group_desc;
    private CheckBox cb_new_group_public;
    private CheckBox cb_new_group_invite;
    private Button bt_new_group_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        initView();

        initListener();
    }

    private void initListener() {
        //按钮的点击事件
        bt_new_group_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到联系人的页面
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);

                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            creatGroup(data.getExtras().getStringArray("members"));
        }
    }

    private void creatGroup(final String[] members) {
        //获取群名称
        final String group_name = et_new_group_name.getText().toString();
        //获取群描述
        final String group_desc = et_new_group_desc.getText().toString();
        //联网 去环信服务器创建群
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMGroupManager.EMGroupOptions emGroupOptions = new EMGroupManager.EMGroupOptions();
                emGroupOptions.maxUsers = 200;
                EMGroupManager.EMGroupStyle groupStyle = null;
                if(cb_new_group_public.isChecked()) {
                    if(cb_new_group_invite.isChecked()) {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;

                    }else{
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;

                    }

                }else {
                    if(cb_new_group_invite.isChecked()) {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else{
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }

                }
                emGroupOptions.style = groupStyle;
                // 群名称  群描述  群成员  群创建原因 群参数设置
                try {
                    EMClient.getInstance().groupManager().createGroup(group_name,group_desc,members,"创建群",emGroupOptions);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();

                            finish();
                        }

                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void initView() {
        et_new_group_name = (EditText) findViewById(R.id.et_new_group_name);
        et_new_group_desc = (EditText) findViewById(R.id.et_new_group_desc);
        cb_new_group_public = (CheckBox) findViewById(R.id.cb_new_group_public);
        cb_new_group_invite = (CheckBox) findViewById(R.id.cb_new_group_invite);
        bt_new_group_create = (Button) findViewById(R.id.bt_new_group_create);
    }
}
