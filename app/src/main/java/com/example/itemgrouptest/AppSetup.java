package com.example.itemgrouptest;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppSetup extends AppCompatActivity {

    final int WRITE_INPUT = 1,TOUCH_INPUT = 2;
    private LinearLayout linearLayout;
    private int spanCount;
    private Context context;
    private File backDirectory;
    private String[] pathList;
    private String filePath;
    private XmlSerializer serializer;
    private XmlPullParser pullParser;
    private SimpleDateFormat format;
    private RadioButton write,touch,spanCount2,spanCount3,spanCount4,alarm_yes,alarm_no;
    private SharedPreferences sharedPreferences;
    private RadioGroup spanCountGroup,alarmMode;
    private boolean alarmModeYes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setup);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("软件设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        context = this;
        sharedPreferences= getSharedPreferences("AppSetup",MODE_PRIVATE);
        alarmModeYes = sharedPreferences.getBoolean("Alarm",false);
        format = new SimpleDateFormat("yyyyMMdd");
        filePath = Environment.getExternalStorageDirectory()+"/MyBackup/";
        linearLayout = findViewById(R.id.linearLayout8);
        initialSpanRadioButton();
        initialAlarmRadioButton();
        initialInputMethod();
    }
    void initialSpanRadioButton(){
        spanCount2 = (RadioButton)findViewById(R.id.span_count_2);
        spanCount3 = (RadioButton)findViewById(R.id.span_count_3);
        spanCount4 = (RadioButton)findViewById(R.id.span_count_4);
        spanCount = sharedPreferences.getInt("SpanCount",3);
        spanCount2.setChecked(spanCount == 2);
        spanCount3.setChecked(spanCount == 3);
        spanCount4.setChecked(spanCount == 4);
        spanCountGroup = (RadioGroup)findViewById(R.id.span_count_group) ;
        spanCountGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (checkedId){
                    case R.id.span_count_2:
                        editor.putInt("SpanCount",2);
                        break;
                    case R.id.span_count_3:
                        editor.putInt("SpanCount",3);
                        break;
                    case R.id.span_count_4:
                        editor.putInt("SpanCount",4);
                        break;
                }
                editor.apply();
            }
        });
    }
    void initialAlarmRadioButton(){
        alarm_no = findViewById(R.id.change_alarm_no);
        alarm_no.setChecked(!alarmModeYes);
        alarm_yes = findViewById(R.id.change_alarm_yes);
        alarm_yes.setChecked(alarmModeYes);
        alarmMode = findViewById(R.id.alarm_mode);
        alarmMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (checkedId){
                    case R.id.change_alarm_yes:
                        editor.putBoolean("Alarm",true);
                        Toast.makeText(context, "已启用紧急模式，（/110/)你懂的！", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.change_alarm_no:
                        editor.putBoolean("Alarm",false);
                        Toast.makeText(context, "已关闭紧急模式", Toast.LENGTH_SHORT).show();
                        break;
                }
                editor.apply();
            }
        });
    }
    void initialInputMethod(){
        write = (RadioButton) findViewById(R.id.input_method_radio1);
        write.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeInputMethod(buttonView);
            }
        });
        touch = (RadioButton) findViewById(R.id.input_method_radio2);
        touch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeInputMethod(buttonView);
            }
        });
        if(sharedPreferences.getInt("InputMethod",1) ==  WRITE_INPUT){
            write.setChecked(true);
            linearLayout.setVisibility(View.GONE);
        }else{
            touch.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void clearDirectory(View view) {
        PermisionUtils.verifyStoragePermissions(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(filePath);
            if (path.exists()) {
                File[] files = path.listFiles();
                if(files.length == 0){
                    Toast.makeText(context,"备份文件夹干干净净，不用清理！",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(File file:files){
                    if(file.isFile()) {
                        file.delete();
                        continue;
                    }
                    if(deleteDirWihtFile(file)){
                        Toast.makeText(context,"备份文件夹清理完成！",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"备份文件夹清理失败！",Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Toast.makeText(context,"未找到备份文件夹！可能原因：您还没有备份过。",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return false;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();
        return true;// 删除目录本身
    }
    public void backupData(View view){
        PermisionUtils.verifyStoragePermissions(this );
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File path = new File(filePath+format.format(new Date()));
            // File path = new File(filePath+"20181209");
            if(!path.exists()){
                if(!path.mkdirs())
                {
                    Toast.makeText(this, "创建目录失败！请重试一次…", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            File itemsBackupFile = new File(path, "items.xml");
            File recordBackupFile = new File(path, "records.xml");
            boolean itemFlag = false,recordFlag = false;
            try {
                if(itemsBackupFile.exists()) {
                    itemFlag = itemsBackupFile.delete();
                }else{
                    itemFlag =true;
                }
                if(recordBackupFile.exists()){
                    recordFlag = recordBackupFile.delete();
                }else{
                    recordFlag = true;
                }
                if (itemFlag && recordFlag ) {
                    if (itemsBackupFile.createNewFile()) {
                        long i = 1;
                        FileOutputStream itemFos = new FileOutputStream(itemsBackupFile);
                        serializer = Xml.newSerializer();
                        serializer.setOutput(itemFos,"utf-8");
                        serializer.startDocument("utf-8",false);
                        serializer.startTag(null,"ItemList");
                        for(ItemName item:LitePal.findAll(ItemName.class)){
                            serializer.startTag(null,"Item");
                            serializer.attribute(null,"Id",String.valueOf(i++));
                            serializer.startTag(null,"Name");
                            serializer.text(item.getName());
                            serializer.endTag(null,"Name");
                            serializer.startTag(null,"isOftenUse");
                            serializer.text(item.isOftenUse()?"1":"0");
                            serializer.endTag(null,"isOftenUse");
                            serializer.endTag(null,"Item");
                        }
                        serializer.endTag(null,"ItemList");
                        serializer.endDocument();
                        serializer.flush();
                        itemFos.close();
                    }else{return;}
                    if (recordBackupFile.createNewFile()) {
                        List<RecordEntity> list = LitePal.findAll(RecordEntity.class);
                        FileOutputStream recordFos = new FileOutputStream(recordBackupFile);
                        long i = 1;
                        serializer = Xml.newSerializer();
                        serializer.setOutput(recordFos,"utf-8");
                        serializer.startDocument("utf-8",false);
                        serializer.startTag(null,"RecordList");
                        for (RecordEntity record:list) {
                            serializer.startTag(null, "Record");
                            serializer.attribute(null,"Id",String.valueOf(i++));
                            serializer.startTag(null,"Date");
                            serializer.text(String.valueOf(record.getDate().getTime()));
                            serializer.endTag(null,"Date");
                            serializer.startTag(null,"Name");
                            serializer.text(record.getName());
                            serializer.endTag(null,"Name");
                            serializer.startTag(null,"Count");
                            serializer.text(String.valueOf(record.getCount()));
                            serializer.endTag(null,"Count");
                            serializer.endTag(null, "Record");
                        }
                        serializer.endTag(null,"RecordList");
                        serializer.endDocument();
                        serializer.flush();
                        recordFos.close();
                    } else {
                        return;
                    }
                    TextView text = new TextView(this);
                    text.setTextSize(20);
                    text.setPadding(50,16,50,0);
                    text.setText("数据保存目录："+path.toString());
                    new android.app.AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("备份成功！")
                            .setView(text)
                            .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    Toast.makeText(this, "备份失败！请重试一次…", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("备份数据",e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "备份出错！请重试一次…", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"SD卡不可用，请稍后再试一下看看…",Toast.LENGTH_SHORT).show();
        }
    }
    public void restoreData(View view){
        File[] files ;
        boolean isExistRecord;
        PermisionUtils.verifyStoragePermissions(this );
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            final File path = new File(filePath);
            if(!path.exists()){
                isExistRecord = false;
            }else{
                isExistRecord = true;
                files = path.listFiles();
                if (files.length == 0) {
                    isExistRecord = false;
                } else {
                    List<String> directoryName = new ArrayList<>();
                    for (File file : files) {
                        if (file.isDirectory()) {
                            directoryName.add(file.getName());
                        }
                    }
                    if(directoryName.size() == 0){
                        isExistRecord = false;
                    }else {
                        pathList =directoryName.toArray(new String[0]);
                    }
                }
            }
            if(isExistRecord){
                backDirectory = new File(filePath+pathList[0]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("请选择要恢复的记录");
                builder.setSingleChoiceItems(pathList, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        backDirectory = new File(filePath+pathList[which]);
                        Toast.makeText(context, "已选择："+backDirectory.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("还原", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView text = new TextView(context);
                        text.setTextSize(16);
                        text.setPadding(50, 16, 50, 0);
                        text.setText("注意：【还原数据】操作将会重置所有数据，还原为您已备份的记录。是否确认该操作？");
                        new AlertDialog.Builder(context)
                                .setCancelable(true)
                                .setTitle("请确认！")
                                .setView(text)
                                .setPositiveButton("确认还原", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 这里写还原操作逻辑
                                        File itemsBackupFile = new File(backDirectory, "items.xml");
                                        File recordBackupFile = new File(backDirectory, "records.xml");
                                        try {
                                            if (itemsBackupFile.exists()) {
                                                pullParser = Xml.newPullParser();
                                                FileInputStream fis = new FileInputStream(itemsBackupFile);
                                                pullParser.setInput(fis, "utf-8");
                                                LitePal.deleteAll(ItemName.class);
                                                int event = pullParser.getEventType();
                                                ItemName itemName = new ItemName();
                                                while (event != XmlPullParser.END_DOCUMENT) {
                                                    switch (event) {
                                                        case XmlPullParser.START_TAG:
                                                            if ("Item".equals(pullParser.getName())) {
                                                                if(itemName == null)
                                                                    itemName=new ItemName();
                                                            }else if ("Name".equals(pullParser.getName())) {
                                                                itemName.setName(pullParser.nextText());
                                                            }else if("isOftenUse".equals(pullParser.getName())){
                                                                itemName.setOftenUse("0".equals(pullParser.nextText())?false:true);
                                                            }
                                                            break;
                                                        case XmlPullParser.END_TAG:
                                                            if("Item".equals(pullParser.getName())){
                                                                itemName.save();
                                                                itemName = null;
                                                            }
                                                    }
                                                    event = pullParser.next();
                                                }
                                                fis.close();
                                                Toast.makeText(context, "项目信息还原成功！\n开始还原详细记录…", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "未找到备份文件！项目信息还原失败。\n开始还原详细记录…", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            if (recordBackupFile.exists()) {
                                                pullParser = Xml.newPullParser();
                                                FileInputStream fis = new FileInputStream(recordBackupFile);
                                                pullParser.setInput(fis, "utf-8");
                                                LitePal.deleteAll(RecordEntity.class);
                                                int event = pullParser.getEventType();
                                                RecordEntity recordEntity = new RecordEntity();
                                                while (event != XmlPullParser.END_DOCUMENT) {
                                                    switch (event) {
                                                        case XmlPullParser.START_TAG:
                                                            if("Record".equals(pullParser.getName())) {
                                                                if(recordEntity == null)
                                                                    recordEntity = new RecordEntity();
                                                            }else if ("Date".equals(pullParser.getName())) {
                                                                recordEntity.setDate(new Date(Long.valueOf(pullParser.nextText())));
                                                            } else if ("Name".equals(pullParser.getName())) {
                                                                recordEntity.setName(pullParser.nextText());
                                                            } else if ("Count".equals(pullParser.getName())) {
                                                                recordEntity.setCount(Integer.valueOf(pullParser.nextText()));
                                                            }
                                                            break;
                                                        case XmlPullParser.END_TAG:
                                                            if("Record".equals(pullParser.getName())){
                                                                recordEntity.save();
                                                                recordEntity = null;
                                                            }
                                                    }
                                                    event = pullParser.next();
                                                }
                                                fis.close();
                                            } else {
                                                Toast.makeText(context, "未找到备份文件，详细记录还原失败！", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            dialog.dismiss();
                                        }
                                        LitePal.deleteAll(ImportedFileName.class);
                                        LitePal.deleteAll(SumTotalRecord.class);
                                        Toast.makeText(context, "详细记录还原成功！", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消操作", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "已取消还原数据操作", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }).create().show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("关闭",null);
                builder.create().show();
            }else{
                Toast.makeText(this, "未找到备份记录！", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void passwdSetup(View view){
        List<UserPassWord> list = LitePal.findAll(UserPassWord.class);
        if(list==null || list.size() == 0) {
            UserPassWord passWord = new UserPassWord();
            passWord.setKey(1);
            passWord.setValue("4187657");
            passWord.save();
        }
        final AlertDialog dialog =new AlertDialog.Builder(this).create();
        dialog.setTitle("修改密码");
        dialog.setView(new EditText(this));
        dialog.show();
        dialog.setContentView(R.layout.passwd_setup_dialog);
        Window window = dialog.getWindow();
        final EditText newPWD = window.findViewById(R.id.setup_newPWD_edittext),confirmPWD = window.findViewById(R.id.setup_confirmPWD_edittext);
        newPWD.requestFocus();
        window.findViewById(R.id.setup_pwd_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstString = newPWD.getText().toString().trim(),secondString = confirmPWD.getText().toString().trim();
                if(TextUtils.isEmpty(firstString)){
                    newPWD.requestFocus();
                    newPWD.setError("请输入新密码!");
                    return;
                }
                if(TextUtils.isEmpty(secondString) || !firstString.equals(secondString)){
                    confirmPWD.requestFocus();
                    confirmPWD.selectAll();
                    confirmPWD.setError("两次输入的密码不一致!");
                }else{
                    ContentValues values = new ContentValues();
                    values.put("value",firstString);
                    LitePal.updateAll(UserPassWord.class,values);
                    Toast.makeText(getBaseContext(),"密码修改成功！",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        window.findViewById(R.id.setup_pwd_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void changeInputMethod(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(write.isChecked()){
            editor.putInt("InputMethod",WRITE_INPUT);
            linearLayout.setVisibility(View.GONE);
        }
        if(touch.isChecked()){
            editor.putInt("InputMethod",TOUCH_INPUT);
            linearLayout.setVisibility(View.VISIBLE);
            initialSpanRadioButton();
        }
        editor.apply();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }
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
