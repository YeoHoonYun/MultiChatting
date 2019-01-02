package yun.fast.multichatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {
    private String name;
    private List<User> userList;

    public Room(String name) {
        this.name = name;
        this.userList = Collections.synchronizedList(new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void userAdd(User user) {
        this.userList.add(user);
    }
}

