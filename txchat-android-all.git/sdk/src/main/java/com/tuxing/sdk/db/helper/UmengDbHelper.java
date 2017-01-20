package com.tuxing.sdk.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangst on 15-10-14.
 */
public class UmengDbHelper extends SQLiteOpenHelper {

    private final static Logger logger = LoggerFactory.getLogger(UmengDbHelper.class);
    private static final String TAG = "UmengDbHelper";
    private static final String DB_NAME = "MsgLogStore.db";
    private static final int DB_VERSION = 2;
    Context context;
    private static UmengDbHelper instance;
    private static final String table_MsgLogIdTypeStore = "CREATE TABLE  IF NOT EXISTS  MsgLogIdTypeStore (MsgId varchar, MsgType varchar, PRIMARY KEY(MsgId))";
    private static final String table_MsgLogStore = "CREATE TABLE IF NOT EXISTS MsgLogStore ( MsgId varchar, ActionType Integer, Time long, PRIMARY KEY(MsgId, ActionType))";

    public static synchronized UmengDbHelper getHelper(Context context) {
        if (instance == null)
            instance = new UmengDbHelper(context);
        return instance;
    }

    // Constructor
    public UmengDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table_MsgLogIdTypeStore);
        db.execSQL(table_MsgLogStore);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(table_MsgLogIdTypeStore);
        db.execSQL(table_MsgLogStore);
    }

    public void createTable (String sql){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }
    public void createUmTable(){
        createTable(table_MsgLogIdTypeStore);
        createTable(table_MsgLogStore);
    }
}
