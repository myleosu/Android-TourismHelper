package com.example.showMap.SQLiteDateBaseHpler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asus on 2019/3/5.
 */

public class MyDataBase extends SQLiteOpenHelper {

    static public MyDataBase myDataBaseOpenHelper;

    public static final String CREATE_HISTORYTABLE = "create table historytable ("
                                        + "id integer primary key autoincrement,"
                                        + "date text,"
                                        + "startpos text,"
                                        + "endpos text,"
                                        + "startweather text,"
                                        + "endweather text,"
                                        + "totaltime text,"
                                        + "memory text)";

    public static final String CREATE_USERTABLE = "create table usertable ("
                                        + "id integer primary key autoincrement,"
                                        + "username text,"
                                        + "userpsd text)";

    private Context mcontext;

    MyDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory,version);
        mcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORYTABLE);
        db.execSQL(CREATE_USERTABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists historytable");
        db.execSQL("drop table if exists usertable");
        onCreate(db);
    }
}
