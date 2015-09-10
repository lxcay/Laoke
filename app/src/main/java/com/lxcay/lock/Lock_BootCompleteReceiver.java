package com.lxcay.lock;

import android.content.BroadcastReceiver; 
import android.content.Context;
import android.content.Intent;

/**
 * @author lxcay
 * 开机启动广播
 */
public class Lock_BootCompleteReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startupintent = new Intent(context,Lock_AlwaysService.class);
		startupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		context.startService(startupintent);  //启动广播 
	} 
}
