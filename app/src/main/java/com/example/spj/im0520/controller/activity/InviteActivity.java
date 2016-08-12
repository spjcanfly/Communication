package com.example.spj.im0520.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.spj.im0520.R;
import com.example.spj.im0520.controller.adapter.InviteAdapter;
import com.example.spj.im0520.model.Modle;
import com.example.spj.im0520.model.bean.InvitationInfo;
import com.example.spj.im0520.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

//邀请信息列表页面
public class InviteActivity extends Activity {

    private ListView lv_invite;
    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        //点击接受按钮
        @Override
        public void onAccept(final InvitationInfo invitationInfo) {
            //联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUserInfo().getHxid());
                        //更新数据库
                        Modle.getInstance().getDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT,
                                invitationInfo.getUserInfo().getHxid());
                        //提示
                        runOnUiThread(  new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "亲接受了约哦", Toast.LENGTH_SHORT).show();
                                //更新界面
                                inviteRefresh();
                            }
                        });


                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受失败:"+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });


        }

        //点击拒绝按钮
        @Override
        public void onReject(final InvitationInfo invitationInfo) {
            //联网
            Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invitationInfo.getUserInfo().getHxid());
                        //更新数据库
                        Modle.getInstance().getDbManager().getInvitationDao().removeInvitation(invitationInfo.getUserInfo().getHxid());
                        //刷新加提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //刷新失败
                                inviteRefresh();
                                Toast.makeText(InviteActivity.this, "拒绝成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                inviteRefresh();
                                Toast.makeText(InviteActivity.this, "拒绝失败："+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
        // 接受邀请
        @Override
        public void onInviteAccept(final InvitationInfo invitationInfo) {
           Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
               @Override
               public void run() {
                   try {
                       EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.
                               getGroupInfo().getGroupId(), invitationInfo.getGroupInfo().getInvitePerson());
                       //本地数据库修改
                       Modle.getInstance().getDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.
                               GROUP_ACCEPT_INVITE,invitationInfo.getGroupInfo().getInvitePerson());
                       //刷新页面
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               inviteRefresh();

                               Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                           }
                       });
                   } catch (final HyphenateException e) {
                       e.printStackTrace();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(InviteActivity.this, "失败"+e, Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }
           });

        }
        // 拒绝邀请
        @Override
        public void onInviteReject(final InvitationInfo invitationInfo) {
             Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
                 @Override
                 public void run() {
                     //联网
                     try {
                         EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroupInfo()
                                 .getGroupId(),invitationInfo.getGroupInfo().getInvitePerson(),"拒绝你");
                         //本地数据库更新
                         Modle.getInstance().getDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE,
                                 invitationInfo.getGroupInfo().getInvitePerson());
                         //刷新页面
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {

                                 inviteRefresh();

                                 Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();
                             }
                         });
                     } catch (final HyphenateException e) {
                         e.printStackTrace();
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(InviteActivity.this, "失败" + e, Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 }
             });
        }

        // 接受申请
        @Override
        public void onApplicationAccept(final InvitationInfo invitationInfo) {
          Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
              @Override
              public void run() {
                  //联网
                  try {
                      EMClient.getInstance().groupManager().acceptApplication(invitationInfo.getGroupInfo()
                              .getGroupId(),invitationInfo.getGroupInfo().getInvitePerson());
                      //本地数据库更新
                      Modle.getInstance().getDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUPO_ACCEPT_APPLICATION,
                              invitationInfo.getGroupInfo().getInvitePerson());
                      //更新页面
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              inviteRefresh();
                              
                              Toast.makeText(InviteActivity.this, "接受申请", Toast.LENGTH_SHORT).show();
                          }
                      });
                  } catch (final HyphenateException e) {
                      e.printStackTrace();
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(InviteActivity.this, "申请接受失败"+e, Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
              }
          });
        }
        // 申请被拒绝
        @Override
        public void onApplicationReject(final InvitationInfo invitationInfo) {
           Modle.getInstance().getGlobalThreadPool().execute(new Runnable() {
               @Override
               public void run() {
                   try {
                       EMClient.getInstance().groupManager().declineApplication(invitationInfo.getGroupInfo().getGroupId(),
                               invitationInfo.getGroupInfo().getInvitePerson(),"申请被拒绝");
                       //本地数据库处理
                       Modle.getInstance().getDbManager().getInvitationDao().updateInvitationStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION,
                               invitationInfo.getGroupInfo().getInvitePerson());
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               //刷新页面
                               inviteRefresh();
                               
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                       });
                   } catch (final HyphenateException e) {
                       e.printStackTrace();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(InviteActivity.this, "失败"+e, Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }
           });
        }
    };
    private InviteAdapter inviteAdapter;
    private BroadcastReceiver InviteChangedRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到邀请信息变化的广播后
            if(intent.getAction() == Constant.CONTACT_INVITE_CHANGED || intent.getAction() == Constant.GROUP_INVITE_CHANGED) {
                //刷新数据
                inviteRefresh();
            }
        }
    };
    private LocalBroadcastManager mLBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initView();

        initData();
    }

    private void initData() {

        //创建适配器
        inviteAdapter = new InviteAdapter(this, mOnInviteListener);
        //添加适配器到listview
        lv_invite.setAdapter(inviteAdapter);
        //刷新数据
        inviteRefresh();
         //监听邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(this);
        // 联系人邀请信息变化
        mLBM.registerReceiver(InviteChangedRecevier, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        // 群邀请信息变化
        mLBM.registerReceiver(InviteChangedRecevier,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    //刷新方法
    private void inviteRefresh() {

        List<InvitationInfo> invitations = Modle.getInstance().getDbManager().getInvitationDao().getInvitations();
        inviteAdapter.refresh(invitations);
    }

    private void initView() {

        lv_invite = (ListView) findViewById(R.id.lv_invite);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解注册广播
        mLBM.unregisterReceiver(InviteChangedRecevier);
    }
}
