package com.tuxing.app.util;

import com.tuxing.sdk.modle.DepartmentMember;
import com.yixia.camera.util.Log;

import java.util.Comparator;

public class PinyinComparator implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        // 按照名字排序
        DepartmentMember user0 = (DepartmentMember) arg0;
        DepartmentMember user1 = (DepartmentMember) arg1;
        String catalog0 = "";
        String catalog1 = "";
        int type = 0;

        if (user0 != null && user0.getUser().getNickname() != null
                && user0.getUser().getNickname().length() >= 1)
            catalog0 = PingYinUtil.getPingYin(user0.getUser().getNickname())
                    .substring(0, 1) + "";

        if (user1 != null && user1.getUser().getNickname() != null
                && user1.getUser().getNickname().length() >= 1)
            catalog1 = PingYinUtil.getPingYin(user1.getUser().getNickname())
                    .substring(0, 1) + "";

        int flag = catalog0.toLowerCase().compareTo(catalog1.toLowerCase());
        if (flag == 0) {
            type = 0;
        } else if (flag > 0) {
            type = 1;
        } else {
            type = -1;
        }
        return type;

    }

}
