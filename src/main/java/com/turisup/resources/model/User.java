package com.turisup.resources.model;

import lombok.Data;

@Data
public class User {
    String user;
    String id;

    public User(String user, String id) {
        this.user = user;
        this.id = id;
    }
}
