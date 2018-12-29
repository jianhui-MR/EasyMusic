package com.bobby.musiczone.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bobby.musiczone.entry.Lrc;
import com.bobby.musiczone.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class LrcView extends View {
    private Paint NormalPaint;
    private Paint CurrentPaint;
    private int width = 0, height = 0;
    private int PaddingHeight=150;
    private final String TAG="LrcView";
    private List<Lrc> mlrcList=new ArrayList<>();
    public LrcView(Context context)
    {
        this(context,null);
    }
    public LrcView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }
    @SuppressLint("HandlerLeak")
    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(attrs);
    }

    public void setLrcList(List<Lrc> lrcList)
    {
        mlrcList=lrcList;
        requestLayout();
    }
    private void initPaint(AttributeSet attrs)
    {
        NormalPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        NormalPaint.setColor(Color.parseColor("#cdcdcd"));
        NormalPaint.setTextSize(42);
        NormalPaint.setTextAlign(Paint.Align.CENTER);

        CurrentPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        CurrentPaint.setColor(Color.WHITE);
        CurrentPaint.setTextSize(42);
        CurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthsize=MeasureSpec.getSize(widthMeasureSpec);
        if (mlrcList!=null)
            setMeasuredDimension(widthsize,mlrcList.size()*PaddingHeight+8*PaddingHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width == 0 || height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
        if (mlrcList==null||mlrcList.size() == 0) {
            canvas.drawText("暂无歌词", width / 2, height / 2, CurrentPaint);
            return;
        }
        drawLrc(canvas);
    }
    private void drawLrc(Canvas canvas)
    {
        int i;
        for(i=0;i<mlrcList.size();i++)
        {
            canvas.drawText(mlrcList.get(i).text,width/2,((i+5)*PaddingHeight),NormalPaint);
        }
    }
}
