package com.yutel.alog.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class LogStream extends Thread {
	InputStream is;
	String type;
	OnLogListener mOnLogListener;

	public LogStream(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	public void setOnLogListener(OnLogListener logListener) {
		mOnLogListener = logListener;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (mOnLogListener != null)
					mOnLogListener.readLine(line);
			}
			Thread.sleep(10000);
			Log.e(Comm.TAG, "sleep 10 seconds");
		} catch (IOException e) {
			Log.e(Comm.TAG, "readLine error", e);
		} catch (InterruptedException e) {
			Log.e(Comm.TAG, "sleep error", e);
		}
	}

	public interface OnLogListener {
		public void readLine(String line);
	}
}
