package com.example.spj.im0520;

import android.app.Application;
import android.content.Context;

import com.example.spj.im0520.model.Modle;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by spj on 2016/8/2.
 */
//想要用别人的东西，一定要先初始化才行，继承application会率先执行。
public class IMApplication extends Application{
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化EaseUI
        initEaseUI();
        //初始化模型层数据
        Modle.getInstance().init(this);
       mContext = this;
    }

    public static Context getmContext() {
        return mContext;
    }

    private void initEaseUI() {
        EMOptions options = new EMOptions();
        //不总是接受邀请信息
        options.setAcceptInvitationAlways(false);
        //不是自动接收群邀请信息
        options.setAutoAcceptGroupInvitation(false);
        //得到对象并初始化
        EaseUI.getInstance().init(this,options);
    }
}
