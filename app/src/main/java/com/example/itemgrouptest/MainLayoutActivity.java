package com.example.itemgrouptest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;

public class MainLayoutActivity extends AppCompatActivity {
    private TextView info,version;
    private SimpleDateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);
        ActivityManager.add(this);
        version = findViewById(R.id.version);
        version.setText(packageName(this));
        info = (TextView) findViewById(R.id.main_activity_info);
        format = new SimpleDateFormat("最近一次记录：yyyy年M月d日");
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordEntity entity = LitePal.order("date").findLast(RecordEntity.class);
        if(entity == null)
            info.setText("");
        else
            info.setText(format.format(entity.getDate()));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }

    public void addClick(View view){
        SharedPreferences pref = getSharedPreferences("AppSetup",MODE_PRIVATE);
        Intent intent;
        if(pref.getInt("InputMethod",1) == 1){
            intent = new Intent(MainLayoutActivity.this,AddActivity.class);
        }else{
            intent = new Intent(MainLayoutActivity.this,AddActivity2.class);
        }
        startActivity(intent);
    }

    public void showClick(View view){
        Intent intent = new Intent(MainLayoutActivity.this,ShowActivity.class);
        startActivity(intent);
    }

    public void showTotalClick(View view){
        Intent intent = new Intent(MainLayoutActivity.this,ShowTotalActivity.class);
        startActivity(intent);
    }
    public void importData(View view){
        startActivity(new Intent(MainLayoutActivity.this,ImportData.class));
    }
    public void appSetup(View view){
        Intent intent = new Intent(MainLayoutActivity.this,AppSetup.class);
        startActivity(intent);
    }
    public void itemsetClick(View view){
        Intent intent = new Intent(MainLayoutActivity.this,ItemSetupActivity.class);
        startActivity(intent);
    }
    public void quitAppClick(View view){
        ActivityManager.closeAll();
    }

//    private int packageCode(Context context) {
//        PackageManager manager = context.getPackageManager();
//        int code = 0;
//        try {
//            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//            code = info.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return code;
//    }
    private String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }
}
