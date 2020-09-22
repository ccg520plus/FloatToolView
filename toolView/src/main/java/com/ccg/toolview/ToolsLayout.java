package com.ccg.toolview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author caocaigui
 * 2020-09-08
 */
public class ToolsLayout extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "ToolsLayout";


    private int measureWidth;
    private int measureHeight;
    private int toolWidth;
    private int toolPadding = 10;
    private boolean isHideTool = true;//是否隐藏工具栏

    private ToolCircleView toolCircleView;

    private OnClickToolResultListener onClickToolResultListener;

    public ToolsLayout(@NonNull Context context) {
        this(context,null);
    }

    public ToolsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ToolsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setOnClickToolResultListener(OnClickToolResultListener onClickToolResultListener) {
        this.onClickToolResultListener = onClickToolResultListener;
    }

    public void setHideTool(boolean hideTool) {
        isHideTool = hideTool;
        Log.e(TAG, "setHideTool: "+isHideTool);
        requestLayout();
    }

    public boolean isHideTool() {
        return isHideTool;
    }

    int getToolCircleViewId(){
        if (toolCircleView != null){
            return toolCircleView.getId();
        }
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure: " );
        measureWidth = 0;
        measureHeight = 0;
        toolWidth = 0;
        int childCount = getChildCount();
        //计算布局里有多少子view
        if(childCount > 0){
            for (int i = 0 ; i < childCount; i++){
                View child = getChildAt(i);
                child.setOnClickListener(this);
                if (child instanceof ToolCircleView){
                    toolCircleView = (ToolCircleView) child;
                    measureWidth += ((ToolCircleView) child).getMeasuredWidth();
                    toolWidth += ((ToolCircleView) child).getMeasuredWidth()/2;
                    measureHeight = (int) Math.max(((ToolCircleView) child).getMeasuredHeight(),measureHeight);
                }
                if (child instanceof ToolItem && !isHideTool){
                    measureWidth += ((ToolItem) child).getMeasuredWidth();
                    toolWidth += ((ToolItem) child).getMeasuredWidth();
                    measureHeight = (int) Math.max(((ToolItem) child).getMeasuredHeight(),measureHeight);
                }
            }
        }
        measureWidth+= toolPadding*2;
        toolWidth += toolPadding;
        Log.e(TAG, "onMeasure: "+measureWidth+"x"+measureHeight );
        setMeasuredDimension(measureWidth,measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout: isChange:"+changed+",l:"+l+",t:"+t+",r:"+r+",b:"+b);
        int childCount = getChildCount();
        if (childCount > 0){
            int left = toolPadding;
            int top = 0;
            int right = 0;
            int bottom = 0;
            for (int i =0 ; i < childCount; i++){
                View child = getChildAt(i);

                child.setVisibility(VISIBLE);

                if (child instanceof ToolCircleView){
                    top = (int) ((measureHeight - ((ToolCircleView) child).getMeasuredHeight())/2);
                }
                if (child instanceof ToolItem && !isHideTool){
                    top = (int) ((measureHeight - ((ToolItem) child).getMeasuredHeight())/2);
                }else if (child instanceof ToolItem){
                    child.setVisibility(GONE);
                    continue;
                }

                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                int width = child.getMeasuredWidth()+layoutParams.leftMargin + layoutParams.rightMargin;
                int height = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

                left += layoutParams.leftMargin;
                top += layoutParams.topMargin;
                right += width;
                bottom = t + height;

                if (child instanceof ToolCircleView){
                    left -= toolPadding;
                }

                child.layout(left,top,right,bottom);

                if (child instanceof ToolCircleView){
                    left += ((ToolCircleView) child).getMeasuredWidth();
                }
                if (child instanceof ToolItem && !isHideTool){
                    left += ((ToolItem) child).getMeasuredWidth();
                }
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!isHideTool){
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            RectF rectF = new RectF(0,measureHeight*0.1f,toolWidth,measureHeight*0.9f);
            canvas.drawRoundRect(rectF,20,20,paint);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: " );
        if (onClickToolResultListener != null){
            onClickToolResultListener.onClickToolResult(v);
        }
    }

    public interface OnClickToolResultListener{
        void onClickToolResult(View view);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams{

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


}
