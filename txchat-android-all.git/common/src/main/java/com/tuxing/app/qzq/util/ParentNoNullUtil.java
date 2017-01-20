package com.tuxing.app.qzq.util;

import com.tuxing.sdk.db.entity.User;

/**
 * Created by shan on 2016/4/28.
 */
public class ParentNoNullUtil {
    public static String getNickName(User user) {
        String str = "";
        if (user.getNickname() != null) {
            str = user.getNickname();
        }else if(user.getCombinedNickname() != null){
            str = user.getCombinedNickname();
        }else if(user.getUsername() != null){
            str = user.getUsername();
        }else if(String.valueOf(user.getChildUserId()) != null){
            str = String.valueOf(user.getChildUserId());
        }else if(user.getRealname() !=null){
            str = user.getRealname();
        }
        return str;
    }
}
