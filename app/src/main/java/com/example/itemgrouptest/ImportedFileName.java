package com.example.itemgrouptest;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class ImportedFileName extends LitePalSupport {
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private Date date;
    private String fileName;
}
