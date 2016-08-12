package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.spj.im0520.R;
import com.example.spj.im0520.model.Modle;
import com.example.spj.im0520.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends Activity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            toMainOrLogin();
        }
    };

    //去主页面还是登录页面
    private void toMainOrLogin() {
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore()) {
                    //获得用户数据
                    UserInfo account = Modle.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());

                    if(account == null) {
                        //去登录页面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        //登录成功之后的初始化处理
                        Modle.getInstance().loginSuccess(account);
                        //去主页面
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }


                } else {
                    //去登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
                //结束当前界面
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.sendMessageDelayed(Message.obtain(), 2000);

    }

}
