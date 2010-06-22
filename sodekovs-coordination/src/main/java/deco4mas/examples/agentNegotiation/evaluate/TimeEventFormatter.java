package deco4mas.examples.agentNegotiation.evaluate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class TimeEventFormatter extends Formatter
{
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	public synchronized String format(LogRecord record)
	{
		if (record.getMessage().equals("new"))
		{
			return "\n";
		} else
		{
			StringBuffer buf = new StringBuffer();
			Date date = new Date();
			date.setTime(record.getMillis());
			// buf.append(df.format(date) + ": ");
			buf.append(record.getMessage());
			buf.append("\n");
			return buf.toString();
		}
	}
}
