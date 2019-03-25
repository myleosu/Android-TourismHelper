package com.example.showMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2019/3/1.
 */

public class LogIn extends AppCompatActivity {

    static int LOGIN_FAILED = 0;
    static int LOGIN_SUCCEEDED = 1;
//    static int REGISTER_FAILED = 2;
//    static int REGISTER_SUCCEEDED = 3;
    Handler handler;
    Dialog dialog;

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
                String username = login_username.getText().toString();
                String userpsd = login_userpsd.getText().toString();

                //检查网络信息
                if(isConnectIntent()){
                    if(username.equals(""))
                        Toast.makeText(LogIn.this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                    else if (userpsd.equals(""))
                        Toast.makeText(LogIn.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
                    else {
                        //启动登录Thread
                        dialog = new Dialog(LogIn.this);
                        dialog.setTitle("正在登录，请稍后...");
                        dialog.setCancelable(false);
                        dialog.show();
                        new LoginPostThread(username,userpsd).start();
                    }
                }else{
                    Toast.makeText(LogIn.this,"未连接互联网，请检查网络状态~",Toast.LENGTH_SHORT).show();
                }
//                String selectusertable = "select * from usertable where username = ? and userpsd = ?";
//                if(username.equals(""))
//                    Toast.makeText(LogIn.this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
//                else if (userpsd.equals(""))
//                    Toast.makeText(LogIn.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
//                else {
//                    MyDataBase.myDataBaseOpenHelper = new MyDataBase(LogIn.this, "ApplicationDataBase.db", null, 1);
//                    SQLiteDatabase db = MyDataBase.myDataBaseOpenHelper.getWritableDatabase();
//                    Cursor cursor = db.rawQuery(selectusertable,new String[]{username,userpsd});
//                    //判断数据库返回值是否为空
//                    if (cursor.getCount() != 0) {
//                        Toast.makeText(LogIn.this, "登录成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(LogIn.this, "登录失败，没有此用户或密码不正确!", Toast.LENGTH_SHORT).show();
//                    }
//                    cursor.close();
//                }
            }
        });

        //Handle,Msg返回成功信息，跳转至其他Activity
        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                dialog.dismiss();
                if(msg.what == 111){
                    //处理返回线程返回的信息
                    if(msg.obj.toString().equals("SUCCEEDED")){
                        //跳转
                        Toast.makeText(LogIn.this,"登录成功，模拟跳转中",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LogIn.this,"登录失败，账号与密码不匹配",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

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

    public class LoginPostThread extends Thread{
        private String username,userpsd;

        public LoginPostThread(String username, String userpsd){
            this.username = username;
            this.userpsd = userpsd;
        }

        @Override
        public void run() {
            //Sevice传回int
            int responseInt = 0;
            if(!username.equals("")){
                //要发送的数据
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username));
                params.add(new BasicNameValuePair("password",userpsd));
                //发送数据，获取对象
                responseInt = LoginPostService.send(params);
                Log.i("tag","LoginActivity:responseInt = " + responseInt);
                //准备发送消息
                Message msg = handler.obtainMessage();
                //设置消息默认值
                msg.what = 111;
                //服务器返回信息的判断和处理
                if(responseInt == LOGIN_SUCCEEDED){
                    msg.obj = "SUCCEEDED";
                }else if(responseInt == LOGIN_FAILED){
                    msg.obj = "FAILED";
                }else{
                    Log.i("tag","LogInAcitivity:msg.obj 错误");
                }
                handler.sendMessage(msg);
            }
        }
    }


    public boolean isConnectIntent(){
        ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity!=null){
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null){
                for(int i = 0;i<info.length;i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
