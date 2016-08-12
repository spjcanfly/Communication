package com.example.spj.im0520.model;

import android.content.Context;
import android.util.Log;

import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.model.dao.UserAccountDao;
import com.example.spj.im0520.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by spj on 2016/8/2.
 */
//模型层
public class Modle {
    private Context mContext;
    private UserAccountDao userAccountDao;
    private DBManager mDbManager ;
    //私有化构造
    private Modle() {

    }

    private static Modle instance = new Modle();

    public static Modle getInstance() {
        return instance;
    }

    public void init(Context context){
        mContext = context;
         userAccountDao = new UserAccountDao(context);
         EventListener evenListener = new EventListener(mContext);
    }
    //获取用户帐号的操作类
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public ExecutorService getGlobalThreadPool() {
        return executorService;
    }

    public void loginSuccess(UserInfo userInfo){

        if(userInfo == null) {
            Log.e("TAG","进入userInfo");
            return;
        }
        if(mDbManager != null) {
            Log.e("TAG","进入mdb");
            mDbManager.close();
        }
        Log.e("TAG","创建对象");
        //获取数据库的管理者
        mDbManager = new DBManager(mContext,userInfo.getName());

    }
    //获取数据库的管理者
    public DBManager getDbManager(){

        return mDbManager;
    }

}
