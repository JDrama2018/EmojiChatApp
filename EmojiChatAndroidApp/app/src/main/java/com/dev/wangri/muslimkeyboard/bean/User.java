package com.dev.wangri.muslimkeyboard.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Super on 5/10/2017.
 */

public class User implements Serializable {
    public String id;
    public String username;
    public String firstname;
    public String lastname;
    public String email;
    public String photo;
    public long lastSeen;
    public Map<String, Object> dialogs;
    public String birthday;
    public String pushToken;
    public boolean notification;

    public User() {

    }

//    public User(String id, String username, String email, String photo, String bday) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.photo = photo;
//        this.birthday = bday;
//    }

}
