package com.telecomyt.videolibrary.bean;

import java.util.List;

/**
 * @author lbx
 * @date 2018/3/6.
 */

public class DefaultVideoRoom<T extends VideoDefaultMeeting> {

    private String name;
    private List<T> list;

    public DefaultVideoRoom(String name, List<T> list) {
        this.name = name;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public List<T> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list == null || list.size() == 0;
    }
}
