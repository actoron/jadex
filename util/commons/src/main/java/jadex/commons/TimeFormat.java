package jadex.commons;

import java.text.DecimalFormat;

/**
 *  Class for formatting time durations.
 */
public class TimeFormat //extends Format
{
	//protected static final DecimalFormat	zd = new DecimalFormat("#####");
	protected static final DecimalFormat	od = new DecimalFormat("####0");
	protected static final DecimalFormat	twd = new DecimalFormat("###00");
	protected static final DecimalFormat	thd = new DecimalFormat("##000");

	/**
	 *  Format the time.
	 *  @param millis The duration in millis.
	 *  @return The string representation.
	 */
	public static String format(long millis)
	{
		String ret = "n/a";
		
		if(millis >= 0)
		{
			long secs = millis/1000;
			millis %= 1000;
			long mins = secs/60;
			secs %= 60;
			long hours = mins/60;
			mins %= 60;
			long days = hours/24;
			hours %= 24;
			long years = days /365;
			days %= 365;
			
			ret = (years>0?twd.format(years)+":":"")
				+(days>0?thd.format(days)+":":"")
				+(hours>0?twd.format(hours)+":":"")
				+(mins>0?twd.format(mins)+":":"")
				+twd.format(secs)+"."
				+thd.format(millis);
		}
		
		return ret;
	}
}