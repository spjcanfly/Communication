package com.example.spj.im0520.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.spj.im0520.R;
import com.example.spj.im0520.model.bean.GroupInfo;
import com.example.spj.im0520.model.bean.InvitationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/5.
 */
/*
* 任何数据 的变化，都要从以下四个方面考虑
1: 网络数据是否变化
2：数据库、、文件、内存中数据是否变化
3：页面是否需要更新
4：是否需要提示
*/
public class InviteAdapter extends BaseAdapter {
    private Context mcontext;
    List<InvitationInfo> mInvitation = new ArrayList<>();
    private OnInviteListener mOnInviteListener;

    public InviteAdapter(Context context, OnInviteListener onInviteListener) {
        mcontext = context;
        //接收传进来的接口对象
        mOnInviteListener = onInviteListener;
    }


    public void refresh(List<InvitationInfo> invitationInfos) {
        if (invitationInfos != null && invitationInfos.size() >= 0) {
            //先清空
            mInvitation.clear();
            //再添加
            mInvitation.addAll(invitationInfos);
        }
        //通知刷新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mInvitation == null ? 0 : mInvitation.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitation.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或获取viewholder
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mcontext, R.layout.item_invite, null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_invite_name);
            viewHolder.tvReason = (TextView) convertView.findViewById(R.id.tv_invite_reason);
            viewHolder.btAccept = (Button) convertView.findViewById(R.id.btn_invite_accept);
            viewHolder.btReject = (Button) convertView.findViewById(R.id.btn_invite_reject);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //获得当前的item数据
        final InvitationInfo invitationInfo = mInvitation.get(position);
        //设置显示
        GroupInfo groupInfo = invitationInfo.getGroupInfo();
        if (groupInfo == null) {
            //个人
            //名称
            viewHolder.tvName.setText(invitationInfo.getUserInfo().getName());
            //原因
            if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.NEW_INVITE) {
                //设置接收和拒绝按钮可见
                viewHolder.btAccept.setVisibility(View.VISIBLE);
                viewHolder.btReject.setVisibility(View.VISIBLE);
                if (invitationInfo.getReason() == null) {
                    viewHolder.tvReason.setText("约吗？");
                } else {
                    viewHolder.tvReason.setText(invitationInfo.getReason());
                }
            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT) {
                //设置接收和拒绝按钮不可见
                viewHolder.btAccept.setVisibility(View.GONE);
                viewHolder.btReject.setVisibility(View.GONE);
                if (invitationInfo.getReason() == null) {
                    viewHolder.tvReason.setText("接受了邀请！");
                } else {
                    viewHolder.tvReason.setText(invitationInfo.getReason());
                }
            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {
                //设置接收和拒绝按钮不可见
                viewHolder.btAccept.setVisibility(View.GONE);
                viewHolder.btReject.setVisibility(View.GONE);
                if (invitationInfo.getReason() == null) {
                    viewHolder.tvReason.setText("邀请被接受");
                } else {
                    viewHolder.tvReason.setText(invitationInfo.getReason());
                }
            }
            //处理接受button的点击事件
            viewHolder.btAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onAccept(invitationInfo);
                }
            });
            //处理拒绝button的点击事件
            viewHolder.btReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invitationInfo);
                }
            });
        } else {
            //群
            //显示名称
            viewHolder.tvName.setText(invitationInfo.getGroupInfo().getInvitePerson());
            //先隐藏按钮
            viewHolder.btAccept.setVisibility(View.GONE);
            viewHolder.btReject.setVisibility(View.GONE);

            //显示原因
            switch (invitationInfo.getStatus()) {
                // 您的群申请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    viewHolder.tvReason.setText("您的群申请已经被接受");
                    break;

                //  您的群邀请已经被接受
                case GROUP_INVITE_ACCEPTED:
                    viewHolder.tvReason.setText("您的群邀请已经被接受");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    viewHolder.tvReason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    viewHolder.tvReason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    // 显示按钮
                    viewHolder.btAccept.setVisibility(View.VISIBLE);
                    viewHolder.btReject.setVisibility(View.VISIBLE);

                    // 群邀请状态的接受按钮
                    viewHolder.btAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invitationInfo);
                        }
                    });

                    // 群邀请状态的拒绝按钮
                    viewHolder.btReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invitationInfo);
                        }
                    });

                    viewHolder.tvReason.setText("您收到了群邀请");
                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    // 显示按钮
                    viewHolder.btAccept.setVisibility(View.VISIBLE);
                    viewHolder.btReject.setVisibility(View.VISIBLE);

                    // 群申请状态的接受按钮
                    viewHolder.btAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invitationInfo);
                        }
                    });

                    // 群申请状态的拒绝按钮
                    viewHolder.btReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invitationInfo);
                        }
                    });

                    viewHolder.tvReason.setText("您收到了群申请");
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    viewHolder.tvReason.setText("你接受了群邀请");
                    break;

                // 您批准了群加入
                case GROUPO_ACCEPT_APPLICATION:
                    viewHolder.tvReason.setText("您批准了群加入");
                    break;
            }

        }
        //返回view
        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvReason;
        Button btAccept;
        Button btReject;
    }

    public interface OnInviteListener {
        //接受按钮的点击事件
        void onAccept(InvitationInfo invitationInfo);

        //拒绝按钮的点击事件
        void onReject(InvitationInfo invitationInfo);

        //邀请信息接受按钮点击事件
        void onInviteAccept(InvitationInfo invitationInfo);

        //邀请信息拒绝按钮点击事件
        void onInviteReject(InvitationInfo invitationInfo);

        //申请信息接受按钮点击事件
        void onApplicationAccept(InvitationInfo invitationInfo);

        //申请信息拒绝按钮点击事件
        void onApplicationReject(InvitationInfo invitationInfo);
    }
}
