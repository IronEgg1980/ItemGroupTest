package com.example.itemgrouptest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class AddRecordAdapter extends RecyclerView.Adapter<AddRecordAdapter.AddViewHolder> {
    private List<ItemName> items;

    public int getPosition() {
        return position;
    }
    private int position;

    public interface MyClickListener{
        void OnClick(View view);
    };
    // 点击监听接口
    private MyClickListener myClickListener;
    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public AddRecordAdapter(){
        update(true);
    }
    @Override
    public AddRecordAdapter.AddViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add2_item_layout,viewGroup,false);
        return new AddViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddViewHolder addViewHolder, final int i) {
        addViewHolder.textView.setText(items.get(i).getName());
        addViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(items.get(i).getName());
                position = i;
                myClickListener.OnClick(v);
            }
        });
        addViewHolder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                position = i;
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public void update(boolean isOftenUse){
        int i = 0; //false
        if(isOftenUse)
            i  = 1;//true
        if(items !=null)
            items.clear();
        else
            items = new ArrayList<>();
        items.addAll( LitePal.order("name")
                .where("isOftenUse = ?",String.valueOf(i))
                .find(ItemName.class));
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull AddViewHolder holder) {
        holder.view.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
    public void setOftenUse(boolean flag){
        ItemName itemName = items.get(position);
        itemName.setOftenUse(flag);
        itemName.save();
        update(!flag);
    }
    class AddViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public View view;
        public AddViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    ItemName itemName = items.get(position);
                    menu.setHeaderTitle(itemName.getName());
                    if (itemName.isOftenUse()) {
                        menu.add(0,1,0,"移除该项");
                    }else{
                        menu.add(0,2,0,"加入常用项目");
                    }
                }
            });
            textView = (TextView) itemView.findViewById(R.id.add2_itemname);
        }
    }
}