package com.example.spj.im0520.model.dao;

/**
 * Created by spj on 2016/8/2.
 */
public class UserAccountTable {

    public static final String TAB_NAME = "user";
    public static final String COL_NAME = "name";
    public static final String COL_HXID = "hxid";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";

    public static final String CREATE_TABLE = "create table "
            + TAB_NAME + " ("
            + COL_HXID + " text primary key, "
            +COL_NAME + " text, "
            + COL_NICK + " text, "
            +COL_PHOTO + " text);";


}
