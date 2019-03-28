package com.example.showMap.MainActivities.HistoryActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.ClientHttpService.DelectPostService;
import com.example.showMap.ClientHttpService.LoginPostService;
import com.example.showMap.ClientHttpService.UpdatePostService;
import com.example.showMap.MainActivities.FirstActivityShowMap;
import com.example.showMap.MainActivities.HistoryActivity.History;
import com.example.showMap.MainActivities.HistoryActivity.History_item;
import com.example.showMap.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created by Freff on 2019/3/15.
 */

public class TripThoughts extends AppCompatActivity implements View.OnClickListener{
    private ImageButton delete;
    private ImageButton save;
    private ImageButton share;
    private TextView routeInfo;
    private EditText content;
    private Intent intent;
    private String trip_mind;
    private ImageView img;
    private int Listhistory_postion = -1;//HistoryList对应的id
    private History_item mhistory_item;//Histoy_list对应的history_item
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.trip_record);
        AppManager.getInstance().addActivity(this);
        Listhistory_postion = getIntent().getIntExtra("Listhistoryid",-1);
        super.onCreate(savedInstanceState);
        if(Listhistory_postion==-1){
            Toast.makeText(this,"旅程记录编号为-1错误",Toast.LENGTH_SHORT).show();
            finish();
        }
        getSupportActionBar().hide();
        //实例化控件
        init();
    }
    private void init() {
        img=(ImageView)findViewById(R.id.imageView2);
        delete=(ImageButton)findViewById(R.id.delete_record_btn);
        save=(ImageButton)findViewById(R.id.save_record_btn);
        share=(ImageButton)findViewById(R.id.share_record_btn);
        routeInfo=(TextView)findViewById(R.id.routeInfo);
        content=(EditText)findViewById(R.id.trip_mind);
        share.setOnClickListener(this);
        delete.setOnClickListener(this);
        save.setOnClickListener(this);
        mhistory_item = History.history_item_List.get(Listhistory_postion);
        if(mhistory_item.getTour_memory() != null)
            content.setText(mhistory_item.getTour_memory());
        String routeInfoText = mhistory_item.getTour_date()+":"
                +mhistory_item.getStartpos()+"("+mhistory_item.getStart_weather()+")"
                +"→"+mhistory_item.getEndpos()+"("+mhistory_item.getEnd_weather()+")";
        routeInfo.setText(routeInfoText);
    }
    //点击分享按钮分享
    public void ShareNote(){
        String shareText = mhistory_item.getTour_date()+"\n"
                +mhistory_item.getStartpos()+"("+mhistory_item.getStart_weather()+")"
                +"→"+mhistory_item.getEndpos()+"("+mhistory_item.getEnd_weather()+")"
                +"\n"+"旅程时间："+mhistory_item.getTotaltime()+"min"
                +"\n"+"旅程感想:"+"\n"
                +mhistory_item.getTour_memory();
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,//分享类型设置为文本型
           shareText);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.share_record_btn:
               ShareNote();
               break;
           //还有删除按钮未监听
           case R.id.delete_record_btn:
               Deletethis_History();
               break;
           case R.id.save_record_btn:
               Savehis_History();
               break;
           default:
       }
    }

    private void Savehis_History(){
        new SavePostThread(mhistory_item.getUsername(),FirstActivityShowMap.password,mhistory_item.getId(),content.getText().toString()).start();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 666) {
                    if(msg.obj.equals("SUCCEEDED")){
                        Toast.makeText(TripThoughts.this,"更新成功！",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(TripThoughts.this,"更新失败！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private void Deletethis_History(){
        new DeletePostThread(mhistory_item.getUsername(), FirstActivityShowMap.password,mhistory_item.getId()).start();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 555){
                    if(msg.obj.equals("SUCCEEDED")){
                        Toast.makeText(TripThoughts.this,"删除成功！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(TripThoughts.this,"删除失败！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }
    public static int HISTORY_SUCCEEDED = 5;
    public static int HISTORY_FAILED = 4;

    public class SavePostThread extends Thread{
        private String username;
        private String password;
        private int id;
        private String memory;
        SavePostThread(String username,String password,int id,String memory){
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
            Message msg = handler.obtainMessage();
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
            handler.sendMessage(msg);
        }
    }

    public class DeletePostThread extends Thread{

        private String username;
        private String password;
        private int id;
        DeletePostThread(String username,String password,int id){
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
            Log.i("tag","TriThoughtActivity:responseInt = " + responseInt);
            //准备发送消息
            Message msg = handler.obtainMessage();
            //设置消息默认值
            msg.what = 555;
            //服务器返回信息的判断和处理
            if(responseInt == HISTORY_SUCCEEDED){
                msg.obj = "SUCCEEDED";
            }else if(responseInt == HISTORY_FAILED){
                msg.obj = "FAILED";
            }else{
                Log.i("tag","TriThoughtAcitivity:msg.obj 错误");
            }
            handler.sendMessage(msg);
        }
    }
}
