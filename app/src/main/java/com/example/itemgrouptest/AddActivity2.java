package com.example.itemgrouptest;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddActivity2 extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    AddRecordAdapter adapter;
    Map listCache;
    SimpleDateFormat dateFormat;
    Date date;
    TextView dateTextView;
    TextSwitcher infoName,infoCount;
    int count;
    String currentName,selectName;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    int spanCount;
    Calendar c ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add2_layout);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        sharedPreferences = getSharedPreferences("AppSetup",MODE_PRIVATE);
        spanCount = sharedPreferences.getInt("SpanCount",3);
        radioButton1 = (RadioButton) findViewById(R.id.isOftenUse_radio);
        radioButton1.setChecked(true);
        radioGroup = (RadioGroup)findViewById(R.id.addActivity2RadioGroup);
        listCache = new HashMap<String,Integer>();
        dateTextView = (TextView) findViewById(R.id.add_activity2_dateTextView);
        initialSwitchText();
        count = 1;
        currentName = "";
        selectName = "";
        c = Calendar.getInstance();
        date = c.getTime();
        dateFormat = new SimpleDateFormat("yyyy年M月d日");
        dateTextView.setText(dateFormat.format(date));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.add_activity2_recycler);
        adapter = new AddRecordAdapter();
        adapter.setMyClickListener(new AddRecordAdapter.MyClickListener() {
            @Override
            public void OnClick(View view) {
                String[] dates = getStartEndDate();
                selectName = view.getTag().toString();
                //int key = adapter.getPosition();
                if(listCache.containsKey(selectName)){
                    count=(Integer) listCache.get(selectName)+1;
                }else{
                    count =1;
                }
                listCache.put(selectName,count);
                RecordEntity r = LitePal
                        .where("name = ? and date >= ? and date < ?",selectName,dates[0],dates[1])
                        .findFirst(RecordEntity.class);
                if (r!=null) {
                    r.setDate(date);
                    r.setCount(r.getCount()+1);
                    r.save();
                } else {
                    r = new RecordEntity(date,selectName,1);
                    r.save();
                }
                showInfo();
            }
        });
        GridLayoutManager manager = new GridLayoutManager(this,spanCount);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.isOftenUse_radio){
                    adapter.update(true);
                }else{
                    adapter.update(false);
                }
            }
        });
    }
    void showInfo(){
        if(!selectName.equals(currentName)){
            currentName = selectName;
            infoName.setText(selectName);
        }
        infoCount.setText(String.valueOf(count));
    }
    void initialSwitchText(){
        infoName = (TextSwitcher) findViewById(R.id.add_activity2_info_name);
        infoCount = (TextSwitcher) findViewById(R.id.add_activity2_info_count);
        infoName.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv1 = new TextView(AddActivity2.this );
                tv1.setTextSize(20);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv1.setTextColor(Color.WHITE);
                return tv1;
            }
        });
        infoCount.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv2 = new TextView(AddActivity2.this );
                tv2.setTextSize(20);
                tv2.setTextColor(Color.WHITE);
                tv2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                return tv2;
            }
        });
    }
    String[] getStartEndDate(){
        String[] dates = new String[2];
        long ONE_DAY_MILLISECONDS = 1000*60*60*24;
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        long start = c.getTimeInMillis();
        long end = start + ONE_DAY_MILLISECONDS;
        dates[0] = String.valueOf(start);
        dates[1] = String.valueOf(end);
        return dates;
    }
    public void selectDate(View view){
        listCache.clear();
        new DatePickerDialog(AddActivity2.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateTextView.setText(year+"年"+(month+1)+"月"+dayOfMonth+"日");
                c = Calendar.getInstance();
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                date = c.getTime();
                Log.d("时间", "onDateSet: 1_"+date.getTime());
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                adapter.setOftenUse(false);
                break;
            case 2:
                adapter.setOftenUse(true);
                break;
        }
        return super.onContextItemSelected(item);
    }
}
