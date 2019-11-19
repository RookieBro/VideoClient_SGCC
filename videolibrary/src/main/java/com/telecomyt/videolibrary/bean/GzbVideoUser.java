package com.telecomyt.videolibrary.bean;

/**
 * Created by lbx on 2017/9/27.
 */

public class GzbVideoUser {

    public String name;
    public String id;

    public GzbVideoUser(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "GzbVideoUser{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
