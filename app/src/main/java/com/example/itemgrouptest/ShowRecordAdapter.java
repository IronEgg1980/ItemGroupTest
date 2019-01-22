package com.example.itemgrouptest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

public class ShowRecordAdapter extends RecyclerView.Adapter<ShowRecordAdapter.ViewHolder>{

    public List<RecordEntity> getRecordEntityList() {
        return recordEntityList;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    private int position;
    private Context mContext;
    private List<RecordEntity> recordEntityList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView name,count;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.show_item_name);
            count = (TextView) itemView.findViewById((R.id.show_item_count));
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            RecordEntity recordEntity = recordEntityList.get(position);
            menu.setHeaderTitle(recordEntity.getName());
            ((ShowActivity)mContext).createMenu(menu);
        }
    }
    public ShowRecordAdapter(){ recordEntityList = new ArrayList<RecordEntity>();}
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
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final RecordEntity r = recordEntityList.get(i);
        final String s = r.getName();
        final int c = r.getCount();
        viewHolder.name.setText(r.getName());
        viewHolder.count.setText(String.valueOf(r.getCount()));
        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(i);
                return false;
            }
        });
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(i);
                viewHolder.view.showContextMenu();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordEntityList.size();
    }

    public boolean updateList(Calendar c,int offset){
        boolean b = false;
        if(offset == 0){
            if (recordEntityList!=null)
                recordEntityList.clear();
            else
                recordEntityList = new ArrayList<>();
        }else{
            Collections.reverse(recordEntityList);
        }
        long ONE_DAY_MILLISECONDS = 1000*60*60*24;
       // Log.d("MyDate2", c.getTime().toString());
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
       // Log.d("MyDate3", c.getTime().toString());
        long start = c.getTimeInMillis();
        long end = start + c.getActualMaximum(Calendar.DATE) * ONE_DAY_MILLISECONDS;
        List<RecordEntity> temp = LitePal
                .where("date >= ? and date < ?",String.valueOf(start),String.valueOf(end))
                .order("date desc")
                .limit(20)
                .offset(offset)
                .find(RecordEntity.class);
        if(temp.size() >0) {
            b = true;
            recordEntityList.addAll(temp);
        }
        Collections.reverse(recordEntityList);
        notifyDataSetChanged();
        return b;
    }
}
