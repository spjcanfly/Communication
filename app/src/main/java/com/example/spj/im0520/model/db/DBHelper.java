package com.example.spj.im0520.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.spj.im0520.model.dao.ContactTable;
import com.example.spj.im0520.model.dao.InvitationTable;

/**
 * Created by spj on 2016/8/3.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final int DB_VERSION=1;

    public DBHelper(Context context, String name) {
        
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactTable.CREATE_TABLE);
        db.execSQL(InvitationTable.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
