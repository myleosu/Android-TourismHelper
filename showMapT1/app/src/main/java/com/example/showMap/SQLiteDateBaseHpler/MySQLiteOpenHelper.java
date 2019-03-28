package com.example.showMap.SQLiteDateBaseHpler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 陈伟钦 on 2018/9/2.
 */

//数据库管理类
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String SEARCH_DATA = "create table search_records("
            +"id integer primary key autoincrement,"
            +"keyword text)";
    private Context mcontext;
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mcontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SEARCH_DATA);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
