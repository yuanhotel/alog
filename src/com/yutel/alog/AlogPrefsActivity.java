package com.yutel.alog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.yutel.alog.comm.Comm;
import com.yutel.alog.comm.Prefs;
import com.yutel.alog.service.LogSaveService;
import com.yutel.alog.vo.NetCfg;

public class AlogPrefsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener, OnSharedPreferenceChangeListener {
	private final static String START = "setprop service.adb.tcp.port 5555\nstop adbd\nstart adbd\n";
	private final static String STOP = "setprop service.adb.tcp.port 5555\nstop adbd\n";
	private CheckBoxPreference autosave;
	private CheckBoxPreference netdebug;
	private ListPreference level;
	private ListPreference format;
	private ListPreference buffer;
	private Prefs mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference);
		mPrefs = new Prefs(this);
		autosave = (CheckBoxPreference) findPreference(Prefs.AUTOSAVE_KEY);
		netdebug = (CheckBoxPreference) findPreference(Prefs.NETDEBUG_KEY);
		level = (ListPreference) findPreference(Prefs.LEVEL_KEY);
		format = (ListPreference) findPreference(Prefs.FORMAT_KEY);
		buffer = (ListPreference) findPreference(Prefs.BUFFER_KEY);
		autosave.setOnPreferenceChangeListener(this);
		netdebug.setOnPreferenceChangeListener(this);
		level.setOnPreferenceChangeListener(this);
		format.setOnPreferenceChangeListener(this);
		buffer.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference pf, Object o) {
		if (netdebug == pf) {
			Boolean dev = (Boolean) o;
			if (dev) {
				Comm.shell(START);
			} else {
				Comm.shell(STOP);
			}
		} else if (autosave == pf) {
			Boolean auto = (Boolean) o;
			Log.i(Comm.TAG, "auto=" + auto);
			if (auto) {
				Intent svcIntent = new Intent(this, LogSaveService.class);
				startService(svcIntent);
			} else {
				Intent svcIntent = new Intent(this, LogSaveService.class);
				stopService(svcIntent);
			}
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setNetdebug();
		setLevelTitle();
		setFormatTitle();
		setBufferTitle();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			System.exit(0);
			return true;
		}
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Prefs.LEVEL_KEY)) {
			setLevelTitle();
		} else if (key.equals(Prefs.FORMAT_KEY)) {
			setFormatTitle();
		} else if (key.equals(Prefs.BUFFER_KEY)) {
			setBufferTitle();
		}
	}

	private void setNetdebug() {
		NetCfg nc = Comm.netcfg();
		if (nc != null) {
			netdebug.setSummary(nc.getIp() + "," + nc.getMac());
		}
	}

	private void setLevelTitle() {
		level.setTitle(getRes(R.string.level)
				+ mPrefs.getLevel().getTitle(this) + ")");
	}

	private void setFormatTitle() {
		format.setTitle(getRes(R.string.format)
				+ mPrefs.getFormat().getTitle(this) + ")");
	}

	private void setBufferTitle() {
		buffer.setTitle(getRes(R.string.buffer)
				+ mPrefs.getBuffer().getTitle(this) + ")");
	}

	private String getRes(int id) {
		return getResources().getString(id) + " (";
	}
}