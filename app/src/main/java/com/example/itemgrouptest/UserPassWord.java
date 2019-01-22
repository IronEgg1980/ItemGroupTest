package com.example.itemgrouptest;

import org.litepal.crud.LitePalSupport;

public class UserPassWord extends LitePalSupport {
    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private int key;
    private String value;
}
