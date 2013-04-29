package com.yutel.alog.comm;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yutel.alog.vo.Buffer;
import com.yutel.alog.vo.Format;
import com.yutel.alog.vo.Level;

public class Prefs {
	public static final String LEVEL_KEY = "level";
	public static final String FORMAT_KEY = "format";
	public static final String BUFFER_KEY = "buffer";
	public static final String FILTER_PATTERN_KEY = "filterPattern";
	public static final String NETDEBUG_KEY = "netdebug";
	public static final String AUTOSAVE_KEY = "autosave";

	private SharedPreferences sharedPrefs = null;

	public Prefs(Context context) {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	private String getString(String key, String def) {
		String s = sharedPrefs.getString(key, def);
		return s;
	}

	private void setString(String key, String val) {
		Editor e = sharedPrefs.edit();
		e.putString(key, val);
		e.commit();
	}

	private boolean getBoolean(String key, boolean def) {
		boolean b = sharedPrefs.getBoolean(key, def);
		return b;
	}

	private void setBoolean(String key, boolean val) {
		Editor e = sharedPrefs.edit();
		e.putBoolean(key, val);
		e.commit();
	}

	public Level getLevel() {
		return Level.valueOf(getString(LEVEL_KEY, "V"));
	}

	public void setLevel(Level level) {
		setString(LEVEL_KEY, level.toString());
	}

	public Format getFormat() {
		String f = getString(FORMAT_KEY, "BRIEF");
		if (!f.equals(f.toUpperCase())) {
			f = f.toUpperCase();
			setString(FORMAT_KEY, f);
		}
		return Format.valueOf(f);
	}

	public void setFormat(Format format) {
		setString(FORMAT_KEY, format.toString());
	}

	public Buffer getBuffer() {
		return Buffer.valueOf(getString(BUFFER_KEY, "MAIN"));
	}

	public void setBuffer(Buffer buffer) {
		setString(BUFFER_KEY, buffer.toString());
	}

	public String getFilter() {
		return getString("filter", null);
	}

	public Pattern getFilterPattern() {
		if (!isFilterPattern()) {
			return null;
		}

		String p = getString("filter", null);
		if (p == null) {
			return null;
		}
		try {
			return Pattern.compile(p, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			setString("filter", null);
			Log.w("alogcat", "invalid filter pattern found, cleared");
			return null;
		}
	}

	public void setFilter(String filter) {
		setString("filter", filter);
	}

	public boolean isFilterPattern() {
		return getBoolean(FILTER_PATTERN_KEY, false);
	}

	public void setFilterPattern(boolean filterPattern) {
		setBoolean(FILTER_PATTERN_KEY, filterPattern);
	}

	public boolean isNetdebug() {
		return getBoolean(NETDEBUG_KEY, false);
	}

	public void setNetdebug(boolean netdebug) {
		setBoolean(NETDEBUG_KEY, netdebug);
	}

	public boolean isAutosave() {
		return getBoolean(AUTOSAVE_KEY, false);
	}

	public void setAutosave(boolean autosave) {
		setBoolean(AUTOSAVE_KEY, autosave);
	}

}
