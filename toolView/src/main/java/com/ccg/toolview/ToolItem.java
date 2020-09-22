package com.ccg.toolview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * @author caocaigui
 * 2020-09-08
 */
public class ToolItem extends View {
    private static final String TAG = "ToolItem";

    public static final int RETURN = 1;
    public static final int HOME = 2;
    public static final int MENU = 3;

    private int bitmapColor;

    private int bitmapType;

    private float bitmapSize;

    private String text;

    private float textSize;

    private int textColor;

    private int selectedColor;
    private int unselectedColor;

    private int textMargin = 5;
    private int defaultPadding = 10;

    private Paint paint;
    private int measureWidth;
    private int measureHeight;
    private boolean isPressed = false;


    public ToolItem(Context context) {
        this(context,null);
    }

    public ToolItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ToolItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ToolItem);
        bitmapType = array.getInt(R.styleable.ToolItem_itemImageType,0);
        bitmapSize =array.getDimension(R.styleable.ToolItem_itemImageSize,50);
        bitmapColor = array.getColor(R.styleable.ToolItem_itemImageColor,Color.BLACK);
        text = array.getString(R.styleable.ToolItem_itemText);
        textSize = array.getDimension(R.styleable.ToolItem_itemTextSize,12);
        textColor = array.getInt(R.styleable.ToolItem_itemTextColor,Color.BLACK);
        selectedColor = array.getColor(R.styleable.ToolItem_itemSelectedColor,Color.GRAY);
        unselectedColor = array.getColor(R.styleable.ToolItem_itemUnselectedColor,Color.WHITE);
        array.recycle();

        Log.e(TAG, "ToolItem: item->text size:"+textSize+",bitmapSize:"+ bitmapSize);

        init();
    }

    private void init(){
        if (paint == null){
            paint = new Paint();
        }

        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
    }

    public int getBitmapType() {
        return bitmapType;
    }

    public void setBitmapType(int bitmapType) {
        this.bitmapType = bitmapType;
    }

    public void setBitmapSize(int bitmapSize) {
        this.bitmapSize = bitmapSize;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getCurrentHeight(){
        int textHeight = 0;
        if (text != null && text.length() > 0){
            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            textHeight = rect.height();
        }
        int height = (int) Math.max(bitmapSize,textHeight);
        return height;
    }

    public int getCurrentWidth(){
        int textWidth = 0;
        if (text != null && text.length() > 0){
            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            textWidth = rect.width();
        }
        int width = (int) (bitmapSize + textMargin + textWidth +defaultPadding*2);

        return width;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        measureWidth = 0;
        measureHeight = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        } else {
            int textWidth = 0;
            if (text != null && text.length() > 0){
            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
                textWidth = rect.width();
            }
            measureWidth += textWidth;
            measureWidth += bitmapSize;
            measureWidth += textMargin;
            measureWidth += defaultPadding*2;
        }

        if (heightMode == MeasureSpec.EXACTLY){
            measureHeight = heightSize;
        }else{
            int textHeight = 0;
            if (text != null && text.length() > 0){
                Rect rect = new Rect();
                paint.getTextBounds(text, 0, text.length(), rect);
                textHeight = rect.height();
            }
            measureHeight += Math.max(textHeight,bitmapSize);
            measureHeight += defaultPadding*2;
        }

        setMeasuredDimension(measureWidth,measureHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //先画背景色
        //如果当前view处于按下状态，那么需要绘制按下状态的颜色背景
        RectF rectF = new RectF(0,0,measureWidth,measureHeight);
        if (isPressed){
            paint.setColor(selectedColor);
        }else{
            paint.setColor(unselectedColor);
        }
        canvas.drawRect(rectF,paint);
        //先画图标
        drawBitmap(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        if (text != null && text.length() > 0){
            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            int textHeight = rect.height();
            paint.setColor(textColor);
            float y = measureHeight/2.0f+textHeight/2.0f-defaultPadding/2.0f;
            canvas.drawText(text,bitmapSize+textMargin,y,paint);
        }

    }

    private void drawBitmap(Canvas canvas){

        float topPadding = (measureHeight - bitmapSize) / 2.0f;
        paint.setColor(bitmapColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        if (bitmapType == RETURN){
            Path path = new Path();
            path.moveTo(5,bitmapSize/2+topPadding);
            path.lineTo(bitmapSize-5,topPadding+5);
            path.lineTo(bitmapSize-5,bitmapSize+topPadding-5);
            path.close();
            canvas.drawPath(path,paint);
        }else if (bitmapType == HOME){
            canvas.drawCircle(bitmapSize/2.0f,measureHeight/2.0f,bitmapSize/2.0f-5,paint);
        }else if (bitmapType == MENU){
            canvas.drawRoundRect(new RectF(5,topPadding+5,bitmapSize-5,bitmapSize+topPadding-5),2,2,paint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        Log.e(TAG, "onTouchEvent: "+event.getAction()+","+x+"x"+y );
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            isPressed = true;
            invalidate();
            Log.e(TAG, "onTouchEvent: down" );
        }
        else if (event.getAction() == MotionEvent.ACTION_UP){
            Log.e(TAG, "onTouchEvent: up" );
            performClick();
            return true;
        }else{
            isPressed = false;
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
