package jadex.bridge.service.component.interceptors;

import java.util.Map;

/**
 *  Struct for sending spans.
 */
public class SpanContextInfo 
{
	/** The values. */
	protected Map<String, String> values;
	
	/**
	 *  Create a new span context info.
	 */
	public SpanContextInfo() 
	{
	}

	/**
	 *  Create a new span context info.
	 */
	public SpanContextInfo(Map<String, String> values) 
	{
		this.values = values;
	}

	/**
	 * @return the values
	 */
	public Map<String, String> getValues() 
	{
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Map<String, String> values) 
	{
		this.values = values;
	}
}
