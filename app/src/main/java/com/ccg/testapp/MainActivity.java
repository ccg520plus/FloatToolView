package com.ccg.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ccg.toolview.MovePanelLayout;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    //判断是否显示了工具栏
    private boolean isDisplay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.show_tool_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isDisplay = true;
                showFloat();
            }
        });

    }

    /**
     * 屏幕发生旋转时，会销毁掉当前activity，然后重新创建一个新的activity
     * 屏幕发生旋转时，在要销毁activity时，会触发该方法，用于缓存当前activity的状态内容
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e(TAG, "onSaveInstanceState: "+isDisplay );
        outState.putBoolean("isDisplay",isDisplay);
        super.onSaveInstanceState(outState);
    }

    /**
     * 屏幕发生旋转后，重新创建的activity，会调用该方法，用于取出缓存的数据，以便恢复原来显示的内容
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isDisplay = savedInstanceState.getBoolean("isDisplay");
        Log.e(TAG, "onRestoreInstanceState: "+isDisplay );
        if (isDisplay){
            showFloat();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFloat(){

        MovePanelLayout movePanelLayout = findViewById(R.id.movelayout);
        movePanelLayout.setOnToolItemClickListener(new MovePanelLayout.OnToolItemClickListener() {
            @Override
            public void onToolItemClick(View view) {
                int id = view.getId();
                Log.e(TAG, "onToolItemClick: " );
                if (id == R.id.tool_item_return) {
                    Toast.makeText(MainActivity.this,"返回",Toast.LENGTH_SHORT).show();
                } else if (id == R.id.tool_item_hide) {
                    Toast.makeText(MainActivity.this,"隐藏",Toast.LENGTH_SHORT).show();
                } else if (id == R.id.tool_item_menu) {
                    Toast.makeText(MainActivity.this,"菜单",Toast.LENGTH_SHORT).show();
                }
            }
        });
        movePanelLayout.requestRefresh();

    }

}
