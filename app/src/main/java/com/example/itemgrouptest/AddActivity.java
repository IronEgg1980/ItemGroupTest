package com.example.itemgrouptest;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class AddActivity extends AppCompatActivity {

    Date date;
    TextView dateTextView;
    TextSwitcher infoTextView;
    EditText count;
    Spinner name;
    SimpleDateFormat dateformat;
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_layout);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        dateTextView = (TextView) findViewById(R.id.add_activity_dateTextView);
        infoTextView = (TextSwitcher) findViewById(R.id.add_activity_info);
        infoTextView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(AddActivity.this );
                return tv;
            }
        });
        c = Calendar.getInstance();
        date = c.getTime();
        dateformat = new SimpleDateFormat("yyyy年M月d日");
        dateTextView.setText(dateformat.format(date));
        name = (Spinner) findViewById(R.id.add_activity_spninner);
        name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
           public void onItemSelected(AdapterView<?> arg0, View arg1,
                                      int arg2, long arg3) {
               count.requestFocus();
               count.selectAll();
               // 打开键盘
               InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.showSoftInput(count,1);
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
               count.requestFocus();
               count.selectAll();
           }
       });
        count = (EditText) findViewById(R.id.add_activity_editTextView);
        initSpinner();
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
    public void selectDate(View view){
        new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateTextView.setText(year+"年"+(month+1)+"月"+dayOfMonth+"日");
                c = Calendar.getInstance();
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);//
                date = c.getTime();
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void confirm(View view){
        if(TextUtils.isEmpty(count.getText())){
            count.setError("请输入数量！");
            count.requestFocus();
            return;
        }
        int _count = Integer.valueOf(count.getText().toString());
        if(_count == 0){
            count.setError("数量不能为0");
            count.requestFocus();
            return;
        }
        String _name = name.getSelectedItem().toString();
        String[] dates = getStartEndDate();
        RecordEntity r = LitePal
                .where("name = ? and date >= ? and date < ?",_name,dates[0],dates[1])
                .findFirst(RecordEntity.class);
        if (r == null) {
            r = new RecordEntity(date,_name,_count);
            r.save();
        } else {
            r.setDate(date);
            r.setCount(r.getCount()+_count);
            r.save();
        }
        count.setText("");
        infoTextView.setText("【名称："+_name + "】【数量："+_count+"】");
        Toast.makeText(this,"添加成功！\n点击关闭按钮返回。",Toast.LENGTH_SHORT).show();
    }
    public void quit(View view){
        finish();
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
    void initSpinner(){
        List<String> s = new ArrayList<>();
        for(ItemName name:LitePal.order("name").find(ItemName.class)){
            s.add(name.getName());
        }
        ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<>(this,R.layout.spinner_item,android.R.id.text1,s);
        stringArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        name.setAdapter(stringArrayAdapter);
    }
}
