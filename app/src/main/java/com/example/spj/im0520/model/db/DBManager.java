package com.example.spj.im0520.model.db;

import android.content.Context;

import com.example.spj.im0520.model.dao.ContactDao;
import com.example.spj.im0520.model.dao.InvitationDao;

/**
 * Created by spj on 2016/8/5.
 */
public class DBManager {

    private final DBHelper mDbhelper;
    private final ContactDao contactDao;
    private final InvitationDao invitationDao;

    public DBManager(Context context,String name) {
         mDbhelper = new DBHelper(context,name);
        contactDao = new ContactDao(mDbhelper);
        invitationDao = new InvitationDao(mDbhelper);
    }
    // 获取联系人操作类
    public ContactDao getContactDao(){
       return  contactDao;
    }
    //获取邀请信息的操作类
    public InvitationDao getInvitationDao(){
        return invitationDao;
    }

    public void close(){
        mDbhelper.close();
    }

}
