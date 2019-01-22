package com.example.itemgrouptest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemSetupActivity extends AppCompatActivity {

    private ItemSetupAdapter adapter;
    private EditText editText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_setup);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("编辑项目");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        recyclerView = (RecyclerView) findViewById(R.id.item_setup_recyclerview);
        editText = (EditText)findViewById(R.id.item_setup_name_edittext);
        adapter = new ItemSetupAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new ShowItemDecoration(this));
        if(adapter.getItemCount() > 0)
            recyclerView.smoothScrollToPosition(adapter.getItemCount() -1);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }
    public void addItemName(View view){
        if(TextUtils.isEmpty(editText.getText())){
            editText.requestFocus();
            editText.setError("请输入名称");
            return;
        }
        String name = editText.getText().toString().trim();
        if(LitePal.where("name = ?",name).find(ItemName.class).size() > 0){
            editText.requestFocus();
            editText.selectAll();
            editText.setError("名称重复");
            return;
        }
        ItemName itemName = new ItemName();
        itemName.setName(name);
        itemName.setOftenUse(false);
        itemName.save();
        adapter.getmItemNameList().add(itemName);
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount() > 0)
             recyclerView.smoothScrollToPosition(adapter.getItemCount() -1);
        editText.setText("");
        editText.requestFocus();
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
}
