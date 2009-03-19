package hu.schmidtsoft.timeboss.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UtilTime {
	public static long minToMilli(long min)
	{
		return min*60*1000;
	}

	public static String timeToString(long lastActivityEntryTime) {
		if(lastActivityEntryTime<0)
		{
			return "never";
		}
		return ""+new Date(lastActivityEntryTime);
	}
	/**
	 * Convert timestamp to user readable time.
	 * The format is: yyyy-MM-dd-HH:mm:ss
	 * The time zone used is the system's default that can be queried using getDefaultTimeZone
	 * @param lastActivityEntryTime
	 * @return
	 */
	public static String timeToCSVString(long lastActivityEntryTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date(lastActivityEntryTime));
	}
	public static TimeZone getDefaultTimeZone() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		return sdf.getTimeZone();
	}

	public static String timeToFileName(long startTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss_SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date(startTime));
	}
}
