package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.PlatformType;
import com.tuxing.rpc.proto.UpgradeRequest;
import com.tuxing.rpc.proto.UpgradeResponse;
import com.tuxing.sdk.event.UpgradeEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.UpgradeManager;
import com.tuxing.sdk.modle.UpgradeInfo;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Alan on 2015/8/5.
 */
public class UpgradeManagerImpl implements UpgradeManager {
    private final static Logger logger = LoggerFactory.getLogger(UpgradeManager.class);
    private static UpgradeManager instance;

    HttpClient httpClient = HttpClient.getInstance();

    private Context context;

    private UpgradeManagerImpl(){

    }

    public synchronized static UpgradeManager getInstance(){
        if(instance == null){
            instance = new UpgradeManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void destroy() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
    @Override
    public void getUpgradeInfo() {
        logger.debug("UpgradeManagerImpl::getUpgradeInfo()");


        UpgradeRequest request = new UpgradeRequest.Builder()
                .platformType(PlatformType.ANDROID)
                .build();

        httpClient.sendRequest(RequestUrl.UPGRADE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                UpgradeResponse response = SerializeUtils.parseFrom(data, UpgradeResponse.class);

                UpgradeInfo upgradeInfo = PbMsgUtils.transObj(response.upgrade);

                EventBus.getDefault().post(new UpgradeEvent(
                        UpgradeEvent.EventType.GET_UPGRADE_SUCCESS,
                        null,
                        upgradeInfo
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new UpgradeEvent(
                        UpgradeEvent.EventType.GET_UPGRADE_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });


    }
}
