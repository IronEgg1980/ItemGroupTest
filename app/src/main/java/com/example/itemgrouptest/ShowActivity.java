package com.example.itemgrouptest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.litepal.LitePalApplication.getContext;

public class ShowActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    Calendar calendar;
    int offset;
    SimpleDateFormat format;
    TextView dateTextView;
    ShowRecordAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_layout);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查看记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        offset = 0;
        format = new SimpleDateFormat("yyyy年M月份");
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecord();
            }
        });
        recyclerView = findViewById(R.id.show_activity_recycler);
        dateTextView = (TextView)findViewById(R.id.activity_show_month_text);
        calendar = Calendar.getInstance();
        //Log.d("MyDate1", calendar.getTime().toString());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new ShowRecordAdapter();
        adapter.updateList(calendar,offset);
        ShowItemDecoration decoration = new ShowItemDecoration(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
        recyclerView.addItemDecoration(new ShowItemStickDecoration(this, new ShowItemStickDecoration.DecorationCallback() {
            @Override
            public Date getDate(int position) {
                return adapter.getRecordEntityList().get(position).getDate();
            }
        }));
        smoothRecyclerView(adapter.getItemCount() -1);
    }
    void refreshRecord(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try{
//                    Thread.sleep(500);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        offset = offset+20;
                        if (!adapter.updateList(calendar, offset)) {
                            Toast.makeText(ShowActivity.this,"已加载该月份所有数据!",Toast.LENGTH_SHORT).show();
                        }else{
                            smoothRecyclerView(20);
                            Toast.makeText(ShowActivity.this,"刷新成功!",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }

    public void preMonth(View view){
        offset = 0;
        calendar.add(Calendar.MONTH,-1);
        adapter.updateList(calendar,offset);
        smoothRecyclerView(adapter.getItemCount() -1);
    }
    public void currentMonth(View view){
        offset = 0;
        calendar = Calendar.getInstance();
        adapter.updateList(calendar,offset);
         smoothRecyclerView(adapter.getItemCount() -1);
    }
    public void nextMonth(View view){
        offset = 0;
        calendar.add(Calendar.MONTH,1);
        adapter.updateList(calendar,offset);
        smoothRecyclerView(adapter.getItemCount() -1);
    }

    private void smoothRecyclerView(int position){
        if(adapter.getItemCount() >1){
            dateTextView.setText(format.format(calendar.getTime())+"的详细记录");
            recyclerView.smoothScrollToPosition(position);
        }else{
            dateTextView.setText(format.format(calendar.getTime())+"没有记录");
        }
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
        final RecordEntity r = adapter.getRecordEntityList().get(adapter.getPosition());
        switch (item.getItemId()){
            case 1:
                final EditText et = new EditText(recyclerView.getContext());
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setText(String.valueOf(r.getCount()));
                AlertDialog dialog =new AlertDialog.Builder(recyclerView.getContext())
                        .setCancelable(false)
                        .setTitle("名称："+r.getName()).setMessage("如需修改，请输入数量：")
                        .setView(et)
                        .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(TextUtils.isEmpty(et.getText())){
                                    et.setError("请输入数量！");
                                    et.requestFocus();
                                    closeDialog(dialog,false);
                                    return;
                                }
                                int _count = Integer.valueOf(et.getText().toString());
                                if (_count == 0) {
                                    et.setError("数量不能为 0");
                                    et.requestFocus();
                                    closeDialog(dialog,false);
                                    return;
                                }
                                closeDialog(dialog,true);
                                r.setCount(Integer.valueOf(_count));
                                r.save();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(recyclerView.getContext(),"修改成功！",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }})
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeDialog(dialog,true);
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case 2:
                AlertDialog dialog1 =new AlertDialog.Builder(recyclerView.getContext())
                                .setTitle("是否删除【"+r.getName()+"】的记录？")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LitePal.delete(RecordEntity.class,r.getId());
                                        adapter.getRecordEntityList().remove(r);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(recyclerView.getContext(),"删除成功！",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消",null).show();
                break;
            default:break;
        }
        return super.onContextItemSelected(item);
    }

    public void createMenu(Menu menu)
    {
        int groupID = 0;
        int order = 0;
        int[] itemID = {1,2};

        for(int i=0;i<itemID.length;i++)
        {
            switch(itemID[i])
            {
                case 1:
                    menu.add(groupID, itemID[i], order, "修改");
                    break;
                case 2:
                    menu.add(groupID, itemID[i], order, "删除");
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 决定是否关闭对话框
     * @param dialog 对话框对象
     * @param isClosed 对话框是否显示  true:关闭  false:不关闭
     */
    private void closeDialog(DialogInterface dialog, boolean isClosed) {
        //1.得到当前AlertDialog 的类模板对象
        Class clz_alertDialog= dialog.getClass();
        //2.得到当前类的父类的类模板
        Class clz_dialog=clz_alertDialog.getSuperclass();
        try {
            Field mShowing_Filed= clz_dialog.getDeclaredField("mShowing");
            //如果私有字段想在类外访问,则必须取消java 语法检查
            mShowing_Filed.setAccessible(true);
            mShowing_Filed.set(dialog,isClosed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
