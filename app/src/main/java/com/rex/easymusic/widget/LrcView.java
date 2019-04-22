package com.rex.easymusic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.rex.easymusic.Bean.Lrc;

import java.util.ArrayList;
import java.util.List;

public class LrcView extends View {
    private Paint NormalPaint;
    private Paint CurrentPaint;
    private int width = 0, height = 0;
    private int PaddingHeight=150;
    private final String TAG="LrcView";
    private List<Lrc> mlrcList=new ArrayList<>();
    private int currentPosition=-1;
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
        try {
            mlrcList=lrcList;
            requestLayout();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setCurrentPosition(int currentPosition){
        this.currentPosition=currentPosition;
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
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        if (mlrcList!=null)
            setMeasuredDimension(widthSize,mlrcList.size()*PaddingHeight+8*PaddingHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width == 0 || height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
//        Log.e(TAG, "mlrcList.size: "+mlrcList.size() );
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
            if (currentPosition!=-1&&i==currentPosition)
                canvas.drawText(mlrcList.get(i).text,width/2,((i+5)*PaddingHeight),CurrentPaint);
            else
                canvas.drawText(mlrcList.get(i).text,width/2,((i+5)*PaddingHeight),NormalPaint);
        }
    }
}
