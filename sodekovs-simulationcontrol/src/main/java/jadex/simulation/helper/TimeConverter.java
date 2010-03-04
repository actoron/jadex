package jadex.simulation.helper;

import java.text.DateFormat;
import java.util.Date;

public class TimeConverter {

	public static String longTime2DateString(Long time) {
		return DateFormat.getDateTimeInstance().format(
				new Date(time.longValue()));
	}
}