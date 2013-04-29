package com.yutel.alog.comm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

import com.yutel.alog.vo.NetCfg;

public class Comm {
	public final static String TAG = "alog";

	public static NetCfg netcfg() {
		NetCfg nc = null;
		try {
			for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
					.getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
				NetworkInterface intf = mEnumeration.nextElement();
				for (Enumeration<InetAddress> enumIPAddr = intf
						.getInetAddresses(); enumIPAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIPAddr.nextElement();
					// 如果不是回环地址
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						nc = new NetCfg();
						nc.setIp(inetAddress.getHostAddress().toString());
						nc.setName(intf.getName());
					}
				}
			}
			if (nc != null) {
				File file = new File("/sys/class/net/" + nc.getName()
						+ "/address");
				BufferedReader br = new BufferedReader(new FileReader(file));
				String temp = null;
				StringBuffer sb = new StringBuffer();
				temp = br.readLine();
				while (temp != null) {
					sb.append(temp);
					temp = br.readLine();
				}
				nc.setMac(sb.toString());
			}
			return nc;
		} catch (Exception e) {
			Log.e(TAG, "InetAddress", e);
			return null;
		}
	}

	public static void shell(String cmd) {
		try {
			Log.i(TAG, "start cmd=" + cmd);
			Process exeEcho = Runtime.getRuntime().exec("su");
			exeEcho.getOutputStream().write(cmd.getBytes());
			exeEcho.getOutputStream().flush();
			Log.i(TAG, "finished");
		} catch (Exception e) {
			Log.e(TAG, "shell error!", e);
		}
	}

	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}
}
