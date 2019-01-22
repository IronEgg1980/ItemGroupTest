package com.example.itemgrouptest;

import android.database.Cursor;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SumRecord {

    public static List<SumTotalRecord> getSumData(Calendar c){
        long ONE_DAY_MILLISECONDS = 1000*60*60*24;
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        long start = c.getTimeInMillis();
        long end = start + c.getActualMaximum(Calendar.DATE) * ONE_DAY_MILLISECONDS-1;
        return getSumData(new Date(start),new Date(end));
    }

    public static List<SumTotalRecord> getSumData(Date start, Date end){
        List<SumTotalRecord> list = new ArrayList<>();
        long startLong = start.getTime();
        long endLong = end.getTime();
        Cursor cursor = LitePal.findBySQL("SELECT name FROM recordentity WHERE date >= ? AND date <= ? GROUP BY name ORDER BY name",String.valueOf(startLong),String.valueOf(endLong));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int count = LitePal.where("name = ? AND date >= ? AND date <= ? ",name,String.valueOf(startLong),String.valueOf(endLong)).sum(RecordEntity.class,"count",int.class);
                SumTotalRecord totalRecord = new SumTotalRecord(name,count);
                list.add(totalRecord);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
