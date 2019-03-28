package com.example.showMap.ActicityTools;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.ClientHttpService.DelectPostService;
import com.example.showMap.ClientHttpService.GethistoryPostService;
import com.example.showMap.ClientHttpService.InserthistoryPostService;
import com.example.showMap.ClientHttpService.LoginPostService;
import com.example.showMap.ClientHttpService.RegisterPostService;
import com.example.showMap.ClientHttpService.UpdatePostService;
import com.example.showMap.MainActivities.FirstActivityShowMap;
import com.example.showMap.MainActivities.HistoryActivity.History;
import com.example.showMap.MainActivities.HistoryActivity.TripThoughts;
import com.example.showMap.MainActivities.LogInAndRegister.LogIn;
import com.example.showMap.MainActivities.LogInAndRegister.Register;
import com.example.showMap.MainActivities.ShowInfoActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by asus on 2019/3/28.
 */

/**
 * Activity工具管理类
 */
public class MyActivityTools {
    /**
     * 当前登录用户的用户名与密码
     */
    public static String currentusername = null;
    public static String currentpassword = null;
    /**
     * 服务端返回的数字意义
     */
    private static int LOGIN_FAILED = 0;
    private static int LOGIN_SUCCEEDED = 1;
    private static int REGISTER_FAILED = 2;
    private static int REGISTER_SUCCEEDED = 3;
    private static int HISTORY_SUCCEEDED = 5;
    private static int HISTORY_FAILED = 4;

    /**
     * 检查是否连接上网络
     * @param context
     * @return
     */
    public static boolean isConnectIntent(Context context){
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 检查用户是否已经登录
     * @param context
     * @return
     */
    public static boolean ishas_login(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdata_sp",MODE_PRIVATE);
        String username = sharedPreferences.getString("username",null);
        String password = sharedPreferences.getString("password",null);
        if(username != null && password != null && !username.equals("") && !password.equals("")){
            currentusername = username;
            currentpassword = password;
            return true;
        }
        return false;
    }

    /**
     * 线程回调监听器
     */
    /**
     * 111 LogInPostThread
     * 222 RegisterPostThread
     * 333 GetHistoryPostThread
     * 444 InsertHistoryPostThread
     * 555 DeleteHistoryPostThread
     * 666 SaveHistoryPostThread
     */
    public static Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("tag:","收到消息,msg="+String.valueOf(msg.what));
            switch (msg.what) {
                case 111:
                    LogIn.resolve_Login_Handler(msg);
                    break;
                case 222:
                    Register.resolve_Register_Handler(msg);
                    break;
                case 333:
                    History.resolve_Gethistory_Handler(msg);
                    break;
                case 444:
                    ShowInfoActivity.resolve_Gethistory_Handler(msg);
                    break;
                case 555:
                    TripThoughts.resolve_DeleteHandler(msg);
                    break;
                case 666:
                    TripThoughts.resolve_SaveHandler(msg);
                    break;
                default:
            }
        }
    };

    /**
     * 启动LogIn线程
     */
    public static class LogInPostThread extends Thread{
        private String username;
        private String password;
        public LogInPostThread(String username, String userpsd){
            this.username = username;
            this.password= userpsd;
        }

        @Override
        public void run() {
            //Sevice传回int
            int responseInt = 0;
            if(!username.equals("")){
                //要发送的数据
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username));
                params.add(new BasicNameValuePair("password",password));
                //发送数据，获取对象
                responseInt = LoginPostService.send(params);
                Log.i("tag","LoginPostThread:responseInt = " + responseInt);
                //准备发送消息
                Message msg = myhandler.obtainMessage();
                //设置消息默认值
                msg.what = 111;
                //服务器返回信息的判断和处理
                if(responseInt == LOGIN_SUCCEEDED){
                    msg.obj = "SUCCEEDED";
                }else if(responseInt == LOGIN_FAILED){
                    msg.obj = "FAILED";
                }else{
                    Log.i("tag","LogIntPostThread:msg.obj 错误");
                }
                myhandler.sendMessage(msg);
            }
        }
    }

    /**
     * 启动Register线程
     */
    public static class RegisterPostThread extends Thread{
        private String username,userpsd;

        public RegisterPostThread(String username, String userpsd){
            this.username = username;
            this.userpsd = userpsd;
        }

        @Override
        public void run() {
            //Sevice传回int
            int responseInt = REGISTER_FAILED;
            if(!username.equals("")){
                //要发送的数据
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username));
                params.add(new BasicNameValuePair("password",userpsd));
                //发送数据，获取对象
                responseInt = RegisterPostService.send(params);
                Log.i("tag","RegisterPostThread:responseInt = " + responseInt);
                //准备发送消息
                Message msg = myhandler.obtainMessage();
                //设置消息默认值
                msg.what = 222;
                //服务器返回信息的判断和处理
                if(responseInt == REGISTER_SUCCEEDED){
                    msg.obj = "SUCCEEDED";
                }else if(responseInt == REGISTER_FAILED){
                    msg.obj = "FAILED";
                }else{
                    Log.i("tag","ResgiterPostThread:msg.obj 错误");
                }
                myhandler.sendMessage(msg);
            }
        }
    }

    /**
     * 启动GetHistory线程
     */
    public static class GethistoryPostThread extends Thread{
        private String username,userpsd;

        public GethistoryPostThread(String username, String userpsd){
            this.username = username;
            this.userpsd = userpsd;
        }

        @Override
        public void run() {
            //Sevice传回JSON
            String responsejson = "FAILED";
            if(!username.equals("")){
                //要发送的数据
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username));
                params.add(new BasicNameValuePair("password",userpsd));
                //发送数据，获取对象
                responsejson = GethistoryPostService.send(params);
                Log.i("tag","GetHistoryPostThread:responsejson = " + responsejson);
                //准备发送消息
                Message msg = myhandler.obtainMessage();
                //设置消息默认值
                msg.what = 333;
                //服务器返回信息的判断和处理
                if(!responsejson.equals("FAILED")){
                    msg.obj = responsejson;
                }else{
                    msg.obj = responsejson;
                    Log.i("tag", "GetHistoryPostThread:msg.obj 错误");
                }
                myhandler.sendMessage(msg);
            }
        }
    }

    /**
     * 启动InsertHistory线程
     */
    public static class InserthistoryPostThread extends Thread{
        private String username,password,historyjson;

        public InserthistoryPostThread(String username,String password,String historyjson){
            this.username = username;
            this.password = password;
            this.historyjson = historyjson;
        }

        @Override
        public void run() {
            //Sevice传回result数字
            int responseint = HISTORY_FAILED;
            if(!username.equals("")){
                //要发送的数据
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username));
                params.add(new BasicNameValuePair("password",password));
                params.add(new BasicNameValuePair("history",historyjson));
                //发送数据，获取对象
                responseint = InserthistoryPostService.send(params);
                Log.i("tag","InserthistoryThread:responseint = " + responseint);
                //准备发送消息
                Message msg = myhandler.obtainMessage();
                //设置消息默认值
                msg.what = 444;
                //服务器返回信息的判断和处理
                if(responseint == HISTORY_SUCCEEDED){
                    msg.obj = "SUCCEEDED";
                }else if (responseint == HISTORY_FAILED){
                    msg.obj = "FAILED";
                }else{
                    Log.i("tag","InsertthistoryThread:msg.obj 错误");
                }
                myhandler.sendMessage(msg);
            }
        }
    }

    /**
     * 启动DeleteHistory线程
     */
    public static class DeletePostThread extends Thread{

        private String username;
        private String password;
        private int id;
        public DeletePostThread(String username,String password,int id){
            this.username = username;
            this.password = password;
            this.id = id;
        }

        @Override
        public void run() {
            int responseInt = HISTORY_FAILED;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username",username));
            params.add(new BasicNameValuePair("password",password));
            params.add(new BasicNameValuePair("id",String.valueOf(id)));
            //发送数据，获取对象
            responseInt = DelectPostService.send(params);
            Log.i("tag","DeletePostThread:responseInt = " + responseInt);
            //准备发送消息
            Message msg = myhandler.obtainMessage();
            //设置消息默认值
            msg.what = 555;
            //服务器返回信息的判断和处理
            if(responseInt == HISTORY_SUCCEEDED){
                msg.obj = "SUCCEEDED";
            }else if(responseInt == HISTORY_FAILED){
                msg.obj = "FAILED";
            }else{
                Log.i("tag","DeletePostThread:msg.obj 错误");
            }
            myhandler.sendMessage(msg);
        }
    }

    /**
     * 启动SaveHistory线程
     */
    public static class SavePostThread extends Thread{
        private String username;
        private String password;
        private int id;
        private String memory;
        public SavePostThread(String username,String password,int id,String memory){
            this.username = username;
            this.password = password;
            this.id = id;
            this.memory = memory;
        }

        @Override
        public void run() {
            int responseInt = HISTORY_FAILED;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username",username));
            params.add(new BasicNameValuePair("password",password));
            params.add(new BasicNameValuePair("id",String.valueOf(id)));
            params.add(new BasicNameValuePair("memory",memory));
            //发送数据，获取对象
            responseInt = UpdatePostService.send(params);
            Log.i("tag","TriThoughtActivity:responseInt = " + responseInt);
            //准备发送消息
            Message msg = myhandler.obtainMessage();
            //设置消息默认值
            msg.what = 666;
            //服务器返回信息的判断和处理
            if(responseInt == HISTORY_SUCCEEDED){
                msg.obj = "SUCCEEDED";
            }else if(responseInt == HISTORY_FAILED){
                msg.obj = "FAILED";
            }else{
                Log.i("tag","TriThoughtAcitivity:msg.obj 错误");
            }
            myhandler.sendMessage(msg);
        }
    }
}
