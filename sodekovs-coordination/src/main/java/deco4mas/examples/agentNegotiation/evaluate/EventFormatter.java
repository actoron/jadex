package deco4mas.examples.agentNegotiation.evaluate;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class EventFormatter extends Formatter
{

	@Override
	public String format(LogRecord record)
	{
		StringBuffer buf = new StringBuffer(100);
		buf.append(record.getMessage());
		buf.append("\n");
		return buf.toString();
	}

}
