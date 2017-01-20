package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.App;
import com.tuxing.rpc.proto.FetchAppRequest;
import com.tuxing.rpc.proto.FetchAppResponse;
import com.tuxing.sdk.db.entity.LightApp;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.LightAppManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alan on 16/2/18.
 */
public class LightAppManagerImpl implements LightAppManager{
    private final static Logger logger = LoggerFactory.getLogger(LightAppManager.class);
    private static LightAppManager instance;

    private Context context;
    HttpClient httpClient = HttpClient.getInstance();

    private LightAppManagerImpl(){

    }

    public synchronized static LightAppManager getInstance(){
        if(instance == null){
            instance = new LightAppManagerImpl();
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
    public void fetchLightApps() {
        FetchAppRequest request = new FetchAppRequest();

        httpClient.sendRequest(RequestUrl.GET_LIGHT_APPS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchAppResponse response = SerializeUtils.parseFrom(data, FetchAppResponse.class);

                List<LightApp> lightApps = new ArrayList<>();

                if(!CollectionUtils.isEmpty(response.homeApps)){
                    for(App app : response.homeApps){
                        LightApp lightApp = PbMsgUtils.transObj(app);
                        lightApp.setShowAt(Constants.LIGHT_APP_SHOW_AT.HOME);
                        lightApps.add(lightApp);
                    }

                    UserDbHelper.getInstance().saveLightApps(lightApps);
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("Get light app list error!", cause);
            }
        });
    }

    @Override
    public List<LightApp> getHomeLightApps() {
        return UserDbHelper.getInstance().getLightAppsByShown(Constants.LIGHT_APP_SHOW_AT.HOME);
    }
}
