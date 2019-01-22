package com.example.itemgrouptest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static org.litepal.LitePalApplication.getContext;

public class ImportData extends AppCompatActivity {
    Calendar calendar;
    ImportedFileName localData;
    SimpleDateFormat format,format1;
    SumTotalRecordAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    List<File> importFiles;
    String[] pathList = null;
    File path = null;
    String myExcelFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("合并数据");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityManager.add(this);
        context = this;
        myExcelFileName = "shared_by_"+android.os.Build.SERIAL+".xls";
        recyclerView = findViewById(R.id.importdata_recyclerView);
        format = new SimpleDateFormat("yyyy年M月份");
        format1 = new SimpleDateFormat("yyyyMM");
        calendar = Calendar.getInstance();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new SumTotalRecordAdapter();
        adapter.updateList();
        ShowItemDecoration decoration = new ShowItemDecoration(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
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
    // 清空接收到的文件
    public void clearReceivedFiles(View view){
        PermisionUtils.verifyStoragePermissions(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = new File(Environment.getExternalStorageDirectory() + "/tencent/MicroMsg/download/");
            if (!path.exists()) {
                path = new File("/tencent/MicroMsg/download/");
            }
        }
        if (!path.exists()) {
            Toast.makeText(context,"未找到微信目录，请联系软件提供者…",Toast.LENGTH_LONG).show();
        } else {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    if (name.length() > 13) {
                        String startName = name.substring(0, 10);
                        String lastName = name.substring(name.length() - 3);
                        if ("xls".equals(lastName) && "shared_by_".equals(startName)) {
                            file.delete();
                        }
                    }
                }
            }
            Toast.makeText(context,"微信接收到数据文件已清理干净，可以重新接收新数据。",Toast.LENGTH_LONG).show();
        }
    }
    //导入数据
    public void importFile(View view) {
        importFiles = new ArrayList<>();
        File[] files;
        boolean isExistRecord;
        PermisionUtils.verifyStoragePermissions(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = new File(Environment.getExternalStorageDirectory() + "/tencent/MicroMsg/download/");
            if (!path.exists()) {
                path = new File("/tencent/MicroMsg/download/");
            }
        }
        if (!path.exists()) {
            isExistRecord = false;
        } else {
            isExistRecord = true;
            files = path.listFiles();
            if (files.length == 0) {
                isExistRecord = false;
            } else {
                List<String> tempFileNameList = new ArrayList<>();
                for (File file : files) {
                    String name = file.getName();
                    if(myExcelFileName.equals(name))
                        continue;
                    if (name.length() > 13) {
                        String startName = name.substring(0, 10);
                        String lastName = name.substring(name.length() - 3);
                        if (!file.isDirectory()) {
                            if ("xls".equals(lastName) && "shared_by_".equals(startName)) {
                                if (LitePal.where("filename = ?", name).find(ImportedFileName.class).size() > 0) {
                                    name = name + "(已导入)";
                                }
                                tempFileNameList.add(name);
                            }
                        }
                    }
                }
                if (tempFileNameList.size() == 0) {
                    isExistRecord = false;
                } else {
                    pathList = tempFileNameList.toArray(new String[0]);
                }
            }
        }
        if (isExistRecord) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
            builder.setTitle("请选择要合并的文件");
            builder.setMultiChoiceItems(pathList, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    String name = pathList[which];
                    if ("(已导入)".equals(name.substring(name.length() - 5))) {
                        name = name.substring(0, name.length() - 5);
                        Log.d("文件名", "onClick:1 " + name);
                    }
                    File _file = new File(path + "/" + name);
                    if (isChecked) {
                        importFiles.add(_file);
                    } else {
                        importFiles.remove(_file);
                    }
                }
            });
            builder.setPositiveButton("选好了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int count = importFiles.size();
                    List<String> skipedFile = new ArrayList<>();
                    for (File file : importFiles) {
                        String file_name = file.getName();
                        ImportedFileName importedFileName = LitePal.where("filename = ?", file_name).findFirst(ImportedFileName.class);
                        if (importedFileName != null) {
                            skipedFile.add(file_name);
                            count--;
                            continue;
                        }
                        importedFileName = new ImportedFileName();
                        importedFileName.setFileName(file_name);
                        importedFileName.save();
                        saveExcelToDatabase(file);
                    }
                    adapter.updateList();
                    String s = "成功导入"+count+"个文件！\n";
                    if (skipedFile.size() > 0) {
                        s =s+ "跳过重复文件：";
                        for (String _s : skipedFile) {
                            s += _s + " 、";
                        }
                    }
                    s = s.substring(0, s.length() - 1);
                    TextView textView = new TextView(context);
                    textView.setTextSize(15);
                    textView.setText(s);
                    textView.setPadding(30,5,20,0);
                    new AlertDialog.Builder(context).setTitle("导入完成").setView(textView).setNegativeButton("关闭",null)
                            .create().show();
                }
            }).setNegativeButton("取消", null);
            builder.create().show();
        } else {
            Toast.makeText(context, "未找到外部数据文件，请通知其他人员重新发送数据。", Toast.LENGTH_SHORT).show();
        }
    }

    void saveExcelToDatabase(File file) {
        Workbook book = null;
        try {//写导入数据逻辑
            book = Workbook.getWorkbook(file);
            Sheet sheet = book.getSheet(0);
            int rows = sheet.getRows();
            for (int i = 1; i < rows; i++) {
                String name = sheet.getCell(0, i).getContents();
                int count = Integer.valueOf(sheet.getCell(1, i).getContents());
                SumTotalRecord record = LitePal.where("name = ?", name).findFirst(SumTotalRecord.class);
                if (record == null){
                    record = new SumTotalRecord(name,count);
                }else{
                    record.setCount(record.getCount()+count);
                }
                record.save();
            }
            book.close();
        } catch (Exception e) {
            e.printStackTrace();}
    }
    public void resetData(View view){
        adapter.resetData();
        Toast.makeText(context, "上一次合并的数据已重置！现在可以重新从文件和本机导入数据。", Toast.LENGTH_SHORT).show();
    }

    public void importThisData(View view){
        localData = LitePal.where("filename = 'local'").findFirst(ImportedFileName.class);
        if(localData != null){
            Toast.makeText(context,"已导入本机数据，请勿重复导入！",Toast.LENGTH_SHORT).show();
            return;
        }
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        //创建并显示DatePickerDialog
        DatePickerDialog dialog =  new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,1);
                List<SumTotalRecord> sumTotalRecords = SumRecord.getSumData(calendar);
                if(sumTotalRecords.size() == 0){
                    Toast.makeText(context,format.format(calendar.getTime())+"没有数据!",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(SumTotalRecord record:sumTotalRecords){
                    String name = record.getName();
                    int _count = record.getCount();
                    SumTotalRecord r = LitePal.where("name = ?",name).findFirst(SumTotalRecord.class);
                    if(r == null){
                        r = new SumTotalRecord(name,_count);
                        r.save();
                    }else{
                        int count = r.getCount();
                        r.setCount(count+_count);
                        r.save();
                    }
                }
                adapter.updateList();
                localData = new ImportedFileName();
                localData.setFileName("local");
                localData.save();
                Toast.makeText(context,"成功导入本机数据!",Toast.LENGTH_SHORT).show();
            }
        },mYear,mMonth,mDay);
        dialog.setTitle("选择月份");
        dialog.show();
        //只显示年月，隐藏掉日
        DatePicker dp = findDatePicker((ViewGroup) dialog.getWindow().getDecorView());
        if (dp != null) {
            ((ViewGroup)((ViewGroup)dp.getChildAt(0)).getChildAt(0))
                    .getChildAt(2).setVisibility(View.GONE);
            // 如果想隐藏掉年，将getChildAt(2)改为getChildAt(0)
        }
    }
    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }
    void shareData(){
        if(adapter.getItemCount() == 0){
            Toast.makeText(ImportData.this,"列表内没有数据！\n请先合并数据，全部完成后再发送。",Toast.LENGTH_LONG).show();
            return;
        }
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Uri fileUri = null;
                File file = getExcelFile();
                if (Build.VERSION.SDK_INT >= 24) {
                    fileUri = FileProvider.getUriForFile(context, "com.example.itemgrouptest.fileprovider", file);
                } else {
                    fileUri = Uri.fromFile(file);
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.ms-excel");
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(intent);
            }
        }, mYear, mMonth, mDay);
        dialog.setTitle("待发送数据的月份");
        dialog.show();
        //只显示年月，隐藏掉日
        DatePicker dp = findDatePicker((ViewGroup) dialog.getWindow().getDecorView());
        if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
                    .getChildAt(2).setVisibility(View.GONE);
            // 如果想隐藏掉年，将getChildAt(2)改为getChildAt(0)
        }
    }
    //生存Excel文件
    File getExcelFile(){
        WritableWorkbook book = null;
        try {
            File excelFile = new File(getContext().getCacheDir(),format1.format(calendar.getTime())+"_total.xls");
            if(excelFile.exists()){
                excelFile.delete();
            }
            excelFile.createNewFile();
            book = Workbook.createWorkbook(excelFile);
            WritableSheet ws = book.createSheet("sheet1", 0);
            Label label1 = new Label(0,0,format.format(calendar.getTime()));
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

}
