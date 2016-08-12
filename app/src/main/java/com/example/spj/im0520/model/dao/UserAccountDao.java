package com.example.spj.im0520.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.model.db.UserAccoutDB;

/**
 * Created by spj on 2016/8/2.
 */
public class UserAccountDao {
    private final UserAccoutDB mHelper;
    private UserInfo userinfo;

    public UserAccountDao(Context context) {
        mHelper = new UserAccoutDB(context);

    }
    //添加用户，用replace可以防止重复的姓名出现
    public void addAccount(UserInfo userInfo) {
        //获取数据库连接
        SQLiteDatabase sqdb = mHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(UserAccountTable.COL_NAME, userInfo.getName());
        value.put(UserAccountTable.COL_HXID, userInfo.getHxid());
        value.put(UserAccountTable.COL_NICK, userInfo.getNick());
        value.put(UserAccountTable.COL_PHOTO, userInfo.getPhoto());
        sqdb.replace(UserAccountTable.TAB_NAME, null, value);

    }
    //只获取一个人的信息
    public UserInfo getAccount(String name) {
        //获取数据库连接
        SQLiteDatabase sqdb = mHelper.getWritableDatabase();
        String sql = "select * from " + UserAccountTable.TAB_NAME + " where " + UserAccountTable.COL_NAME + " =?";
        Cursor cursor = sqdb.rawQuery(sql, new String[]{name});
      if(cursor.moveToNext()) {
           userinfo = new UserInfo();
          userinfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
          userinfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
          userinfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
          userinfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
      }
        cursor.close();

        return userinfo;
    }
    //只查找一个用户，用if
    public UserInfo getAccountByHxId(String hxId) {
        //获取数据库连接
        SQLiteDatabase sqdb = mHelper.getWritableDatabase();
        String sql = "select * from " + UserAccountTable.TAB_NAME + " where " + UserAccountTable.COL_HXID + " =?";
        Cursor cursor = sqdb.rawQuery(sql, new String[]{hxId});
        if(cursor.moveToNext()) {
            userinfo = new UserInfo();
            userinfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userinfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userinfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userinfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        cursor.close();

        return userinfo;

    }
}
