package com.tuxing.sdk.modle;

import com.tuxing.sdk.db.entity.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alan on 2015/6/11.
 */
public class DepartmentMember  implements Serializable {
    private static final long serialVersionUID = 3852749952469350810L;
    
    private User user;
    private List<User> relatives;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<User> relatives) {
        this.relatives = relatives;
    }
}
