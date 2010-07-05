package jadex.simulation.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeConverter {

	/**
	 * Returns date value, presented as long value, as formatted string. 
	 * @param time
	 * @return
	 */
	public static String longTime2DateString(Long time) {
		return DateFormat.getDateTimeInstance().format(
				new Date(time.longValue()));
	}
	
	/**
	 * Returns date value, presented as string ("yyyy-MM-dd HH:mm:ss"), as long value.
	 * @param timeString
	 * @return
	 */
	public static long dateString2LongTime(String timeString){
		Date date = new Date();
		// Festlegung des Formats:
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		df.setTimeZone( TimeZone.getTimeZone( "Europe/Berlin" ) );		

		// Einlesen vom String:
		try {
			date = df.parse(timeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("#TimeConverter# Could not convert Time String into long: " + timeString);
			return -1;
		}	
		System.out.println("--> " + date.toLocaleString());
		return date.getTime();
	}
}