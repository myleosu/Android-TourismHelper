package com.example.showMap.MainActivities.HistoryActivity;

import android.content.Context;
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


import com.example.showMap.ActicityTools.MyActivityTools;
import com.example.showMap.ActivityManager.AppManager;
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
    private static EditText content;
    private Intent intent;
    private String trip_mind;
    private ImageView img;
    private static int Listhistory_postion = -1;//HistoryList对应的id
    private History_item mhistory_item;//Histoy_list对应的history_item
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
        if(MyActivityTools.isConnectIntent(TripThoughts.this)){
            //启动SavePostThread线程
            new MyActivityTools.SavePostThread(mhistory_item.getUsername(),FirstActivityShowMap.password,mhistory_item.getId(),content.getText().toString()).start();
            Handler savehandler = MyActivityTools.myhandler;
        }else{
            Toast.makeText(TripThoughts.this,"请检查当前网络状态!",Toast.LENGTH_SHORT).show();
        }
    }

    private void Deletethis_History(){
        if(MyActivityTools.isConnectIntent(TripThoughts.this)){
            //启动DeletePostThread线程
            new MyActivityTools.DeletePostThread(MyActivityTools.currentusername,MyActivityTools.currentpassword,mhistory_item.getId()).start();
            Handler deletehandler = MyActivityTools.myhandler;
        }else{
            Toast.makeText(TripThoughts.this,"请检查当前网络状态!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理DeletePostThreadHandler消息
     */
    public static void resolve_DeleteHandler(Message msg){
        Context deletecontext = AppManager.getInstance().getActivitycontext(TripThoughts.class);
        if(msg.obj.equals("SUCCEEDED")){
            Toast.makeText(deletecontext,"删除成功！",Toast.LENGTH_SHORT).show();
            History.history_item_List.remove(Listhistory_postion);
            History.recyclerView.setAdapter(new History_item_Adapter(History.history_item_List));
            AppManager.getInstance().killActivity(TripThoughts.class);
        }
        else{
            Toast.makeText(deletecontext,"删除失败！",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理SavePostThreadHandler消息
     */
    public static void resolve_SaveHandler(Message msg){
        Context savecontext = AppManager.getInstance().getActivitycontext(TripThoughts.class);
        if(msg.obj.equals("SUCCEEDED")){
            Toast.makeText(savecontext,"更新成功！",Toast.LENGTH_SHORT).show();
            History.history_item_List.get(Listhistory_postion).setTour_memory(content.getText().toString());
            History.recyclerView.setAdapter(new History_item_Adapter(History.history_item_List));
        }else{
            Toast.makeText(savecontext,"更新失败！",Toast.LENGTH_SHORT).show();
        }
    }
}
