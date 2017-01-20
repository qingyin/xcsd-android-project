package com.tuxing.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tuxing.sdk.manager.ContactManager;
import com.tuxing.sdk.manager.CounterManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.manager.impl.ContactManagerImpl;
import com.tuxing.sdk.manager.impl.CounterManagerImpl;
import com.tuxing.sdk.manager.impl.LoginManagerImpl;
import com.tuxing.sdk.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alan on 2015/7/7.
 */
public class AlarmReceiver extends BroadcastReceiver{
    private static final Logger logger = LoggerFactory.getLogger(AlarmReceiver.class);
    private static final String ACTION_TEMPLATE = "tuxing.alarm.%s.action";

    private CounterManager counterManager = CounterManagerImpl.getInstance();
    private LoginManager loginManager = LoginManagerImpl.getInstance();
    private ContactManager contactManager = ContactManagerImpl.getInstance();

    private String action;

    public AlarmReceiver(String version){
        action = String.format(ACTION_TEMPLATE, version);
    }

    public String getAction() {
        return action;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action)) {
            if(loginManager.getCurrentUser() != null) {
                counterManager.updateCounters();
                if(CollectionUtils.isEmpty(contactManager.getAllDepartment())){
                    contactManager.syncContact();
                }
            }
        }
    }
}
