package com.example.spj.im0520.model.dao;

/**
 * Created by spj on 2016/8/3.
 */
//联系人的表
public class ContactTable {
    public static final String TABLE_NAME="tab_contact";
    public static final String COL_NAME="name";
    public static final String COL_HXID="hxid";
    public static final String COL_NICK="nick";
    public static final String COL_PHOTO="photo";
    public static final String COL_IS_CONTACT="is_contact";//是否是自己的联系人

    public static final String CREATE_TABLE="create table "
            + TABLE_NAME +" ("
            + COL_HXID + " text primary key,"
            +COL_NAME + " text,"
            +COL_NICK + " text,"
            +COL_PHOTO + " text,"
            +COL_IS_CONTACT + " integer);";
}
