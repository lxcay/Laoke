/**
 * 文件名：PreferencesUtil.java   <br/>
 * <p/>
 * 版本信息：     <br/>
 * 日期：2013年10月14日     <br/>
 * Copyright Sogou Corporation 2013      <br/>
 * 版权所有     <br/>
 */
package com.lxcay.laoke.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 使用Preference来管理配置<br/>
 *
 * @author wangyunsi0662
 */
public class PreferenceUtil {
    /**
     * 外部可读的xml文件名(包名+WORLD_READABLE_PREFS_NAME)
     */
    private static final String WORLD_READABLE_PREFS_NAME = "world_readable";
    private static final String THIRD_PART_USER_ID = "com.sogou.passportsdk.util.PreferencesUtil.THIRD_PART_USER_ID";
    private static final String PASSPORT_SGID = "com.sogou.passportsdk.util.PreferencesUtil.PASSPORT_SGID";
    private static final String PASSPORT_USERINFO = "com.sogou.passportsdk.util.PreferencesUtil.PASSPORT_USERINFO";
    private static final String LOGIN_TYPE = "login_type";
    public static final String LOGIN_TYPE_QQ = "1";
    private static final String PASSPORT_CONFIGINFO = "com.sogou.passportsdk.util.PreferencesUtil.PASSPORT_CONFIGINFO";
    private static final String PASSPORT_DEVCONFIGINFO = "com.sogou.passportsdk.util.PreferencesUtil.PASSPORT_DEVCONFIGINFO";
    private static final String PASSPORT_ONLINECONFIGINFO = "com.sogou.passportsdk.util.PreferencesUtil.PASSPORT_ONLINECONFIGINFO";
    public static final String PASSPORT_CONFIGINFOTIMEOUT = "com.sogou.passportsdk.util.PreferencesUtil.PASSPORT_CONFIGINFOTIMEOUT";


    public static final String USERNAME = "username";//登录的配置文件

    public static boolean setLogin(Context context, String type) {
        Context appContext = context.getApplicationContext();
        return setStringValue(appContext, "", USERNAME, type);
    }

    /**
     * 获取到的是手机号码，但是我将把他作为用户名来用
     * @param context
     * @return
     */
    public static String getLogin(Context context) {
        Context appContext = context.getApplicationContext();
        return getStringValue(appContext, "", USERNAME);
    }

    /**
     * 设置Key对应的String值 <br/>
     */
    public static boolean setStringValue(Context context, String prefsName, String key, String value) {
        removeStringValue(context, prefsName, key);

        SharedPreferences prefs;

        if (TextUtils.isEmpty(prefsName)) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            prefs = context.getSharedPreferences(context.getPackageName() + "." + prefsName,
                    Context.MODE_WORLD_READABLE);
        }

        Editor editor = prefs.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 获得Key对应的String值 <br/>
     * 默认返回空<br/>
     */
    public static String getStringValue(Context context, String prefsName, String key) {
        SharedPreferences prefs;

        if (TextUtils.isEmpty(prefsName)) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            prefs = context.getSharedPreferences(context.getPackageName() + "." + prefsName,
                    Context.MODE_WORLD_READABLE);
        }

        return prefs.getString(key, "");
    }

    /**
     * 删除Key对应的String值 <br/>
     * 默认返回空<br/>
     */
    public static boolean removeStringValue(Context context, String prefsName, String key) {
        SharedPreferences prefs;

        if (TextUtils.isEmpty(prefsName)) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            prefs = context.getSharedPreferences(context.getPackageName() + "." + prefsName,
                    Context.MODE_WORLD_READABLE);
        }

        Editor editor = prefs.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static void setBooleanValue(Context context,String key,boolean value){
        SharedPreferences spf=context.getSharedPreferences("boolean_prop", Context.MODE_PRIVATE);
        Editor edit=spf.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBooleanValue(Context context,String key){
        SharedPreferences spf=context.getSharedPreferences("boolean_prop", Context.MODE_PRIVATE);
        return spf.getBoolean(key, false);
    }
}
