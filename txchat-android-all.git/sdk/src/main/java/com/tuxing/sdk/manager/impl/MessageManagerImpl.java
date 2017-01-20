package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.sdk.db.entity.RevokedMessage;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.manager.MessageManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Alan on 2015/9/10.
 */
public class MessageManagerImpl implements MessageManager{
    private final static Logger logger = LoggerFactory.getLogger(MessageManager.class);
    private static MessageManager instance;

    private Context context;

    private MessageManagerImpl(){

    }

    public synchronized static MessageManager getInstance(){
        if(instance == null){
            instance = new MessageManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void destroy() {

    }

    @Override
    public RevokedMessage getRevokedMessage(String msgId) {
        return UserDbHelper.getInstance().getRevokedMessageByMsgId(msgId);
    }

    @Override
    public void deleteRevokedMessage(String msgId) {
        UserDbHelper.getInstance().deleteRevokedMessage(msgId);
    }

    @Override
    public void saveRevokedMessage(RevokedMessage revokedMessage) {
        UserDbHelper.getInstance().saveRevokedMessage(revokedMessage);
    }

    @Override
    public List<RevokedMessage> getAllRevokedMessage() {
        return UserDbHelper.getInstance().getAllRevokedMessage();
    }
}
