package com.example.showMap;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

public class Register extends AppCompatActivity {

//    static int LOGIN_FAILED = 0;
//    static int LOGIN_SUCCEEDED = 1;
    static int REGISTER_FAILED = 2;
    static int REGISTER_SUCCEEDED = 3;
    Dialog dialog;
    Handler handler;

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

                if(isConnectIntent()){
                    if(username.equals("") == true)
                        Toast.makeText(Register.this,"用户名不能为空!",Toast.LENGTH_SHORT).show();
                    else if(userpsd.equals("") == true)
                        Toast.makeText(Register.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
                    else if(userpsd.equals(userpsd_again) == false)
                        Toast.makeText(Register.this,"输入的两次密码不一致!",Toast.LENGTH_SHORT).show();
                    else{
//                    //sql插入语句
//                    String insertusertable = "insert into usertable(username,userpsd) values(?,?)";
//                    //向数据库的usertable表插入user数据
//                    MyDataBase.myDataBaseOpenHelper = new MyDataBase(Register.this,"ApplicationDataBase.db",null,1);
//                    SQLiteDatabase db = MyDataBase.myDataBaseOpenHelper.getWritableDatabase();
//                    db.execSQL(insertusertable,new String[]{register_username.getText().toString(),register_userpsd.getText().toString()});
//                    Toast.makeText(Register.this,"用户注册成功！",Toast.LENGTH_SHORT).show();
                        dialog = new Dialog(Register.this);
                        dialog.setTitle("正在注册，请稍后");
                        dialog.setCancelable(false);
                        dialog.show();
                        new RegisterPostThread(username,userpsd).start();
                    }
                }else{
                    Toast.makeText(Register.this,"网络连接不可用，请检查当前网络状态~",Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Handle,Msg返回成功信息，跳转至其他Activity
        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                dialog.dismiss();
                if(msg.what == 222){
                    //处理返回线程返回的信息
                    if(msg.obj.toString().equals("SUCCEEDED")){
                        //跳转
                        Toast.makeText(Register.this,"注册成功，模拟跳转中",Toast.LENGTH_SHORT).show();
                    }else if(msg.obj.toString().equals("FAILED")){
                        Toast.makeText(Register.this,"注册失败，账号已被注册",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Register.this,"注册失败，服务器出了点问题",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    public class RegisterPostThread extends Thread{
        private String username,userpsd;

        public RegisterPostThread(String username, String userpsd){
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
                responseInt = RegisterPostService.send(params);
                Log.i("tag","LoginActivity:responseInt = " + responseInt);
                //准备发送消息
                Message msg = handler.obtainMessage();
                //设置消息默认值
                msg.what = 222;
                //服务器返回信息的判断和处理
                if(responseInt == REGISTER_SUCCEEDED){
                    msg.obj = "SUCCEEDED";
                }else if(responseInt == REGISTER_FAILED){
                    msg.obj = "FAILED";
                }else{
                    Log.i("tag","LogInAcitivity:msg.obj 错误");
                }
                handler.sendMessage(msg);
            }
        }
    }

    //检查网络状态
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

