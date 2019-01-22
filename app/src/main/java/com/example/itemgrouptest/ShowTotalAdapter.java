package com.example.itemgrouptest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

public class ShowTotalAdapter extends RecyclerView.Adapter<ShowTotalAdapter.ViewHolder>{

    public List<SumTotalRecord> getRecordEntityList() {
        return recordEntityList;
    }
    private Context mContext;
    private List<SumTotalRecord> recordEntityList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,count;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.show_item_name);
            count = (TextView) itemView.findViewById((R.id.show_item_count));
        }
    }
    public ShowTotalAdapter(){
        recordEntityList = new ArrayList<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.record_view_item,viewGroup,false);
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final SumTotalRecord r = recordEntityList.get(i);
        viewHolder.name.setText(r.getName());
        viewHolder.count.setText(String.valueOf(r.getCount()));
    }

    @Override
    public int getItemCount() {
        return recordEntityList.size();
    }

    public void updateList(Calendar c){
        recordEntityList.clear();
        recordEntityList.addAll(SumRecord.getSumData(c));
        notifyDataSetChanged();
    }

    public void updateList(Date start,Date end){
        recordEntityList.clear();
        recordEntityList.addAll(SumRecord.getSumData(start,end));
        notifyDataSetChanged();
    }
}
