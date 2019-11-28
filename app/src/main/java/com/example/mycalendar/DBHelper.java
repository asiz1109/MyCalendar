package com.example.mycalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "eventsDB";
    public static final String TABLE_EVENTS = "events";

    public static final String KEY_ID = "_id";
    public static final String KEY_EVENT = "event";
    public static final String KEY_DAY = "day";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_ALL_DAY = "all_day";
    public static final String KEY_REMIND = "remind";

    private static volatile DBHelper mInstance;

    private DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @NonNull
    static DBHelper getInstance(Context context){
        DBHelper instance = mInstance;
        if(instance==null){
            synchronized (DBHelper.class){
                instance = mInstance;
                if(instance==null){
                    instance = mInstance = new DBHelper(context);
                }
            }
        }
        return instance;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_EVENTS + "(" + KEY_ID
                + " integer primary key autoincrement," + KEY_EVENT + " text," + KEY_DAY + " integer," + KEY_MONTH + " integer," + KEY_YEAR + " integer," + KEY_HOUR + " integer,"
                + KEY_MINUTE + " integer," + KEY_ALL_DAY + " integer," + KEY_REMIND + " integer"+ ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
