package com.example.itemgrouptest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowItemStickDecoration extends RecyclerView.ItemDecoration {
    private DecorationCallback callback;
    private TextPaint textPaint;
    private Paint paint;
    private int topHead;
    private SimpleDateFormat format;

    public ShowItemStickDecoration(Context context, DecorationCallback decorationCallback) {
        this.callback = decorationCallback;
        format = new SimpleDateFormat("yyyy年M月d日");
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.bg_header));
        textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        topHead = context.getResources().getDimensionPixelSize(R.dimen.head_top);
        this.callback = decorationCallback;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        String data = format.format(callback.getDate(pos));
        if (TextUtils.isEmpty(data)) {
            return;
        }
        //同组的第一个才添加padding
        if (pos == 0 || isHeader(pos)) {
            outRect.top = topHead;
        } else {
            outRect.top = 0;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //获取当前可见的item的数量，不包括分组项，注意区分下面的
        int childCount = parent.getChildCount();
        //获取所有的的item个数,不建议使用Adapter中获取
        int itemCount = state.getItemCount();
        //int left = parent.getPaddingLeft();
        int left = parent.getLeft();
        int right = parent.getRight();
        //int right = parent.getRight() - parent.getPaddingRight();
        String preDate;
        String currentDate = null;
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            String textLine = format.format(callback.getDate(position));
            preDate = currentDate;
            currentDate = format.format(callback.getDate(position));
            if (TextUtils.isEmpty(currentDate) || TextUtils.equals(currentDate, preDate)) {
                continue;
            }
            if (TextUtils.isEmpty(textLine)) {
                continue;
            }
            int viewBottom = view.getBottom();
            float textY = Math.max(topHead, view.getTop());
            //下一个和当前不一样移动当前
            if (position + 1 < itemCount) {
                String nextData = format.format(callback.getDate(position + 1));
                if (!currentDate.equals(nextData) && viewBottom < textY) {//组内最后一个view进入了header
                    textY = viewBottom;
                }
            }
            Rect rect = new Rect(left, (int) textY - topHead, right, (int) textY);
            c.drawRect(rect, paint);
            //绘制文字基线，文字的的绘制是从绘制的矩形底部开始的
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            textPaint.setTextAlign(Paint.Align.CENTER);//文字居中
            //绘制文本
            c.drawText(textLine, rect.centerX(), baseline, textPaint);
        }
    }

    private boolean isHeader(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String preDate = format.format(callback.getDate(pos - 1));
            String currentDate = format.format(callback.getDate(pos));
            return !currentDate.equals(preDate);
        }
    }

    public interface DecorationCallback {
        Date getDate(int position);;
    }
}
