package com.example.showMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        //清空history_item_list
        history_item_List.clear();

        //加载历史记录到List中
        initHistory_items();

        //设置RecyclerView布局
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //利用LinearLayoutManager来管理布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        History_item_Adapter adapter = new History_item_Adapter(history_item_List);
        recyclerView.setAdapter(adapter);
    }

    //加载历史记录到List中
    private void initHistory_items(){
        //创建数据库
        MyDataBase.myDataBaseOpenHelper  = new MyDataBase(this,"ApplicationDataBase.db",null,1);
        //sql查询语句
        String selecthistorysql = "select * from historytable";

        //查询数据库
        SQLiteDatabase db = MyDataBase.myDataBaseOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selecthistorysql,null);
        if(cursor.moveToFirst()){
            do{
                history_id = cursor.getInt(cursor.getColumnIndex("id"));
                history_date = cursor.getString(cursor.getColumnIndex("date"));
                history_startpos = cursor.getString(cursor.getColumnIndex("startpos"));
                history_endpos = cursor.getString(cursor.getColumnIndex("endpos"));
                history_start_weather = cursor.getString(cursor.getColumnIndex("startweather"));
                history_end_weather = cursor.getString(cursor.getColumnIndex("endweather"));
                history_totaltime = cursor.getString(cursor.getColumnIndex("totaltime"));
                history_memory = cursor.getString(cursor.getColumnIndex("memory"));

                History_item mhistory_item = new History_item(history_date,history_startpos,history_endpos,
                        history_start_weather,history_end_weather,history_totaltime);
                history_item_List.add(mhistory_item);
            }while(cursor.moveToNext());
        }
        cursor.close();
//        for(int i = 0;i<20;i++){
//            History_item first_history_item = new History_item(history_date,history_startpos,history_endpos,
//                    history_start_weather,history_end_weather,history_totaltime);
//            history_item_List.add(first_history_item);
//        }
    }
}
