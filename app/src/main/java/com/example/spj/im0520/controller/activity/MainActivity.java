package com.example.spj.im0520.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.example.spj.im0520.R;
import com.example.spj.im0520.controller.fragment.ChatFragment;
import com.example.spj.im0520.controller.fragment.ContactListFragment;
import com.example.spj.im0520.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();

    }

//初始化监听
    private void initListener() {
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_main_chat:
                        fragment = chatFragment;

                        break;
                    case R.id.rb_main_contact:
                        fragment = contactListFragment;

                        break;
                    case R.id.rb_main_setting:
                        fragment = settingFragment;

                        break;
                }
                swithFragment(fragment);
            }
        });
        //默认选择一个Fragment
       rg_main.check(R.id.rb_main_chat);

    }
    //管理这三个Fragment

    private void swithFragment(Fragment fragment) {

        FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
        transation.replace(R.id.fl_main,fragment);
        transation.commit();
    }

    //初始化数据
    private void initData() {
         chatFragment = new ChatFragment();
         contactListFragment = new ContactListFragment();
         settingFragment = new SettingFragment();

    }

    //初始化布局
    private void initView() {

        rg_main = (RadioGroup) findViewById(R.id.rg_main);

    }
}
