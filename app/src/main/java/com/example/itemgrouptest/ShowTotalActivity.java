package com.example.itemgrouptest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static org.litepal.LitePalApplication.getContext;

public class ShowTotalActivity extends AppCompatActivity {
    Date start,end;
    DatePicker startPicker,endPicker;
    Calendar calendar;
    SimpleDateFormat format,format1;
    TextView dateTextView;
    ShowTotalAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_total_layout);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("统计数据");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        context = this;
        format = new SimpleDateFormat("yyyy年M月份");
        format1 = new SimpleDateFormat("yyyy年M月d日 H:mm:ss");;
        recyclerView = findViewById(R.id.show_total_activity_recycler);
        dateTextView = (TextView)findViewById(R.id.activity_show_total_month_text);
        calendar = Calendar.getInstance();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new ShowTotalAdapter();
        adapter.updateList(calendar);
        ShowItemDecoration decoration = new ShowItemDecoration(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
        showInfo();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }
    public void preMonth_Total(View view){
        calendar.add(Calendar.MONTH,-1);
        adapter.updateList(calendar);
        showInfo();
    }
    public void currentMonth_Total(View view){
        calendar = Calendar.getInstance();
        adapter.updateList(calendar);
        showInfo();
    }
    public void nextMonth_Total(View view){
        calendar.add(Calendar.MONTH,1);
        adapter.updateList(calendar);
        showInfo();
    }
    public void customDate_Total(View view){
        View dialogView = LayoutInflater.from(ShowTotalActivity.this).inflate(R.layout.select_startdate_enddate,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowTotalActivity.this);
        builder.setTitle("请选择日期");
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        startPicker = (DatePicker)dialogView.findViewById(R.id.start_date);
        endPicker = (DatePicker)dialogView.findViewById(R.id.end_date);
        ((Button)dialogView.findViewById(R.id.select_date_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(startPicker.getYear(),startPicker.getMonth(),startPicker.getDayOfMonth(),0,0,0);
                start = calendar.getTime();
                calendar.set(endPicker.getYear(),endPicker.getMonth(),endPicker.getDayOfMonth(),0,0,0);
                end = calendar.getTime();
                if(start.getTime()<=end.getTime()){
                    calendar.set(endPicker.getYear(),endPicker.getMonth(),endPicker.getDayOfMonth(),23,59,59);
                    end = calendar.getTime();
                }else{
                    start = end;
                    calendar.set(startPicker.getYear(),startPicker.getMonth(),startPicker.getDayOfMonth(),23,59,59);
                    end = calendar.getTime();
                }
                adapter.updateList(start,end);
                dialog.dismiss();
                String temp = format1.format(start.getTime())+"至"+format1.format(end.getTime());
                if(adapter.getItemCount() >1){
                    dateTextView.setText(temp+"的汇总数据");
                }else{
                    dateTextView.setText(temp+"没有记录");
                }
            }
        });
        ((Button)dialogView.findViewById(R.id.select_date_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    void showInfo(){
        if(adapter.getItemCount() >1){
            dateTextView.setText(format.format(calendar.getTime())+"的汇总数据");
        }else{
            dateTextView.setText(format.format(calendar.getTime())+"没有记录");
        }
    }
    // 分享数据
    void shareData(){
        if(adapter.getItemCount() == 0){
            Toast.makeText(ShowTotalActivity.this,"该月份没有汇总数据！",Toast.LENGTH_SHORT).show();
            return;
        }
        Uri fileUri = null;
        File file = getExcelFile();
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(this, "com.example.itemgrouptest.fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(intent);
    }
    //生存Excel文件
    File getExcelFile(){
        WritableWorkbook book = null;
        try {
            String serialNumber = android.os.Build.SERIAL;
            File excelFile = new File(getContext().getCacheDir(),"shared_by_"+serialNumber+".xls");
            if(excelFile.exists()){
                excelFile.delete();
            }
            excelFile.createNewFile();
            book = Workbook.createWorkbook(excelFile);
            WritableSheet ws = book.createSheet("sheet1", 0);
            Label label1 = new Label(0,0,"分享数据："+format.format(calendar.getTime()));
            ws.addCell(label1);
            for (int i = 1;i<=adapter.getItemCount();i++){
                SumTotalRecord record = adapter.getRecordEntityList().get(i - 1);
                Label label2 = new Label(0, i, record.getName());
                ws.addCell(label2);
                Label label3 = new Label(1, i, String.valueOf(record.getCount()));
                ws.addCell(label3);
            }
            book.write();
            return excelFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            try{
                if(book != null)
                    book.close();
            }catch (WriteException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.showtotal_toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.quit_app:
                ActivityManager.closeAll();
                break;
            case R.id.share_data:
                shareData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
