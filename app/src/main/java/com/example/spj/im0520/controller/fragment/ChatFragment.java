package com.example.spj.im0520.controller.fragment;

import android.content.Intent;

import com.example.spj.im0520.controller.activity.ChatActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

/**
 * Created by spj on 2016/8/3.
 */
//会话界面
public class ChatFragment extends EaseConversationListFragment{



    @Override
    protected void initView() {
        super.initView();
        //跳转到会话界面的监听
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
                //群聊会话类型
                if(conversation.getType() == EMConversation.EMConversationType.Chat.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        });
        conversationList.clear();
       //监听会话消息的变化
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        //接收会话消息的时候调用
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            //刷新显示会话列表
            EaseUI.getInstance().getNotifier().onNewMesg(list);
            //刷新
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
