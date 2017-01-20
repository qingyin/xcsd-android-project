package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.LightApp;

import java.util.List;

/**
 * Created by alan on 16/2/18.
 */
public interface LightAppManager extends BaseManager{
    void fetchLightApps();

    List<LightApp> getHomeLightApps();
}
