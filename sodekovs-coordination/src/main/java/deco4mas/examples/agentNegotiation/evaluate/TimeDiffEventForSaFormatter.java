package deco4mas.examples.agentNegotiation.evaluate;

import jadex.bridge.IComponentIdentifier;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class TimeDiffEventForSaFormatter extends Formatter
{
	@Override
	public String format(LogRecord record)
	{
		if (!record.getMessage().equals("titles"))
		{
			Object[] param = record.getParameters();
			Long startTime = (Long) param[0];
			Long time = (Long) param[1];
			IComponentIdentifier sa = (IComponentIdentifier) param[2];
			Integer number = AgentLogger.getNumber(sa.getLocalName());
			if (param.length > 3)
			{
				number = (Integer) param[3];
			}

			StringBuffer buf = new StringBuffer(100);
			for (int i = 0; i < number; i++)
			{
				buf.append("x x x ");
			}
			Long timevalue = (time - startTime);
			Double dbtv = timevalue.doubleValue();
			dbtv = dbtv / 1000;

			if (record.getMessage().equals(""))
			{
				buf.append(dbtv.toString() + " " + AgentLogger.getNumber(sa.getLocalName()) + " " + sa.getLocalName());
			} else
			{
				buf.append(dbtv.toString() + " " + record.getMessage() + " " + sa.getLocalName());
			}
			buf.append("\n");
			return buf.toString();
		} else
		{
			StringBuffer buf = new StringBuffer(100);
			for (String name : AgentLogger.getAllSas())
			{
				if (record.getLoggerName().contains("TrustChange"))
					buf.append("0 30 " + name + " ");
				else
					buf.append("x x " + name + " ");
			}
			buf.append("\n");
			return buf.toString();
		}
	}

}
