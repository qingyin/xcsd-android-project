package com.tuxing.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tuxing.sdk.event.NetworkEvent;
import com.tuxing.sdk.utils.NetworkUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alan on 2015/7/7.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(NetworkStateReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        if(NetworkUtils.isNetWorkAvailable(context)){
            logger.debug("Network is available.");
            EventBus.getDefault().post(new NetworkEvent(
                    NetworkEvent.EventType.NETWORK_AVAILABLE,
                    null
            ));
        }else{
            logger.debug("Network state is unavailable.");
            EventBus.getDefault().post(new NetworkEvent(
                    NetworkEvent.EventType.NETWORK_UNAVAILABLE,
                    null
            ));
        }


    }
}
