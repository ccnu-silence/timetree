/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yey.kindergaten.huanxin;

import android.content.Intent;

import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Friend;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.huanxin.Activity.ChatActivity2;
import com.yey.kindergaten.huanxin.bean.User;
import com.yey.kindergaten.huanxin.controller.HXSDKHelper;
import com.yey.kindergaten.huanxin.model.HXSDKModel;
import com.yey.kindergaten.huanxin.util.CommonUtils;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.TimeUtil;

import java.util.Map;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * @author easemob
 *
 */
public class DemoHXSDKHelper extends HXSDKHelper {

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    
    @Override
    protected void initHXOptions(){
        super.initHXOptions();
        // you can also get EMChatOptions to set related SDK options
        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener(){
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
      return new OnMessageNotifyListener() {

          @Override
          public String onNewMessageNotify(EMMessage message) {
              // 设置状态栏的消息提示，可以根据message的类型做相应提示
              String ticker = CommonUtils.getMessageDigest(message, appContext);
              String nick = "";
              try {
                   nick = message.getStringAttribute("nick")==null?"":message.getStringAttribute("nick");
              } catch (EaseMobException e) {
                  e.printStackTrace();
              }
              /*if(message.getType() == Type.TXT)
                  ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");*/
              switch (message.getType()) {
                  case TXT:
                      TextMessageBody txtBody = (TextMessageBody)message.getBody();
                      ticker = txtBody.getMessage();
                      ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                      break;
                  case IMAGE:
                      ticker = "[图片]";
                      break;
                  case VOICE:
                      ticker = "[语音]";
                      break;
                  case LOCATION:
                      ticker = "[位置]";
                      break;

              }
             handleChat(message);
              return nick + ": " + ticker;
          }

          @Override
          public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
              return null;
             // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
          }

          @Override
          public String onSetNotificationTitle(EMMessage message) {
              //修改标题,这里使用默认
              return null;
          }

          @Override
          public int onSetSmallIcon(EMMessage message) {
              //设置小图标
              return 0;
          }
      };
    }
    
    @Override
    protected OnNotificationClickListener getNotificationClickListener(){
        return new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                Intent intent = new Intent(appContext, ChatActivity2.class);
//                ChatType chatType = message.getChatType();
//                Session session = Session.getSession();
//                try {
//                    String nick = message.getStringAttribute("nick");
//                    String avatar = message.getStringAttribute("avatar");
//
//                    if (chatType == ChatType.Chat) { // 单聊信息
//                        intent.putExtra("userId", message.getFrom());
//                        intent.putExtra("chatType", ChatActivity2.CHATTYPE_SINGLE);
//                    } else { // 群聊信息
//                        // message.getTo()为群聊id
//                        intent.putExtra("groupId", message.getTo());
//                        intent.putExtra("chatType", ChatActivity2.CHATTYPE_GROUP);
//                    }
//                } catch (EaseMobException e) {
//                    e.printStackTrace();
//                }
                try {
                    String avatar = message.getStringAttribute("avatar")==null?"":message.getStringAttribute("avatar");
                    String nick = message.getStringAttribute("nick")==null?"":message.getStringAttribute("nick");
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                intent.putExtra("userId", message.getFrom());
                return intent;
            }
        };
    }
    
    @Override
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
    }
    
    @Override
    protected void onCurrentAccountRemoved(){
    	Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
    
    
    @Override
    protected void initListener(){
        super.initListener();
      /*  IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingVoiceCallBroadcastAction());
        appContext.registerReceiver(new VoiceCallReceiver(), callFilter);    */
    }

    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }
    
    /** 、
     *
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel(){
        return (DemoHXSDKModel) hxModel;
    }
    
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((DemoHXSDKModel) getModel()).getContactList();
        }
        
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }
    
    @Override
    public void logout(final EMCallBack callback){
        super.logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                setContactList(null);
                getModel().closeDB();
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) { }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }

    public Friend toFriend = null;

    public Friend getToFriend() {
        return toFriend;
    }

    public void setToFriend(Friend toFriend) {
        this.toFriend = toFriend;
    }

    private void handleChat( final EMMessage mess) {
        String from = mess.getFrom().substring(0,mess.getFrom().length()-1);
        String to = mess.getTo().substring(0,mess.getTo().length()-1);
        AccountInfo info2 =  AppContext.getInstance().getAccountInfo();
        MessageRecent newMessagePublic = null;
        try {
            String nick = mess.getStringAttribute("nick");
            String avatar = mess.getStringAttribute("avatar");
            switch (mess.getType()) {
                case TXT:
                    TextMessageBody txtBody = (TextMessageBody)mess.getBody();
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getChatTime(mess.getMsgTime()),from , to, txtBody.getMessage(),txtBody.getMessage(), "", "", 1, AppConstants.PUSH_ACTION_FRIENDS, 0,avatar,0,mess.getFrom(),mess.getTo());
                    break;
                case IMAGE:
                    ImageMessageBody imgBody = (ImageMessageBody)mess.getBody();
                               /* Log.d("img message from:" + mess.getFrom() + " thumbnail:" + imgBody.getThumbnailUrl()
                                        + " remoteurl:" + imgBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getChatTime(mess.getMsgTime()),from , to, imgBody.getRemoteUrl(),imgBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 1,avatar,0,mess.getFrom(),mess.getTo());
                    break;
                      case VOICE:
                     VoiceMessageBody voiceBody = (VoiceMessageBody)mess.getBody();
                              /*  Log.d("voice message from:" + message.getFrom() + " length:" + voiceBody.getLength()
                                        + " remoteurl:" + voiceBody.getRemoteUrl()+ " \n\r");*/
                    newMessagePublic  = new MessageRecent(mess.getMsgId(), nick, TimeUtil.getChatTime(mess.getMsgTime()),from , to,voiceBody.getRemoteUrl(),voiceBody.getRemoteUrl(), "", "", 1,AppConstants.PUSH_ACTION_FRIENDS, 2,avatar,0,mess.getFrom(),mess.getTo());
                                break;
                    case LOCATION:
                       LocationMessageBody locationBody = (LocationMessageBody)mess.getBody();
                             /*   Log.d("location message from:" + message.getFrom() + " address:" + locationBody.getAddress() +" \n\r");*/
                                break;

            }
            try {
                MessageRecent messagePublic = DbHelper.getDB(AppContext.getInstance()).findFirst(Selector.from(MessageRecent.class).where("fromId","=",from));
                if(messagePublic == null){
                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); //存入最近会话
                }else{
                    int count =messagePublic.getNewcount();
                    DbHelper.getDB(AppContext.getInstance()).delete(MessageRecent.class, WhereBuilder.b("msgid", "=", messagePublic.getMsgid()));;
                    newMessagePublic.setNewcount(count+1);
                    DbHelper.getDB(AppContext.getInstance()).save(newMessagePublic); //存入最近会话
                }

            } catch (DbException e) {
                e.printStackTrace();
            }
        } catch (EaseMobException e) {
            e.printStackTrace();
        }


    };
}
