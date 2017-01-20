package com.tuxing.app.domain;

public class HomeMenuItem {

    public int icon;
    public String name;
    public String counter;
    public String activityClass;
    public boolean showCount;
    public String text_name;

    public HomeMenuItem(int icon, String name, String counter, String activityClass, boolean showCount,String text_name) {
        this.icon = icon;
        this.name = name;
        this.counter = counter;
        this.activityClass = activityClass;
        this.showCount = showCount;
        this.text_name = text_name;
    }

    public String getText_name() {
        return text_name;
    }

    public void setText_name(String text_name) {
        this.text_name = text_name;
    }

    public HomeMenuItem(int icon, String counter, String activityClass, boolean showCount) {
        this.icon = icon;
        this.name = name;
        this.counter = counter;
        this.activityClass = activityClass;
        this.showCount = showCount;
    }

}
