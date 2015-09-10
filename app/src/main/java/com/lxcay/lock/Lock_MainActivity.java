package com.lxcay.lock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.lxcay.laoke.R;

public class Lock_MainActivity extends Activity {

	private DevicePolicyManager dpm;

	@Override
	protected void onStart() {
		Intent intent = new Intent(this, Lock_ForeverService.class);
		startService(intent);
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_activity_main);
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);// 得到设备管理器对象
	}

	public void active(View v) {// 激活一键锁屏
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);// 意图管理器
		ComponentName who = new ComponentName(this, Lock_MyAdmin.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后才能一键锁屏，保护设备");
		startActivity(intent);
	}

	public void uninstall(View v) {// 卸载
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);// 得到设备管理器对象
		ComponentName mDeviceAdminSample = new ComponentName(this, Lock_MyAdmin.class);
		dpm.removeActiveAdmin(mDeviceAdminSample);// 删除权限

		Intent intent = new Intent();// 用代码的方式卸载这个软件
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

//	public void setPassword(View v) {// 设置密码锁屏
//		// 判断是否开启了管理员
//		ComponentName who = new ComponentName(this, Lock_MyAdmin.class);
//		if (dpm.isAdminActive(who)) {
//			dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);// 得到设备管理器对象
//			dpm.resetPassword("2589", 0);
//			dpm.lockNow();
//		} else {
//			Toast.makeText(this, "先激活一键锁屏吧", Toast.LENGTH_SHORT).show();
//			return;
//		}
//	}
//
//	public void cleanPassword(View v) {// 清除密码
//
//		// 判断是否开启了管理员
//		ComponentName who = new ComponentName(this, Lock_MyAdmin.class);
//		if (dpm.isAdminActive(who)) {
//			dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);// 得到设备管理器对象
//			dpm.resetPassword("", 0);
//		} else {
//			Toast.makeText(this, "先激活一键锁屏吧", Toast.LENGTH_SHORT).show();
//			return;
//		}
//	}

	public void createIcon(View v) {// 创建屏幕快捷图标

		// 判断是否开启了管理员
		ComponentName who = new ComponentName(this, Lock_MyAdmin.class);
		if (dpm.isAdminActive(who)) {
			// 发送一个广播消息。 大吼一声 桌面帮忙来个快捷图标呗。
			Intent intent = new Intent();
			// 与桌面的源代码的广播接受者关心的动作一致。
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

			// 指定快捷方式的数据
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键锁屏");
			// 指定快捷方式的图片
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qq);//这种不适应所有的
//			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
			Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.mipmap.qq);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			// 指定快捷方式的动作。

			Intent i = new Intent();
			i.setClass(this, Lock_Shuoping.class);
			i.setAction("com.lixiang");// 在清单文件中有设置
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);// 快捷键图标生效
			intent.putExtra("duplicate", false);
			sendBroadcast(intent);
			//方式2
//			createShortcut(getApplicationContext(), 2);
		} else {
			Toast.makeText(this, "先激活一键锁屏吧", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	/**
	 * 创建快捷方式
	 * 
	 * @param context
	 * @param type
	 *            启动创建还是设置页面创建
	 */
	public static void createShortcut(Context context, int type) {
		Intent shortcuts = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcuts.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键换壁纸");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.mipmap.qq);
		shortcuts.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		Intent i = new Intent();
		i.setClass(context, Lock_Shuoping.class);
		i.setAction("com.lixiang");// 在清单文件中有设置
		shortcuts.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);// 快捷键图标生效
		shortcuts.putExtra("duplicate", false);

		// 如果该方法被设置页面调用，则显示Toast提示。
		if (type == SHORTCUT_CREATE_TYPE_SETTING) {
			Toast.makeText(context, "正在创建一键锁屏快捷方式…", Toast.LENGTH_SHORT).show();
		}
		/*
		 * 发送广播，系统当前的Launcher自动创建快捷方式 如果该Launcher 默认创建快捷方式或者检测到桌面上有该快捷方式后提示，
		 * 则会显示两次（我们自己让其显示一次） 在此单独适配的机型有： LG NEXUS4、三星S3，小米2S
		 */
		String phoneType = Build.MODEL;
		if (phoneType.equals("Full JellyBean on Mako") || phoneType.equals("SHV-E120L") 
				|| phoneType.equals("MI 2S") || phoneType.equals("MI 2A") || phoneType.equals("MI 3")
				|| phoneType.equals("HM 1") || phoneType.equals("2013022")) {

			if (!hasExistShortcut(context)) {
				if (phoneType.equals("MI 2S") || phoneType.equals("MI 2A") || phoneType.equals("MI 3") || phoneType.equals("HM 1") || phoneType.equals("2013022")) { // 小米系列
					context.sendBroadcast(shortcuts);
					// 如果该方法被设置页面调用，则显示Toast提示。
					if (type == SHORTCUT_CREATE_TYPE_SETTING) {
						Toast.makeText(context, "一键锁屏快捷方式创建成功", Toast.LENGTH_SHORT).show();
					}
				} else {
					context.sendBroadcast(shortcuts);
				}
			} else {
				if (type == SHORTCUT_CREATE_TYPE_SETTING) {
					Toast.makeText(context, "快捷方式已存在", Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			context.sendBroadcast(shortcuts);
		}

	}

	/**
	 * 判断当前桌面上是否存在“一键换壁纸”的快捷方式
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean hasExistShortcut(Context context) {
		String uriStr = null;
		String authority = null;
		String phoneType = Build.MODEL;
		if (Build.VERSION.SDK_INT < 8) {
			authority = "com.android.launcher.settings";
		} else {
			if (phoneType.equals("SHV-E120L")) { // 小米1
				authority = "com.sec.android.app.twlauncher.settings";
			} else if (phoneType.equals("Full JellyBean on Mako")) { // LG
				authority = "com.android.launcher2.settings";
			} else if (phoneType.equals("MI 2S") || phoneType.equals("MI 2A") || phoneType.equals("MI 3") || phoneType.equals("HM 1") || phoneType.equals("2013022")) { // 小米2S,2A
				authority = "com.miui.home.launcher.settings";
			} else {
				authority = "com.android.launcher2.settings";
			}
		}
		uriStr = "content://" + authority + "/favorites?notify=true";
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(Uri.parse(uriStr), new String[] { "_id", "title", "iconPackage" }, "title=? and iconPackage=?",
					new String[] { "一键换壁纸", "com.sogou.wallpaper" }, null);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					return true;
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	/**
	 * 创建快捷方式的类型-2-从设置页面的“创建快捷方式”item点击创建
	 */
	public static final int SHORTCUT_CREATE_TYPE_SETTING = 2;

}
