package jadex.simulation.analysis.common;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class GnuLogger extends Formatter
{
	public synchronized String format(LogRecord record)
	{
			StringBuffer buf = new StringBuffer();
			buf.append(record.getMessage());
			buf.append("\n");
			return buf.toString();
//		}
	}
}
