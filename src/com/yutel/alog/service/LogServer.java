package com.yutel.alog.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.yutel.alog.comm.Comm;
import com.yutel.alog.comm.LogStream;
import com.yutel.alog.comm.LogStream.OnLogListener;
import com.yutel.alog.comm.Prefs;

public class LogServer {
	private static LogServer mLogServer;
	private static final SimpleDateFormat LOG_FILE_FORMAT;
	private static final File path;

	private Prefs mPrefs;
	BufferedWriter bw;
	Process proc;

	static {
		LOG_FILE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ssZ");
		path = new File(Environment.getExternalStorageDirectory(), "alog");
		if (path != null && !path.exists()) {
			path.mkdir();
		}
	}

	private LogServer(Context context) {
		mPrefs = new Prefs(context);
	}

	public static LogServer getInstance(Context context) {
		if (mLogServer == null) {
			mLogServer = new LogServer(context);

		}
		return mLogServer;
	}

	public void start() {
		try {
			String[] cmd = { "logcat", "-v", mPrefs.getFormat().getValue(),
					"-b", mPrefs.getBuffer().getValue(),
					"*:" + mPrefs.getLevel() + "\n" };
			File file = new File(path + "/alog."
					+ LOG_FILE_FORMAT.format(new Date()) + ".txt");
			file.createNewFile();
			bw = new BufferedWriter(new FileWriter(file), 1024);
			proc = Runtime.getRuntime().exec(cmd);
			LogStream print = new LogStream(proc.getInputStream(), "OUT");
			print.setOnLogListener(new OnLogListener() {
				@Override
				public void readLine(String line) {
					try {
						bw.write(line + "\n");
					} catch (IOException e) {
						Log.e(Comm.TAG, "error write log", e);
					}
				}
			});
			print.start();
		} catch (IOException e) {
			Log.e(Comm.TAG, "error create log file", e);
		}
	}

}
