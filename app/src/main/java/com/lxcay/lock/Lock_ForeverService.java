package com.lxcay.lock;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * 不死的服务
 */
public class Lock_ForeverService extends Service {
	private ScreenOffReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
			for (RunningAppProcessInfo list : lists) {
				am.killBackgroundProcesses(list.processName);// 锁屏杀死所有进程
				System.out.println(list.processName);
			}
		}
	}

	@Override
	public void onCreate() {
		receiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);// 创建一个锁屏的广播接受者
		registerReceiver(receiver, filter);// 注册广播
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Intent intent = new Intent(this, Lock_AlwaysService.class);
		startService(intent);
		unregisterReceiver(receiver);// 当服务销毁的时候 把广播接收者释放掉
		super.onDestroy();
	}
}
