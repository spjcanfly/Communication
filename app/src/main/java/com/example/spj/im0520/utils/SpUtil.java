package com.example.spj.im0520.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by spj on 2016/8/5.
 */
public class SpUtil {
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SharedPreferences mSp;

    private SpUtil() {

    }

    private static SpUtil instance = new SpUtil();

    public static SpUtil getInstance(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences("im0520", Context.MODE_PRIVATE);
        }
        return instance;
    }
    public void save(String key,Object value){
        if(value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        }else if(value instanceof Boolean){
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        }else if(value instanceof Integer){
            mSp.edit().putInt(key, (Integer) value).commit();
        }

    }
    //获取
    public String getString(String key,String defValue){
          return mSp.getString(key,defValue);
    }
    public boolean getBoolean(String key,boolean defValue){
        return mSp.getBoolean(key, defValue);
    }
    public Integer getInteger(String key,Integer defValue){
        return mSp.getInt(key,defValue);
    }

}
