package com.tuxing.sdk.modle;

import java.io.Serializable;

/**
 * Created by Alan on 2015/8/5.
 */
public class UpgradeInfo implements Serializable {
    private static final long serialVersionUID = -5198601540028000920L;

    boolean showAtMain;
    boolean forceUpgrade;
    boolean hasNewVersion;
    String upgradeMsg;
    String showMsg;
    String version;
    String upgradeUrl;

    public boolean isShowAtMain() {
        return showAtMain;
    }

    public void setShowAtMain(boolean showAtMain) {
        this.showAtMain = showAtMain;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public boolean isHasNewVersion() {
        return hasNewVersion;
    }

    public void setHasNewVersion(boolean hasNewVersion) {
        this.hasNewVersion = hasNewVersion;
    }

    public String getUpgradeMsg() {
        return upgradeMsg;
    }

    public void setUpgradeMsg(String upgradeMsg) {
        this.upgradeMsg = upgradeMsg;
    }

    public String getShowMsg() {
        return showMsg;
    }

    public void setShowMsg(String showMsg) {
        this.showMsg = showMsg;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpgradeUrl() {
        return upgradeUrl;
    }

    public void setUpgradeUrl(String upgradeUrl) {
        this.upgradeUrl = upgradeUrl;
    }
}
