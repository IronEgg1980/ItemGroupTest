package com.example.itemgrouptest;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class RecordEntity extends LitePalSupport {
    public int getId() {
        return id;
    }

    private int id;
    private Date date;
    private String name;
    private int count;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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
    public RecordEntity(){

    }
    public RecordEntity(Date _date,String _name,int _count){
        this.date = _date;
        this.name = _name;
        this.count = _count;
    }
}
