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
import android.text.format.DateUtils;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowItemGroupDecoration extends RecyclerView.ItemDecoration {
    private DecorationCallback callback;
    private TextPaint textPaint;
    private Paint paint;
    private int topHead;
    private SimpleDateFormat format;

    public ShowItemGroupDecoration(Context context, DecorationCallback decorationCallback) {
        this.callback = decorationCallback;
        format = new SimpleDateFormat("yyyy年M月d日");
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.bg_header));
        textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        topHead = context.getResources().getDimensionPixelSize(R.dimen.head_top);
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
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            String textLine = format.format(callback.getDate(position));
            if (TextUtils.isEmpty(textLine)) {
                return;
            }
            if (position == 0 || isHeader(position)) {
                float top = view.getTop() - topHead;
                float bottom = view.getTop();
                //绘制矩形
                Rect rect = new Rect(left, (int) top, right, (int) bottom);
                c.drawRect(rect, paint);
                //绘制文字基线，文字的的绘制是从绘制的矩形底部开始的
                Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                float baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                textPaint.setTextAlign(Paint.Align.CENTER);//文字居中
                //绘制文本
                c.drawText(textLine, rect.centerX(), baseline, textPaint);
            }
        }
    }


    private boolean isHeader(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String preData = format.format(callback.getDate(pos - 1));
            String data = format.format(callback.getDate(pos));
            return !preData.equals(data);
        }
    }

    public interface DecorationCallback {
        Date getDate(int position);
    }
}
