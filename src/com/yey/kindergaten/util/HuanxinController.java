package com.yey.kindergaten.util;

import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.Teacher;
import com.yey.kindergaten.db.DbHelper;

import java.util.List;

/**
 * Created by zyj on 2015/3/12.
 * 环信登陆，查询身份，环信注册等。
 */
public class HuanxinController {

    private  static final String TAG = "HuanxinController";

    /**
     * 根据MessageRecent表获取有身份显示的姓名
     * @param message
     */
    public static String getRelationNameByRecent(MessageRecent message){
        String nick = message.getName();
        if (message.getAction() == 0) {
            AccountInfo info2 = AppContext.getInstance().getAccountInfo();
            String hxFromId = message.getHxfrom();
            String from = "0";
            if (hxFromId.length() > 2) {
                from = hxFromId.substring(0, hxFromId.length() - 2);
            }
            int relation;
            try {
                relation = Integer.valueOf(hxFromId.substring(hxFromId.length() - 1, hxFromId.length()));
                UtilsLog.i(TAG, "getRelationNameByRecent get relation");
            } catch (Exception e) {
                UtilsLog.i(TAG, "getRelationNameByRecent get relation Exception:" + e.getMessage());
                relation = 0;
            }
            String nickRelation = AppConstants.RELATIONNAME[relation];
            if (info2.getRole() == 0) {
                List<Teacher> director = DbHelper.findDirector();
//              nick = nick + "("+"老师"+")";
                if (director!=null && director.size()!=0) {
                    for (Teacher teacher : director) {
                        if (String .valueOf(teacher.getUid()).equals(from)) {
                            if (!nick.contains("(园长)")) {
                                nick = nick + "(" + "园长" + ")";
                            }
                            break;
                        }
                    }
                }
                if (!nick.contains("(园长)") && !nick.contains("(老师)")) {
                    nick = nick + "(" + "老师" + ")";
                }
            } else if (info2.getRole() == 1) {
                if (relation == 0) {
                    List<Teacher> director = DbHelper.findDirector();
                    boolean flag = true;
                    if (director!=null && director.size()!=0) {
                        for (Teacher teacher : director) {
                            if (String .valueOf(teacher.getUid()).equals(from)) {
                                nick = nick + "(" + "园长" + ")";
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        nick = nick + "(" + "老师" + ")";
                    }
                } else {
                    nick = nick + "(" + nickRelation + ")";
                }
            } else if (info2.getRole() == 2) {
                if (relation == 0) {
                    nick = nick + "(" + "老师" + ")";
                } else {
                    nick = nick + "(" + nickRelation + ")";
                }
            }
        }
        return nick;
    }

    /**
     * 根据EMMessage表获取有身份显示的姓名
     * @param mess
     */
    public static String getRelationNameByHuanxinRecent(EMMessage mess) {
        String nick = null;

        AccountInfo info2 = AppContext.getInstance().getAccountInfo();
        String from = mess.getFrom().substring(0, mess.getFrom().length() - 2);
        int relation = Integer.valueOf(mess.getFrom().substring(mess.getFrom().length() - 1, mess.getFrom().length()));
        String nickRelation = AppConstants.RELATIONNAME[relation];
        try {
            UtilsLog.i(TAG,"getRelationNameByHuanxinRecent get nick:" + nick);
            nick = mess.getStringAttribute("nick") == null ? "" : mess.getStringAttribute("nick");
        } catch (EaseMobException e) {
            UtilsLog.i(TAG,"getRelationNameByHuanxinRecent get nick Exception: " + e.getMessage());
        }
        if (info2.getRole() == 0) {
            UtilsLog.i(TAG,"getRelationNameByHuanxinRecent getRole is Director ");
            List<Teacher> director = DbHelper.findDirector();
            boolean flag = true;
            if (director!=null && director.size()!=0) {
                for (Teacher teacher : director) {
                    if (String .valueOf(teacher.getUid()).equals(from)) {
                        nick = nick + "(" + "园长" + ")";
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                nick = nick + "(" + "老师" + ")";
            }
        } else if (info2.getRole() == 1) {
            UtilsLog.i(TAG, "getRelationNameByHuanxinRecent getRole is Teacher ");
            if (relation == 0) {
                List<Teacher> director = DbHelper.findDirector();
                if (director!=null && director.size()!=0) {
                    for (Teacher teacher : director) {
                        nick = nick + "(" + "老师" + ")";
                        if (String .valueOf(teacher.getUid()).equals(from)) {
                            nick = nick + "(" + "园长" + ")";
                            break;
                        }
                    }
                }
            }
        } else if (info2.getRole() == 2) {
            if (relation == 0) {
                nick = nick + "(" + "老师" + ")";
            } else {
                nick = nick + "(" + nickRelation + ")";
            }
        }
        return nick;
    }


}
