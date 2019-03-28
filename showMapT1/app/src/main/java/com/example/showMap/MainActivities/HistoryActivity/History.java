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

import com.example.showMap.ActicityTools.MyActivityTools;
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

    public static List<History_item> history_item_List = new ArrayList<>();
    public static RecyclerView recyclerView;

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

        //加载历史记录到List中
        history_item_List.clear();
        initHistory_items();
    }

    //加载历史记录到List中
    private void initHistory_items(){

        //检查网络状态
        if(MyActivityTools.isConnectIntent(History.this)){
            String username = MyActivityTools.currentusername;
            String password = MyActivityTools.currentpassword;

            //启动GethistoryPostThread线程
            new MyActivityTools.GethistoryPostThread(username,password).start();
            Handler gethistoryhandler = MyActivityTools.myhandler;
            Log.i("tag:","username"+MyActivityTools.currentusername);
            Log.i("tag:","password"+MyActivityTools.currentpassword);
        }else{
            Toast.makeText(History.this,"当前网络不可用，请检查网络状态",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 处理Gethistory_Handler线程返回的信息
     * @param msg
     */
    public static void resolve_Gethistory_Handler(Message msg){
        //处理返回线程返回的信息
        Context gethistorycontext = AppManager.getInstance().getActivitycontext(History.class);
        if(!msg.obj.toString().equals("FAILED")){
            String history_json = msg.obj.toString();
            Log.i("tag:线程已获得JSON文件，内容为：",history_json);
            //设置History_list
            try{
                Gson gson = new Gson();
                history_item_List = gson.fromJson(history_json, new TypeToken<List<History_item>>() {}.getType());
                if(history_item_List.isEmpty()){
                    Log.i("tag:","history_list is empty");
                }
                else{
                    Log.i("tag:","收到返回list，现在开始填充Adapter");
                    History_item_Adapter adapter = new History_item_Adapter(history_item_List);
                    recyclerView.setAdapter(adapter);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(gethistorycontext,"服务器获取历史记录失败or用户暂无旅程记录",Toast.LENGTH_SHORT).show();
            Log.i("tag","服务获取历史记录失败or用户暂无旅程记录");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }
}
