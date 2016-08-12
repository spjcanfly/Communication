package com.example.spj.im0520.controller.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.controller.activity.LoginActivity;
import com.example.spj.im0520.model.Modle;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by spj on 2016/8/3.
 */
//设置界面
public class SettingFragment extends Fragment {

    private Button btn_setting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        btn_setting = (Button) view.findViewById(R.id.btn_setting);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        //获取当前登录的用户
        String user = EMClient.getInstance().getCurrentUser();
        btn_setting.setText("退出登录 (" + user + ")");
        //设置监听
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

            }
        });

    }

    private void logout() {
        Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //弹出对话框
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        ad.setTitle("确认退出？");
                        ad.setMessage("再次提示亲，真的舍得退出吗？");
                        ad.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //去环信服务器退出登录
                                EMClient.getInstance().logout(false, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                startActivity(intent);
                                                //关闭这个页面
                                                getActivity().finish();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(int i, final String s) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "退出失败" + s, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });


                            }
                        });
                        ad.setNegativeButton("NO", null);
                        ad.show();
                    }
                });

            }


        });
    }

}


