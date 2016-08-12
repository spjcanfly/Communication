package com.example.spj.im0520.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.spj.im0520.model.dao.UserAccountTable;

/**
 * Created by spj on 2016/8/2.
 */
public class UserAccoutDB extends SQLiteOpenHelper {

    private static final int DB_VERSION=1;

    public UserAccoutDB(Context context) {
        super(context, "account.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
          db.execSQL(UserAccountTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
