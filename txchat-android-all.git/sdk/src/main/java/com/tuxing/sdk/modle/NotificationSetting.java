package com.tuxing.sdk.modle;

import java.io.Serializable;

/**
 * Created by Alan on 2015/7/1.
 */
public class NotificationSetting implements Serializable{
    private static final long serialVersionUID = 8435209925550503816L;

    private Integer disturbFreeSetting;
    private Integer enableSound;
    private Integer enableVibration;

    public Integer getDisturbFreeSetting() {
        return disturbFreeSetting;
    }

    public void setDisturbFreeSetting(Integer disturbFreeSetting) {
        this.disturbFreeSetting = disturbFreeSetting;
    }

    public Integer getEnableSound() {
        return enableSound;
    }

    public void setEnableSound(Integer enableSound) {
        this.enableSound = enableSound;
    }

    public Integer getEnableVibration() {
        return enableVibration;
    }

    public void setEnableVibration(Integer enableVibration) {
        this.enableVibration = enableVibration;
    }
}
