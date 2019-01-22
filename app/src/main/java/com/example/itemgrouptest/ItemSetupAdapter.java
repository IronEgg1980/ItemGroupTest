package com.example.itemgrouptest;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ItemSetupAdapter extends RecyclerView.Adapter<ItemSetupAdapter.ViewHolder> {
    public List<ItemName> getmItemNameList() {
        return mItemNameList;
    }
    private List<ItemName> mItemNameList;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public View view;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_textView);
            view = itemView;
        }
    }
    public ItemSetupAdapter(){
        mItemNameList = LitePal.findAll(ItemName.class);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerveiw_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.nameTextView.setText(mItemNameList.get(i).getName());
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(getmItemNameList().get(i).getName())
                        .setMessage("是否删除该项？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LitePal.delete(ItemName.class,getmItemNameList().get(i).getId());
                                getmItemNameList().remove(i);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消",null).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mItemNameList.size();
    }
}
