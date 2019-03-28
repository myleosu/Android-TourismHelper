package com.example.showMap.MainActivities.LogInAndRegister;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.MainActivities.FirstActivityShowMap;
import com.example.showMap.R;

/**
 * Created by asus on 2019/3/28.
 */

public class HasLogIn extends Activity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.haslogin_layout);
        Button Logout_button = (Button) findViewById(R.id.hasbeenLogin_Button_on_Logout);
        EditText editText = (EditText) findViewById(R.id.hasLogin_username_edit);
        editText.setText("当前用户："+FirstActivityShowMap.username);
        Logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //响应点击注销按钮响应
                //清楚userdata_sp文件数据
                SharedPreferences sharedPreferences = getSharedPreferences("userdata_sp",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                FirstActivityShowMap.username = null;
                FirstActivityShowMap.password = null;
                //将FirstActivity从Activity堆栈中移除
                AppManager.getInstance().killActivity(FirstActivityShowMap.class);
                //启动Login界面
                startActivity(new Intent(HasLogIn.this,LogIn.class));
            }
        });
    }
}
