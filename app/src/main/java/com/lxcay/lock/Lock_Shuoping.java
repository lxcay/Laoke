package com.lxcay.lock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.Toast;

public class Lock_Shuoping extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		// 判断是否开启了管理员
		ComponentName who = new ComponentName(this, Lock_MyAdmin.class);
		if (dpm.isAdminActive(who)) {
			dpm.lockNow();
			finish();
		} else {
			Toast.makeText(this, "先激活一键锁屏吧", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}
}
