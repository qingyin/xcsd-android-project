package com.tuxing.app.easemob.util;

import android.content.Context;
import com.tuxing.app.R;
import com.tuxing.app.easemob.domain.User;
import com.tuxing.app.easemob.iml.TuxingHXSDKHelper;
import com.tuxing.app.view.RoundImageView;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = TuxingHXSDKHelper.getInstance().getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
            user.setNick(username);
//            currentUser.setAvatar("http://downloads.easemob.com/downloads/57.png");
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, RoundImageView imageView){
        User user = getUserInfo(username);
        if(user != null){
        	imageView.setImageUrl("http://gravatar.duoshuo.com/avatar/5ccc4ef4f1e0e06f2b612406fea738bc?s=90&d=identicon", R.drawable.default_avatar);
//            Picasso.with(context).load(currentUser.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
        	imageView.setImageUrl("", R.drawable.default_avatar);
//            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    
}
