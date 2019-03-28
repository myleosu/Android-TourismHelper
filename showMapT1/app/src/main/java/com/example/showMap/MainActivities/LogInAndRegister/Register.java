package com.example.showMap.MainActivities.LogInAndRegister;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.showMap.ActicityTools.MyActivityTools;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.ClientHttpService.RegisterPostService;
import com.example.showMap.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2019/3/1.
 */

public class Register extends AppCompatActivity {

    private TextView toLogin;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.register);
        //隐藏标题栏
        this.getSupportActionBar().hide();

        //实例化返回登录图标
        toLogin=(TextView)findViewById(R.id.link_to_login);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加下划线
                toLogin.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                startActivity(new Intent(Register.this,LogIn.class));
            }
        });

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
                //检查网络状态
                if(MyActivityTools.isConnectIntent(Register.this)){
                    if(username.equals("") == true)
                        Toast.makeText(Register.this,"用户名不能为空!",Toast.LENGTH_SHORT).show();
                    else if(userpsd.equals("") == true)
                        Toast.makeText(Register.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
                    else if(userpsd.equals(userpsd_again) == false)
                        Toast.makeText(Register.this,"输入的两次密码不一致!",Toast.LENGTH_SHORT).show();
                    else{
                        new MyActivityTools.RegisterPostThread(username,userpsd).start();
                        Handler registerhandler = MyActivityTools.myhandler;
                    }
                }else{
                    Toast.makeText(Register.this,"网络连接不可用，请检查当前网络状态~",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 处理RegisterPostThread线程返回的信息
     * @param msg
     */
    public static void resolve_Register_Handler(Message msg){
        //处理返回线程返回的信息
        final Context registercontext = AppManager.getInstance().getActivitycontext(Register.class);
        if(msg.obj.toString().equals("SUCCEEDED")){
            //跳转
            Toast.makeText(registercontext,"注册成功!",Toast.LENGTH_SHORT).show();
            Handler mhandler = new Handler();
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    intent = new Intent(registercontext,LogIn.class);
                    registercontext.startActivity(intent);
                }
            }, 2000);//2秒后执行Runnable中的run方法
        }else if(msg.obj.toString().equals("FAILED")){
            Toast.makeText(registercontext,"注册失败，账号已被注册",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(registercontext,"注册失败，服务器出了点问题",Toast.LENGTH_SHORT).show();
        }
    }
}

