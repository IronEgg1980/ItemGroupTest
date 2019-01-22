package com.example.itemgrouptest;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SumTotalRecordAdapter extends RecyclerView.Adapter<SumTotalRecordAdapter.ViewHolder> {
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

    public SumTotalRecordAdapter(){
        recordEntityList = new ArrayList<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.record_view_item,viewGroup,false);
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        final SumTotalRecordAdapter.ViewHolder viewHolder = new SumTotalRecordAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final SumTotalRecord r = recordEntityList.get(i);
        viewHolder.name.setText(r.getName());
        viewHolder.count.setText(String.valueOf(r.getCount()));
    }

    @Override
    public int getItemCount() {
        return recordEntityList.size();
    }

    public void resetData(){
        recordEntityList.clear();
        notifyDataSetChanged();
        LitePal.deleteAll(SumTotalRecord.class);
        LitePal.deleteAll(ImportedFileName.class);
    }

    public void updateList(){
        recordEntityList.clear();
        recordEntityList.addAll(LitePal.findAll(SumTotalRecord.class));
        notifyDataSetChanged();
    }
}
