# FloatToolView
一个可拖动的工具悬浮窗（不需要开启悬浮窗权限）
# 功能：
1、实现三个虚拟按键的悬浮窗效果；
2、可在全屏拖动；
3、会自动贴边隐藏。

# 使用方式
在布局中直接添加MovePanelLayout即可，该布局是工具悬浮窗可拖动的范围。
```
...
    <com.ccg.toolview.MovePanelLayout
     android:id="@+id/movelayout"
     android:layout_width="match_parent"
     android:layout_height="match_parent"/>
...

```
执行如下代码，可对悬浮窗口初始化。
```
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
```
