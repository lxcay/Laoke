package com.lxcay.laoke.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Random;
import java.util.UUID;


public class MobileUtil {

    /**
     * divicesId
     * @param c
     * @return
     */
    public static String getDeviceId(Context c) {
        TelephonyManager tm = (TelephonyManager) c.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();

        return TextUtils.isEmpty(deviceId) ? "" : deviceId;
    }

    public static String getAndroidId(Context c) {
        String androidId = Settings.Secure.getString(c.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        return TextUtils.isEmpty(androidId) ? "" : androidId;
    }

    public static String getSDKVersion() {
        return android.os.Build.VERSION.SDK;
    }

    public static String getSDKRelease() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机服务商信息
     * 需要加入权限<uses-permission
     * android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static String getOperatorName(Context c) {
        String ProvidersName = "";
        // 返回唯一的用户ID;就是这张卡的编号神马的
        TelephonyManager IMSIS = ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE));
        String IMSI = IMSIS.getSubscriberId();
        if (IMSI == null)
            return ProvidersName;

        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        }

        return ProvidersName;
    }

    public static String getResolution(Context c) {
        DisplayMetrics mDisplayMetrics = c.getResources().getDisplayMetrics();
        return mDisplayMetrics.widthPixels + "x" + mDisplayMetrics.heightPixels;
    }

    private static String changeUUID(Context context) {

        String uuid = getSogouUUID(context);
        return uuid;
    }

    private static String getSogouUUID(Context context) {
        return "SOGOU" + UUID.randomUUID().toString() + get15Random();
    }

    private static String get15Random() {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            str += random.nextInt(10);
        }
        return str;
    }

    /**
     * 获取当前设置的电话号码
     */
    public static String getNativePhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phone=telephonyManager.getLine1Number();
        return phone;
    }
}
