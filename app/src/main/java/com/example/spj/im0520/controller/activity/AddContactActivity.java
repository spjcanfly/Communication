package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.model.Modle;
import com.example.spj.im0520.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddContactActivity extends Activity {

    private Button btn_add_contact;
    private TextView tv_add_contact_find;
    private EditText et_add_contact;
    private TextView tv_add_contact_name;
    private LinearLayout ll_add_contact;
    private UserInfo userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        initView();

        initListener();


    }

    private void initListener() {
        //查找按钮的监听
        tv_add_contact_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFind();
            }
        });

        //接受按钮的监听
        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAdd();


            }
        });
    }

    private void buttonAdd() {
        //联网
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userinfo.getHxid(),"添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            ll_add_contact.setVisibility(View.GONE);
                            et_add_contact.setText("");
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送好友邀请失败"+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void buttonFind() {
        //获得输入的内容
        String name = et_add_contact.getText().toString();
        //校验
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(AddContactActivity.this, "输入的内容不能为空", Toast.LENGTH_SHORT).show();
          return;
        }
        //判断是否有此人
        userinfo = new UserInfo(name);
        //获取此人的姓名
        ll_add_contact.setVisibility(View.VISIBLE);
        tv_add_contact_name.setText(userinfo.getName());
    }


    private void initView() {
        tv_add_contact_find = (TextView) findViewById(R.id.tv_add_contact_find);
        et_add_contact = (EditText) findViewById(R.id.et_add_contact);
        ll_add_contact = (LinearLayout) findViewById(R.id.ll_add_contact);
        tv_add_contact_name = (TextView) findViewById(R.id.tv_add_contact_name);
        btn_add_contact = (Button) findViewById(R.id.btn_add_contact);
        //这个刚开始是不显示的
        ll_add_contact.setVisibility(View.GONE);

    }
}
