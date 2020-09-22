package com.ccg.toolview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class MovePanelLayout extends ViewGroup implements ToolsLayout.OnClickToolResultListener {
    private static final String TAG = "MoveLayout";

    private static final int TOOL_MOVE = 1;
    private static final int TOOL_HIDE = 2;
    private static final int TOOL_REMOVE = 3;
    private static final int TOOL_SHOW = 4;

    private static final int TOOL_LEFT = 5;
    private static final int TOOL_RIGHT = 6;
    private static final int TOOL_OTHER = 7;

    private ToolsLayout moveView;

    private int moveX = 0;
    private int moveY = 0;

    private int lastDownX = 0;
    private int lastDownY = 0;

    private OnToolItemClickListener onToolItemClickListener;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int x = msg.arg1;
            int y = msg.arg2;
            moveX += x;
            moveY += y;
            requestLayout();
        }
    };

    private int currentALign = TOOL_LEFT;
    private int currentStatus = 0;
    //判断是否显示了工具栏
    private boolean isHideLayout = false;

    private Runnable hide = new Runnable() {
        @Override
        public void run() {
            if (moveView != null){
                moveView.setHideTool(!moveView.isHideTool());
                currentStatus = TOOL_HIDE;
                requestLayout();
                handler.postDelayed(remove,5*1000);
            }
        }
    };


    private Runnable remove = new Runnable() {
        @Override
        public void run() {
            if (moveView != null){
                currentStatus = TOOL_REMOVE;
                isHideLayout = true;
                requestLayout();
            }
        }
    };

    public MovePanelLayout(Context context) {
        this(context,null);
    }

    public MovePanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MovePanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFloatView(ToolsLayout view){
        if (moveView != null){
            removeAllViews();
        }
        moveView = view;
        moveView.setOnClickToolResultListener(this);
        addView(moveView);
    }

    public void setOnToolItemClickListener(OnToolItemClickListener onToolItemClickListener) {
        this.onToolItemClickListener = onToolItemClickListener;
    }

    private void init() {
        isHideLayout = false;
        currentStatus = TOOL_HIDE;
        moveX = 0;
        moveY = 51;
    }

    public void requestRefresh(){
        init();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: status->"+currentStatus);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (moveView != null && (currentStatus == TOOL_HIDE || currentStatus == TOOL_SHOW)){
            int screenWidth = getMeasuredWidth();
            Log.d(TAG, "屏幕宽度: "+screenWidth+",tools x:"+moveX);
            int measuredWidth = moveView.getMeasuredWidth();
            if (currentALign == TOOL_LEFT){
                Log.d(TAG, "onMeasure: 左侧");
                moveX = 0;
            }else if (currentALign == TOOL_RIGHT){
                Log.d(TAG, "onMeasure: 右侧");
                moveX = screenWidth - measuredWidth;
            }else{
                if (moveX < screenWidth/2.0f){
                    moveX = 0;
                }else{
                    Log.d(TAG, "scale: "+measuredWidth);
                    moveX = screenWidth - measuredWidth;
                }
            }

        }else if (moveView != null &&  currentStatus == TOOL_REMOVE){
            int measuredWidth = moveView.getMeasuredWidth();
            Log.e(TAG, "run: "+(-measuredWidth/4) );
            int screenWidth = getMeasuredWidth();
            Log.d(TAG, "run: root width :"+screenWidth);
//            float x = moveView.getX();
            if (moveX < screenWidth/2.0f){
                moveX = (-measuredWidth/4);
                currentALign =TOOL_LEFT;
            }else{
                moveX = screenWidth-measuredWidth/2;
                currentALign = TOOL_RIGHT;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0){
            if (moveView != null){
                int measuredHeight = moveView.getMeasuredHeight();
                int measuredWidth = moveView.getMeasuredWidth();
                Log.d(TAG, "onLayout: "+measuredWidth+"x"+measuredHeight);
                moveView.layout(moveX,moveY,moveX+measuredWidth,moveY+measuredHeight);
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e(TAG, "dispatchTouchEvent: " );
        boolean result = false;
        if (onInterceptTouchEvent(ev)){
            Log.w(TAG, "dispatchTouchEvent: true" );
            result = super.dispatchTouchEvent(ev);
        }
        else{
            Log.w(TAG, "dispatchTouchEvent: false" );
            result = onTouchEvent(ev);
        }
        if (!result){
            return super.dispatchTouchEvent(ev);
        }
        Log.e(TAG, "dispatchTouchEvent: "+result );
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        reset();

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            lastDownY = y;
            lastDownX = x;
            currentStatus = TOOL_HIDE;
            Log.e(TAG, "onTouchEvent: down->:"+lastDownX+"x"+lastDownY );
            return false;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            Log.e(TAG, "onTouchEvent: move->x:"+x+",y:"+y );
            int screenHeight = getMeasuredHeight();
            Log.w(TAG, "onTouch: tool y->"+moveY+",max:"+(screenHeight-200) );
            if (moveY <= 50){
                moveY = 51;
                return true;
            }else if (moveY >= screenHeight-200){
                moveY = screenHeight  - 201;
                return true;
            }

            if (currentStatus == TOOL_MOVE || Math.abs(lastDownY - y) >= 5 || Math.abs(lastDownX - x) >= 5){
                Log.e(TAG, "onTouchEvent: 1" );
                currentStatus = TOOL_MOVE;

                int offsetX = x - lastDownX;
                int offsetY = y - lastDownY;

                Message message = new Message();
                message.arg1 = offsetX;
                message.arg2 = offsetY;
                handler.sendMessageDelayed(message,0);

                lastDownY = y;
                lastDownX = x;
                return true;
            }
            lastDownY = y;
            lastDownX = x;
            Log.e(TAG, "onTouchEvent: 2" );
            return false;
        }else{
            Log.e(TAG, "onTouchEvent: up" );
            postHideTool();
            if (currentStatus == TOOL_MOVE){
                if (x < getMeasuredWidth()/2.0f){
                    Log.d(TAG, "onTouchEvent: 左侧");
                    currentALign = TOOL_LEFT;
                }else{
                    Log.d(TAG, "onTouchEvent: 右侧");
                    currentALign = TOOL_RIGHT;
                }
                currentStatus = TOOL_HIDE;
                requestLayout();
                return true;
            }
            currentStatus = TOOL_HIDE;
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void onClickToolResult(View view) {
        int id = view.getId();
        if (moveView != null && id == moveView.getToolCircleViewId()) {
            Log.e(TAG, "onClickToolResult: circle view..."+currentStatus);
            reset();
            if (isHideLayout){
                isHideLayout = false;
                currentStatus = TOOL_HIDE;
                handler.postDelayed(remove, 5 * 1000);
            }else{
                if (moveView != null){
                    if (moveView.isHideTool()){
                        currentStatus = TOOL_SHOW;
                        handler.postDelayed(hide, 5 * 1000);
                    }else{
                        currentStatus = TOOL_HIDE;
                        handler.postDelayed(remove, 5 * 1000);
                    }
                    moveView.setHideTool(!moveView.isHideTool());
                }

            }
            requestLayout();
        }else{
            if (onToolItemClickListener != null){
                onToolItemClickListener.onToolItemClick(view);
            }
        }
    }

    private void reset(){
        handler.removeCallbacks(hide);
        handler.removeCallbacks(remove);
    }

    private void postHideTool(){
        if (moveView != null){
            if (moveView.isHideTool()){
                handler.postDelayed(remove, 5 * 1000);
            }else{
                handler.postDelayed(hide, 5 * 1000);
            }
        }
    }

    public interface OnToolItemClickListener{
        void onToolItemClick(View view);
    }
}
