package com.example.itemgrouptest;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class ItemName extends LitePalSupport {
    private int id;
    private String name;
    private boolean isOftenUse;

    public boolean isOftenUse() {
        return isOftenUse;
    }

    public void setOftenUse(boolean oftenUse) {
        isOftenUse = oftenUse;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ItemName(){

    }
    public ItemName(String _name,boolean _isOftenUse){
        this.name = _name;
        this.isOftenUse = _isOftenUse;
    }
}
