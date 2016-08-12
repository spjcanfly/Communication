package com.example.spj.im0520.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.model.db.DBHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by spj on 2016/8/3.
 */
public class ContactDao {
    private DBHelper mhelper;

    public ContactDao(DBHelper helper) {
        mhelper = helper;
    }

    // 获取所有联系人
    public List<UserInfo> getContacts() {
        SQLiteDatabase db = mhelper.getWritableDatabase();
        String sql = "select * from " + ContactTable.TABLE_NAME + " where " +
                ContactTable.COL_IS_CONTACT + "=1";//被邀请的

        Cursor cursor = db.rawQuery(sql, null);
        List<UserInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
            list.add(userInfo);
        }
        //关闭资源
        cursor.close();

        return list;
    }

    // 通过环信id获取用户联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxIds) {
        //校验
        if (hxIds == null && hxIds.size() <= 0) {
            return null;
        }

        SQLiteDatabase db = mhelper.getWritableDatabase();

        List<UserInfo> list = new ArrayList<>();
        for (String hxid : hxIds) {
            String sql = "select * from " + ContactTable.TABLE_NAME + " where " +
                    ContactTable.COL_HXID + "=?";
            Cursor cursor = db.rawQuery(sql, new String[]{hxid});
            if (cursor.moveToNext()) {
                UserInfo userInfo = new UserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
                userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
                list.add(userInfo);
            }

            cursor.close();
        }
        return list;
    }

    // 通过环信id获取联系人单个信息
    public UserInfo getContactByHx(String hxId) {// ctrl+alt+l 格式化代码

        if (hxId == null) {
            return null;
        }
        SQLiteDatabase db = mhelper.getWritableDatabase();
        String sql = "select * from " + ContactTable.TABLE_NAME + " where " + ContactTable.COL_HXID
                + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        UserInfo userInfo = null;
        if (cursor.moveToNext()) {
            userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }

        cursor.close();
        return userInfo;
    }

    // 保存单个联系人
    public void saveContact(UserInfo user, boolean isMyContact) {
        // 校验
        if (user == null) {
            return;
        }
        //  获取数据库链接
        SQLiteDatabase db = mhelper.getReadableDatabase();
        ContentValues value = new ContentValues();
        value.put(ContactTable.COL_HXID,user.getHxid());
        value.put(ContactTable.COL_NAME,user.getName());
        value.put(ContactTable.COL_NICK,user.getNick());
        value.put(ContactTable.COL_IS_CONTACT,isMyContact?1:0);
        db.replace(ContactTable.TABLE_NAME,null,value);
    }

    // 保存联系人信息
    public void saveContacts(Collection<UserInfo> contacts, boolean isMyContact) {
        // 校验
        if (contacts == null || contacts.size() <= 0) {
            return;
        }
        for(UserInfo contact:contacts){
            saveContact(contact,isMyContact);
        }
    }

    // 删除联系人信息
    public void deleteContactByHxId(String hxId) {
        if (hxId == null) {
            return;
        }
         SQLiteDatabase db = mhelper.getWritableDatabase();
        db.delete(ContactTable.TABLE_NAME,ContactTable.COL_HXID+"=?",new String[]{hxId});
    }
}
