package com.putaotown.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.text.format.DateFormat;

public class TimeShowUtil
{
	public static String showGoodTime(long timeinmilles) {
		Calendar now = Calendar.getInstance();
		long x = now.getTimeInMillis() - timeinmilles;
		
		if (x <= 3 * 60 * 1000){
			return "刚刚";
		} else if(x > 3 * 60 * 1000 && x <= 60 * 60 * 1000) {
			int i = (int) x/(60 * 1000);
			return i+"分钟前";
		} else if (x > 60 * 60 * 1000 && x < 24 * 60 * 60 * 1000) {
			int i = (int)x/(60*60*1000);
			return i+"小时前";
		} else if (x > 24 * 60 * 60 * 1000 && x < 5 * 24 * 60 * 60 * 1000) {
			int i = (int)x/(24*60*60*1000);
			return i+"天前";
		} else {
			Calendar targetime = Calendar.getInstance();
			targetime.setTimeInMillis(timeinmilles);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(targetime.getTime());
		}
		
	}
}