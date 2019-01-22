package com.example.itemgrouptest;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class SumTotalRecord extends LitePalSupport {
    private String name;
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public SumTotalRecord(){

    }
    public SumTotalRecord(String _name,int _count){
        this.name = _name;
        this.count = _count;
    }
}
