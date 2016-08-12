package com.example.spj.im0520.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.spj.im0520.model.bean.GroupInfo;
import com.example.spj.im0520.model.bean.InvitationInfo;
import com.example.spj.im0520.model.bean.UserInfo;
import com.example.spj.im0520.utils.Constant;
import com.example.spj.im0520.utils.SpUtil;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

/**
 * Created by spj on 2016/8/3.
 */
public class EventListener {


    private Context mcontext;
    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mcontext = context;
        //获取本地广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mcontext);

        //监听联系的变化
        EMClient.getInstance().contactManager().setContactListener(ContactChangedListener);

        //监听群的变化
        EMClient.getInstance().groupManager().addGroupChangeListener(GroupChangedListener);
    }

    private final EMGroupChangeListener GroupChangedListener = new EMGroupChangeListener() {
        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //保存邀请信息到数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            //群
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, inviter));
            //原因
            invitationInfo.setReason(reason);
            //状态
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            //添加邀请信息到本地数据库
            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);
            //设置红点显示
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            //发送群信息变化广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }


        //收到 群申请通知
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applicant, String reason) {

            //添加邀请信息到本地数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroupInfo(new GroupInfo(groupName,groupId,applicant));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);
            //设置红点显示
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            //发送群信息变化广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            //保存信息到本地数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, accepter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);
            //显示红点
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            //发送群信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被拒绝
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {

            //保存数据到数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroupInfo(new GroupInfo(groupName,groupId,decliner));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);
            //显示红点
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {

            // 保存邀请信息到本地数据库
            InvitationInfo invitation = new InvitationInfo();
            invitation.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);// 邀请被同意了

            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitation);

            // 设置红点显示
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE, true);

            // 发送群信息变化广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {

            // 保存邀请信息到本地数据库
            InvitationInfo invitation = new InvitationInfo();
            invitation.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);// 邀请被拒绝

            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitation);

            // 设置红点显示
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE, true);

            // 发送群信息变化广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        //收到 群成员被删除
        @Override
        public void onUserRemoved(String s, String s1) {

        }
        //收到 群被解散
        @Override
        public void onGroupDestroy(String s, String s1) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

            // 保存邀请信息到本地数据库
            InvitationInfo invitation = new InvitationInfo();
            invitation.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
            invitation.setReason(inviteMessage);
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);// 自动接受邀请

            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitation);

            // 设置红点显示
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE, true);

            // 发送群信息变化广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };

    EMContactListener ContactChangedListener = new EMContactListener() {
        // 添加联系人的方法
        @Override
        public void onContactAdded(String hxid) {
            // 添加联系人到本地数据库
             Modle.getInstance().getDbManager().getContactDao().saveContact(new UserInfo(hxid),true);


            // 发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //联系人删除的方法
        @Override
        public void onContactDeleted(String hxid) {
           //从本地数据库中删除联系人
            Modle.getInstance().getDbManager().getContactDao().deleteContactByHxId(hxid);
            Modle.getInstance().getDbManager().getInvitationDao().removeInvitation(hxid);
            // 发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //联系人的邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            // 添加邀请信息到本地数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUserInfo(new UserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);
            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);
            //存储状态为有红点
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //联系人同意邀请
        @Override
        public void onContactAgreed(String hxid) {
            // 添加邀请信息到本地数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUserInfo(new UserInfo(hxid));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Modle.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);
            //存储状态为有红点
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //联系人拒绝了邀请
        @Override
        public void onContactRefused(String hxid) {
            //存储状态为有红点
            SpUtil.getInstance(mcontext).save(SpUtil.IS_NEW_INVITE,true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

}
