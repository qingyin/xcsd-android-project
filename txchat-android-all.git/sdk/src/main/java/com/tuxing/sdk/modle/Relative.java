package com.tuxing.sdk.modle;

import com.tuxing.sdk.db.entity.User;

import java.io.Serializable;

/**
 * Created by Alan on 2015/6/29.
 */
public class Relative implements Serializable{
    private static final long serialVersionUID = -5979104638798261470L;

    private User user;
    private int relativeType;
    private boolean master;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRelativeType() {
        return relativeType;
    }

    public void setRelativeType(int relativeType) {
        this.relativeType = relativeType;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
}
