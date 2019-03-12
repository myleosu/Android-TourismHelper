package com.example.showMap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by asus on 2019/3/1.
 */

public class LogIn extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //隐藏标题栏
        this.getSupportActionBar().hide();

        //获取编辑框实例
        final EditText login_username = (EditText) findViewById(R.id.login_username_edit);
        final EditText login_userpsd = (EditText) findViewById(R.id.login_password_edit);

        //设置登录按钮监听事件
        Button login_button = (Button) findViewById(R.id.Login_Button_on_login);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectusertable = "select * from usertable where username = ? and userpsd = ?";
                String username = login_username.getText().toString();
                String userpsd = login_userpsd.getText().toString();
                if(username.equals(""))
                    Toast.makeText(LogIn.this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                else if (userpsd.equals(""))
                    Toast.makeText(LogIn.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
                else {
                    MyDataBase.myDataBaseOpenHelper = new MyDataBase(LogIn.this, "ApplicationDataBase.db", null, 1);
                    SQLiteDatabase db = MyDataBase.myDataBaseOpenHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery(selectusertable,new String[]{username,userpsd});
                    //判断数据库返回值是否为空
                    if (cursor.getCount() != 0) {
                        Toast.makeText(LogIn.this, "登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LogIn.this, "登录失败，没有此用户或密码不正确!", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                }
            }
        });

        //设置注册按钮监听事件
        Button register_button = (Button) findViewById(R.id.register_Button_on_login);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动注册按钮界面
                startActivity(new Intent(LogIn.this,Register.class));
            }
        });
    }
}
