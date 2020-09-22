package com.ccg.toolview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
/**
 * @author caocaigui
 * 2020-09-08
 */
public class ToolCircleView extends View {
    private static final String TAG = "ToolCircleView";


    private Bitmap mBitmap;
    //图片资源id
    private int mSrcResourceId;
    //要显示的圆形图片大小
    private float mSrcBmpSize = 100;
    //外圈圆环的宽度
    private float strokeWidth = 5;
    //外圈圆环的颜色
    private int strokeColor = Color.GRAY;

    private Paint mPaint;

    private final int defaultPadding = 5;

    public ToolCircleView(Context context) {
        this(context,null);
    }

    public ToolCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ToolCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ToolCircleView);
        mSrcResourceId = array.getResourceId(R.styleable.ToolCircleView_toolCircleImage,0);
        mSrcBmpSize = array.getDimension(R.styleable.ToolCircleView_toolCircleImageSize,100);
        strokeWidth = array.getDimension(R.styleable.ToolCircleView_toolCircleStrokeWidth,5);
        strokeColor = array.getInt(R.styleable.ToolCircleView_toolCircleStrokeColor,Color.GRAY);
        array.recycle();

        init();
    }


    private void init(){
        //关闭掉硬件加速，否则无法绘制圆形
        setLayerType(LAYER_TYPE_HARDWARE,null);
        if (mPaint == null){
            mPaint = new Paint();
        }
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
    }


    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public void setSrcBmpSize(float mSrcBmpSize) {
        this.mSrcBmpSize = mSrcBmpSize;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getCurrentHeight(){
        return  mSrcBmpSize;
    }

    public float getCurrentWidth(){
        return mSrcBmpSize;
    }

    //
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) mSrcBmpSize,(int) mSrcBmpSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas){
        if (mBitmap == null){
            mBitmap = getScaleBitmap();
        }
        if (mBitmap != null && !mBitmap.isRecycled()){
            float cx = mSrcBmpSize/2.0f;
            float cy = mSrcBmpSize/2.0f;
            float fillRadus = mSrcBmpSize/2-strokeWidth-defaultPadding-3;
            float strokeRadus = mSrcBmpSize/2-defaultPadding;

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(cx,cy,strokeRadus,mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.RED);
            //显示特效，圆形
            canvas.drawCircle(cx,cy,fillRadus,mPaint);
            //设置模式为：显示背景层与上景层的重叠，且显示上层图像
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            //给绘制要显示的图像
            canvas.drawBitmap(mBitmap,null,new RectF(cx-strokeRadus,0,cx+strokeRadus,mSrcBmpSize),mPaint);
            //重置xfermode
            mPaint.setXfermode(null);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(strokeColor);
            mPaint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(cx,cy,strokeRadus,mPaint);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent: " +event.getAction());
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            Log.e(TAG, "onTouchEvent: move" );
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e(TAG, "dispatchTouchEvent: " );
        boolean isResult = false;
        if ( onTouchEvent(event)){
            isResult = true;
        }else{
            isResult = false;
        }
        Log.e(TAG, "dispatchTouchEvent: "+isResult );
        return isResult;
    }

    /**
     * 获取缩放后的图片
     * @return
     */
    private Bitmap getScaleBitmap(){
        if (mSrcResourceId != 0){
//            mBitmap = BitmapFactory.decodeResource(getResources(),mSrcResourceId);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(),mSrcResourceId,options);
            options.inSampleSize = calcScaleSize(options,(int)mSrcBmpSize,(int) mSrcBmpSize);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(getResources(),mSrcResourceId,options);
        }
        return null;
    }

    /**
     * 图片缩放大小
     * @param options
     * @param width
     * @param height
     * @return
     */
    private int calcScaleSize(BitmapFactory.Options options,int width,int height){
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int sampleSize =1 ;
        while ((outWidth = outWidth >> 1) > width && (outHeight= outHeight >> 1) > height){
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

}
