package com.example.showMap.MainActivities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.R;

public class Setting extends Activity {
    private boolean isFirstSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.setting);

        //将提醒设置的数据储存到SharedPreferences,文件名为:wakeupData
        final SharedPreferences.Editor editor = getSharedPreferences("wakeupData",MODE_PRIVATE).edit();
        final SharedPreferences sharedPreferences = getSharedPreferences("wakeupData",MODE_PRIVATE);
        final EditText editTextTime = (EditText)findViewById(R.id.editText_time);
        final EditText editTextMile = (EditText)findViewById(R.id.editText_mile);

        //查找SharedPreferences的设置
        editTextTime.setText(String.valueOf(sharedPreferences.getInt("wakeupTime",10)));//如果查找不到则返回10
        editTextMile.setText(String.valueOf(sharedPreferences.getInt("wakeupMile",5)));//如果查找不到则返回10

        //监听editText中的文本的改变状态,设置输入无法超过最大值
        final int numSmall = 1,numBig = 20;
        editTextTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start > 1) {
                    if (numSmall != -1 && numBig != -1) {
                        int num = Integer.parseInt(s.toString());
                        if (num > 20) {
                            s = "20";
                            Toast.makeText(Setting.this,"最多设置提前20分钟提醒",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if(start==0){
                    s="1";
                }
            }

            //改变后的状态
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals("")) {
                    if (numSmall != -1 && numBig != -1) {//最大值和最小值自设
                        int a = 0;
                        try {
                            a = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            // TODO Auto-generated catch block
                            a = 0;
                        }
                        if (a > 20)
                            editTextTime.setText("20");
                    }
                }
                if(editTextTime.getText().toString().equals("")){
                    editTextTime.setText("1");
                }
                editor.putInt("wakeupTime",Integer.parseInt(editTextTime.getText().toString()));
                if(editor.commit()){
                }
                else {
                    Log.e("sharedPreferences","sharedPreferences commit error");
                }
            }
        });
        editTextMile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start > 1) {
                    if (numSmall != -1 && numBig != -1) {
                        int num = Integer.parseInt(s.toString());
                        if (num > 20) {
                            s = "20";
                            Toast.makeText(Setting.this,"最多设置距离20公里提醒",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            //改变后的状态
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals("")) {
                    if (numSmall != -1 && numBig != -1) {//最大值和最小值自设
                        int a = 0;
                        try {
                            a = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            // TODO Auto-generated catch block
                            a = 0;
                        }
                        if (a > 20)
                            editTextMile.setText("20");
                    }
                }
                if(editTextMile.getText().toString().equals("")){
                    editTextMile.setText("1");
                }
                editor.putInt("wakeupMile",Integer.parseInt(editTextMile.getText().toString()));
                if(editor.commit()){
                }
                else {
                    Log.e("sharedPreferences","sharedPreferences commit error");
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }
}
