package com.yutel.alog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yutel.alog.comm.Comm;
import com.yutel.alog.comm.Prefs;

public class AlogBootReceiver extends BroadcastReceiver {
	private Prefs mPrefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		mPrefs = new Prefs(context);
		if (mPrefs.isNetdebug()) {
			Comm.shell("setprop service.adb.tcp.port 5555\nstop adbd\nstart adbd\n");
		}
		if (mPrefs.isAutosave()) {
			Intent svcIntent = new Intent(context, LogSaveService.class);
			context.startService(svcIntent);
		}
	}
}
