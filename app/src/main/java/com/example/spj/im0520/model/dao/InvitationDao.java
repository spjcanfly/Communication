package com.example.spj.im0520.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.spj.im0520.model.bean.GroupInfo;
import com.example.spj.im0520.model.bean.InvitationInfo;
import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/5.
 */
public class InvitationDao {
    private DBHelper mDbHelper;

    public InvitationDao(DBHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    // 添加邀请
    public void addInvitation(InvitationInfo invitationInfo) {
        //获取数据库连接
        SQLiteDatabase sqdb = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InvitationTable.COL_REASON, invitationInfo.getReason());
        //把枚举类型转换
        values.put(InvitationTable.COL_STATUS, invitationInfo.getStatus().ordinal());
        UserInfo user = invitationInfo.getUserInfo();
        if (user != null) {//个人邀请
            values.put(InvitationTable.COL_USER_NAME, invitationInfo.getUserInfo().getName());
            values.put(InvitationTable.COL_USER_HXID, invitationInfo.getUserInfo().getHxid());

        } else {//群
            values.put(InvitationTable.COL_GROUP_NAME, invitationInfo.getGroupInfo().getGroupName());
            values.put(InvitationTable.COL_GROUP_ID, invitationInfo.getGroupInfo().getGroupId());
            values.put(InvitationTable.COL_USER_HXID, invitationInfo.getGroupInfo().getInvitePerson());
        }

        sqdb.replace(InvitationTable.TAB_NAME, null, values);
    }

    // 获取所有邀请信息
    public List<InvitationInfo> getInvitations() {
        //获取数据库连接
        SQLiteDatabase sqdb = mDbHelper.getWritableDatabase();
        List<InvitationInfo> invitations = new ArrayList<>();
        String sql = "select * from " + InvitationTable.TAB_NAME;
        Cursor cursor = sqdb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            InvitationInfo invitationInfo = new InvitationInfo();
            //原因和状态
            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_REASON)));
            invitationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InvitationTable.COL_STATUS))));
            String groupId = cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_ID));
            if (groupId == null) {
                //个人
                UserInfo userInfo = new UserInfo();
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_NAME)));
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_HXID)));
                invitationInfo.setUserInfo(userInfo);

            } else {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_ID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_NAME)));
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_HXID)));
                invitationInfo.setGroupInfo(groupInfo);
            }

            invitations.add(invitationInfo);
        }
        cursor.close();
        return invitations;

    }

    // 将int类型状态转换为邀请的状态
    private InvitationInfo.InvitationStatus int2InviteStatus(int intStatus) {
        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;

        }
        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUPO_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUPO_ACCEPT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }
        return null;

    }
    // 删除邀请
    public void removeInvitation(String hxId) {
        if(hxId == null) {
            return;
        }
        SQLiteDatabase sqdb = mDbHelper.getWritableDatabase();
        sqdb.delete(InvitationTable.TAB_NAME,InvitationTable.COL_USER_HXID +"=?",new String[]{hxId});
    }
    // 更新邀请状态
    public void updateInvitationStatus(InvitationInfo.InvitationStatus invitationStatus, String hxId) {

        if (hxId == null) {
            return;
        }
        SQLiteDatabase sqdb = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InvitationTable.COL_STATUS,invitationStatus.ordinal());
        sqdb.update(InvitationTable.TAB_NAME,values,InvitationTable.COL_USER_HXID+"=?",new String[]{hxId});

    }

}
