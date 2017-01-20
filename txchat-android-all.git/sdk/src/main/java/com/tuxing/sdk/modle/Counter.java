package com.tuxing.sdk.modle;

import java.io.Serializable;

/**
 * Created by Alan on 2015/7/15.
 */
public class Counter implements Serializable, Cloneable {
    private static final long serialVersionUID = -4306849875393937223L;

    private String name;
    private int value;
    private long lastTime;
    private String summary;

    public Counter(){

    }

    public Counter(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public Counter clone(){
        Counter other = new Counter();
        other.setName(name);
        other.setValue(value);
        other.setLastTime(lastTime);
        other.setSummary(summary);

        return other;
    }
}
