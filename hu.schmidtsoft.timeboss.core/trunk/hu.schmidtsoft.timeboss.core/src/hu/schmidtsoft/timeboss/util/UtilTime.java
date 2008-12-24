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

	public static String timeToFileName(long startTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss_SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date(startTime));
	}
}
