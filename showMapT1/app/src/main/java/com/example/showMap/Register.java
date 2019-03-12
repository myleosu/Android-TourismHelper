package com.example.showMap;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by asus on 2019/3/1.
 */

public class Register extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //隐藏标题栏
        this.getSupportActionBar().hide();


        //获得编辑框实例
        final EditText register_username = (EditText) findViewById(R.id.register_username_edit);
        final EditText register_userpsd = (EditText) findViewById(R.id.register_password_edit);
        final EditText register_userpsd_again = (EditText) findViewById(R.id.register_password_edit_again);

        //设置注册按钮监听事件
        final Button register_button = (Button) findViewById(R.id.register_button_on_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = register_username.getText().toString();
                String userpsd = register_userpsd.getText().toString();
                String userpsd_again = register_userpsd_again.getText().toString();

                if(username.equals("") == true)
                    Toast.makeText(Register.this,"用户名不能为空!",Toast.LENGTH_SHORT).show();
                else if(userpsd.equals("") == true)
                    Toast.makeText(Register.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
                else if(userpsd.equals(userpsd_again) == false)
                    Toast.makeText(Register.this,"输入的两次密码不一致!",Toast.LENGTH_SHORT).show();
                else{
                    //sql插入语句
                    String insertusertable = "insert into usertable(username,userpsd) values(?,?)";
                    //向数据库的usertable表插入user数据
                    MyDataBase.myDataBaseOpenHelper = new MyDataBase(Register.this,"ApplicationDataBase.db",null,1);
                    SQLiteDatabase db = MyDataBase.myDataBaseOpenHelper.getWritableDatabase();
                    db.execSQL(insertusertable,new String[]{register_username.getText().toString(),register_userpsd.getText().toString()});
                    Toast.makeText(Register.this,"用户注册成功！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
