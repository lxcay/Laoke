package com.lxcay.laoke;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeNotify;
import com.lxcay.laoke.utils.AppUtil;
import com.lxcay.laoke.utils.Logger;
import com.lxcay.laoke.utils.PreferenceUtil;

public class GotyeService extends Service {
    String TAG=GotyeService.class.getSimpleName();
    public static final String ACTION_LOGIN = "gotyeim.login";
    private GotyeAPI api;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        api = GotyeAPI.getInstance();
        MyApplication.loadSelectedKey(this);
        //初始化
        api.init(getBaseContext(), MyApplication.APPKEY);
        //语音识别初始化
        api.initIflySpeechRecognition();
        api.enableLog(false, true, false);
        //开始接收离线消息
        api.beginReceiveOfflineMessage();
        //添加推送消息监听
        api.addListener(mDelegate);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (ACTION_LOGIN.equals(intent.getAction())) {
                String name = intent.getStringExtra("name");
                api.login(name,"");
            }
        } else {
            String user = getUser(this);
            Logger.d(TAG, "user = " + user);
            if (!TextUtils.isEmpty(user)) {
                //导致杀掉进程后自动登录的Bug
				int code = api.login(user, "");
				Logger.d(TAG, code== -1?"正在登陆":"已经登陆或者正在登陆");
            }
        }
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    public static String getUser(Context context) {
        return PreferenceUtil.getLogin(context);
    }

    @SuppressWarnings("deprecation")
    private void notify(String msg) {
        String currentPackageName = AppUtil.getTopAppPackage(getBaseContext());
        if (currentPackageName.equals(getPackageName())) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        Notification notification = new Notification(R.mipmap.ic_launcher, msg, System.currentTimeMillis());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notify", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, getString(R.string.app_name),msg, pendingIntent);
        notificationManager.notify(0, notification);
    }


    private GotyeDelegate mDelegate = new GotyeDelegate() {
        @Override
        public void onReceiveMessage(GotyeMessage message) {
            String msg = null;

            if (message.getType() == GotyeMessageType.GotyeMessageTypeText) {
                msg = message.getSender().getName() + ":" + message.getText();
            } else if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
                msg = message.getSender().getName() + "发来了一条图片消息";
            } else if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
                msg = message.getSender().getName() + "发来了一条语音消息";
            } else if (message.getType() == GotyeMessageType.GotyeMessageTypeUserData) {
                msg = message.getSender().getName() + "发来了一条自定义消息";
            } else {
                msg = message.getSender().getName() + "发来了一条群邀请信息";
            }
            if (message.getReceiver() instanceof GotyeGroup) {
                if (!(MyApplication.isGroupDontdisturb(message.getReceiver().getId()))) {
                    GotyeService.this.notify(msg);
                }
                return;
            } else {
                GotyeService.this.notify(msg);
            }
        }

        @Override
        public void onReceiveNotify(GotyeNotify notify) {
            String msg = notify.getSender().getName() + "邀请您加入群[";
            if (!TextUtils.isEmpty(notify.getFrom().getName())) {
                msg += notify.getFrom().getName() + "]";
            } else {
                msg += notify.getFrom().getId() + "]";
            }
            GotyeService.this.notify(msg);
        }
    };

    //服务被注销的时候重启服务
    @Override
    public void onDestroy() {
        Logger.d(TAG, "gotye_service onDestroy");
        GotyeAPI.getInstance().removeListener(mDelegate);
        this.startService(new Intent(this, GotyeService.class)); // 銷毀時重新啟動Service
        super.onDestroy();
    }
}
