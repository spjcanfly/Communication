package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.model.Modle;
import com.example.spj.im0520.model.bean.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class LoginActivity extends Activity {

    private EditText et_login_user;
    private EditText et_login_pwd;
    private Button btn_login_register;
    private Button btn_login_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        initListener();

    }

    private void initListener() {
        //注册按钮的点击事件
        btn_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();

            }
        });
        //登录按钮的点击事件
        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    private void register() {
        final String user = et_login_user.getText().toString();
        final String pwd = et_login_pwd.getText().toString();

        if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;//注册不了下面不执行
        }

        //去环信服务器注册,进度对话框
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("正在注册中");
        pd.show();
        //访问网络
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(user, pwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }

                    });
                    pd.cancel();

                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.cancel();
                        }
                    });
                }
            }
        });


    }
    private void login(){
        final String user = et_login_user.getText().toString();
        final String pwd = et_login_pwd.getText().toString();

        if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;//登录不了下面不执行
        }
        //去环信服务器注册
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("正在登录中");
        pd.show();
        //访问网络
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器登录
                EMClient.getInstance().login(user, pwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //关闭进度条
                        pd.cancel();
                        //保存用户名到数据库
                        Modle.getInstance().getUserAccountDao().addAccount(new UserInfo(user));
                        //登录成功后初始化处理

                        Modle.getInstance().loginSuccess(new UserInfo(user));
                        //跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                       startActivity(intent);
                        //结束当前页面
                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.cancel();
                            }
                        });

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });


    }
    private void initView() {
        et_login_user = (EditText) findViewById(R.id.et_login_user);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        btn_login_register = (Button) findViewById(R.id.btn_login_register);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);
    }
}
