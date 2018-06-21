package jadex.wfms.service.listeners;

import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LogEvent extends SimplePropertyObject
{
	public static final String EVENT_TYPE_CREATION = "Created";
	public static final String EVENT_TYPE_DISPOSAL = "Disposed";
	
	public static final String EVENT_SOURCE_PROCESS_INSTANCE	= "Process Instance";
	public static final String EVENT_SOURCE_PROCESS_GOAL		= "Process Goal";
	public static final String EVENT_SOURCE_PROCESS_PLAN		= "Process Plan";
	public static final String EVENT_SOURCE_PROCESS_ACTIVITY	= "Process Activity";
	
	public static final String EVENT_TYPE_PROPERTY	= "Event Type";
	public static final String SOURCE_TYPE_PROPERTY	= "Source Type";
	public static final String SOURCE_NAME_PROPERTY	= "Source Name";
	public static final String REASON_PROPERTY		= "Reason";
	
	public LogEvent(String eventType, String sourceType, String sourceName)
	{
		setProperty(EVENT_TYPE_PROPERTY, eventType);
		setProperty(SOURCE_TYPE_PROPERTY, sourceType);
		setProperty(SOURCE_NAME_PROPERTY, sourceName);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		Map props = new HashMap(getProperties());
		String et = (String) props.remove(EVENT_TYPE_PROPERTY);
		if (et != null)
		{
			sb.append(et);
			sb.append(": ");
		}
		String st = (String) props.remove(SOURCE_TYPE_PROPERTY);
		if (st != null)
			sb.append(st);
		String sn = (String) props.remove(SOURCE_NAME_PROPERTY);
		if (sn != null)
		{
			sb.append(" ");
			sb.append(sn);
		}
		
		for (Iterator it = props.entrySet().iterator(); it.hasNext(); )
		{
			sb.append(", ");
			Map.Entry entry = (Map.Entry) it.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
		
		return sb.toString();
	}
}
