package com.example.showMap.MainActivities.HistoryActivity;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.ClientHttpService.GethistoryPostService;
import com.example.showMap.MainActivities.FirstActivityShowMap;
import com.example.showMap.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2019/3/2.
 */

public class History extends AppCompatActivity {

    static public List<History_item> history_item_List = new ArrayList<>();

    private String history_date;
    private String history_startpos;
    private String history_endpos;
    private String history_start_weather;
    private String history_end_weather;
    private String history_totaltime;
    private int history_id;
    private String history_memory;
    Handler handler;
    Dialog dialog;
    private boolean is_returenhistory = false;
    RecyclerView recyclerView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.history_layout);
        Log.i("tag:","开始加载ReyclerView布局");
        //设置RecyclerView布局
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //利用LinearLayoutManager来管理布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        //清空history_item_list
        history_item_List.clear();

        //查看是否已经登录账号
        if(check_is_Login()){
            Toast.makeText(History.this,"您已登录~",Toast.LENGTH_SHORT).show();
            //加载历史记录到List中
            initHistory_items();
        }else{
            Toast.makeText(History.this,"请先登录~",Toast.LENGTH_SHORT).show();
        }
        /**
         * 确认返回线程返回值之后加载history_list
         */
        Log.i("tag:","程序运行至这");
    }

    /**
     * 检查是否登录
     */
    private boolean check_is_Login(){
        String username = FirstActivityShowMap.username;
        String password = FirstActivityShowMap.password;
        if(username!=null && password!=null && !username.equals("") && !password.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    //加载历史记录到List中
    private void initHistory_items(){
        /**
         * 检查网络状况
         */
        if(isConnectIntent()){
            String username = FirstActivityShowMap.username;
            String password = FirstActivityShowMap.password;
            Log.i("tag","username="+username);
            Log.i("tag","password="+password);
            /**
             * 开始发送Http请求
             */
            dialog = new Dialog(History.this);
            dialog.setTitle("正在获取历史记录，请稍后");
            dialog.setCancelable(false);
            dialog.show();
            new GethistoryPostThread(username,password).start();
        }else{
            Toast.makeText(History.this,"当前网络不可用，请检查网络状态",Toast.LENGTH_SHORT).show();
        }

        /**
         * 处理333线程返回信息
         */
        handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                dialog.dismiss();
                if(msg.what == 333){
                    //处理返回线程返回的信息
                    if(!msg.obj.toString().equals("FAILED")){
                       String history_json = msg.obj.toString();
                        Log.i("tag:线程已获得JSON文件，内容为：",history_json);
                        //设置History_list
                        try{
                            Gson gson = new Gson();
                            history_item_List = gson.fromJson(history_json, new TypeToken<List<History_item>>() {}.getType());
                            is_returenhistory = true;
                            if(history_item_List.isEmpty()){
                                Log.i("tag:","history_list is empty");
                            }
                            else{
                                Log.i("tag:","history_list is not empty");
                                History_item mhistory = history_item_List.get(0);
                                Log.i("tag:",mhistory.toString());
                                Log.i("tag:","收到返回list，现在开始填充Adapter");
                                History_item_Adapter adapter = new History_item_Adapter(history_item_List);
                                recyclerView.setAdapter(adapter);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(History.this,"服务器获取历史记录失败",Toast.LENGTH_SHORT).show();
                        Log.i("tag","服务获取历史记录失败");
                    }
                }else{
                    Log.e("错误","线程333错误！");
                }
            }
        };
    }

    public class GethistoryPostThread extends Thread{
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
                Log.i("tag","historyActivity:responsejson = " + responsejson);
                //准备发送消息
                Message msg = handler.obtainMessage();
                //设置消息默认值
                msg.what = 333;
                //服务器返回信息的判断和处理
                if(!responsejson.equals("FAILED")){
                    msg.obj = responsejson;
                }else{
                    msg.obj = responsejson;
                    Log.i("tag","HistoryAcitivity:msg.obj 错误");
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
