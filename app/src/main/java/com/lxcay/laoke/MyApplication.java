package com.lxcay.laoke;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.gotye.api.GotyeUser;
import com.lxcay.laoke.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxcay on 2015/7/21.
 */
public class MyApplication extends Application {

    public static final String DEFAULT_APPKEY = "9c236035-2bf4-40b0-bfbf-e8b6dec54928";
    public static String APPKEY = DEFAULT_APPKEY;
    public static String IP = null;
    public static int PORT = -1;
    private static SharedPreferences spf;
    private static final String SHARE_PREFERENCE_NAME = "lxcay_config";
    public static ArrayList<Long> disturbGroupIds = new ArrayList<Long>();
    public static int LOGIN_OUT=0;
    // 是否有新消息提醒
    public static boolean newMsgNotify = true;
    // 不接收群消息
    public static boolean notReceiveGroupMsg = false;
    //群消息状态
    public static Map<Long, Integer> mapList = new HashMap<Long, Integer>();
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.isLog=true;//false 不打印log
        loadSelectedKey(this);
        spf = getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 判断是否设置群消息免打扰
     *
     * @param groupId
     * @return
     */
    public static boolean isGroupDontdisturb(long groupId) {
        if (disturbGroupIds == null) {
            String dontdisturbIds = spf.getString("groupDontdisturb", null);
            if (dontdisturbIds == null) {
                return false;
            } else {
                disturbGroupIds = new ArrayList<Long>();
                String ids[] = dontdisturbIds.split(",");
                for (String id : ids) {
                    disturbGroupIds.add(Long.parseLong(id));
                }
                return disturbGroupIds.contains(groupId);
            }
        } else {
            return disturbGroupIds.contains(groupId);
        }
    }
    public static void loadSelectedKey(Context context) {
        SharedPreferences spf = context.getSharedPreferences("gotye_api",Context.MODE_PRIVATE);
        APPKEY = spf.getString("selected_key", DEFAULT_APPKEY);
        String ip_port = spf.getString("selected_ip_port", null);
        if (!TextUtils.isEmpty(ip_port)) {
            String[] ipPort = ip_port.split(":");
            if (ipPort != null && ipPort.length >= 2) {
                try {
                    int port = Integer.parseInt(ipPort[1]);
                    IP = ipPort[0];
                    PORT = port;
                } catch (Exception e) {

                }

            }
        }
    }

    void onLoginCallBack(int code, GotyeUser currentLoginUser) {
        newMsgNotify = spf.getBoolean("new_msg_notify_" + currentLoginUser.getName(), true);
        notReceiveGroupMsg = spf.getBoolean("not_receive_group_msg_" + currentLoginUser.getName(), false);
    }

    public static boolean isNewMsgNotify() {
        return newMsgNotify;
    }

    /**
     * 设置新消息是否提醒
     *
     * @param newMsgNotify_
     */
    public static void setNewMsgNotify(boolean newMsgNotify_, String name) {
        newMsgNotify = newMsgNotify_;
        spf.edit().putBoolean("new_msg_notify_" + name, newMsgNotify).commit();
    }
    /**
     * 判断是否接收群消息
     */
    public static boolean isNotReceiveGroupMsg() {
        return notReceiveGroupMsg;
    }

    /**
     * 设置是否接收群消息
     *
     * @param notReceiveGroupMsg_
     */
    public static void setNotReceiveGroupMsg(boolean notReceiveGroupMsg_, String loginName) {
        notReceiveGroupMsg = notReceiveGroupMsg_;
        spf.edit().putBoolean("not_receive_group_msg_" + loginName, notReceiveGroupMsg).commit();
    }

    /**
     * 设置指定群组的群消息转态
     * @param groupId
     * @param tag
     */
    public static void setGroupMarkTag(long groupId, int tag){
        if(mapList == null){
            mapList =  new HashMap<Long, Integer>();
        }
        mapList.put(groupId, tag);
    }
    /**
     * 获取指定群组的群消息状态
     * @param groupId
     * @return
     */
    public static int getGroupMarKTag(long groupId){
        int code = -1;
        if(mapList != null){
            if(mapList.containsKey(groupId)){
                code = MyApplication.mapList.get(groupId);
            }
        }
        return code ;
    }


    /**
     * 设置群消息免打扰
     */
    public static void setGroupDontdisturb(long groupId) {
        if (disturbGroupIds == null) {
            disturbGroupIds = new ArrayList<Long>();
        }
        if (!disturbGroupIds.contains(groupId)) {
            disturbGroupIds.add(groupId);
            String dontdisturbIds = spf.getString("groupDontdisturb", null);
            if (dontdisturbIds == null) {
                spf.edit().putString("groupDontdisturb", String.valueOf(groupId)).commit();
            } else {
                dontdisturbIds += "," + String.valueOf(groupId);
                spf.edit().putString("groupDontdisturb", dontdisturbIds).commit();
            }
        }
    }

    /**
     * 移除群消息免打扰
     *
     * @param groupId
     */
    public static void removeGroupDontdisturb(long groupId) {
        if (disturbGroupIds == null) {
            return;
        } else {
            disturbGroupIds.remove(groupId);
        }
    }
}
