package jadex.commons;

/** 
 * Static methods for parsing objects (e.g. for objects found in maps or as strings).
 */
public class SConfigParser
{
	/**
	 *  Parses a boolean value.
	 *  
	 *  @param argval Argument value.
	 *  @return Value.
	 */
	public static final boolean getBoolValue(Object argval)
	{
		return getBoolValue(argval, false);
	}
	
	/**
	 *  Parses an boolean value.
	 *  
	 *  @param argval Argument value. 
	 *  @param defaultval Default value if unparsable.
	 *  @return Value.
	 */
	public static final boolean getBoolValue(Object argval, boolean defaultval)
	{
		boolean ret = defaultval;
		if (argval instanceof Boolean)
		{
			ret = (Boolean) argval;
		}
		else if (argval instanceof Boolean3 && !Boolean3.NULL.equals(((Boolean3) argval)))
		{
			ret = ((Boolean3) argval).toBoolean();
		}
		else if (argval instanceof String)
		{
			try
			{
				ret = Boolean.parseBoolean(((String) argval).trim().toLowerCase());
			}
			catch (Exception e)
			{
			}
		}
		return ret;
	}
	
	/**
	 *  Parses an integer value.
	 *  
	 *  @param argval Argument value.
	 *  @return Value.
	 */
	public static final int getIntValue(Object argval)
	{
		return getIntValue(argval, -1);
	}
	
	/**
	 *  Parses an integer value.
	 *  
	 *  @param argval Argument value. 
	 *  @param defaultval Default value if unparsable.
	 *  @return Value.
	 */
	public static final int getIntValue(Object argval, int defaultval)
	{
		int ret = defaultval;
		if (argval instanceof Integer)
		{
			ret = (Integer) argval;
		}
		else if (argval instanceof String)
		{
			try
			{
				ret = Integer.parseInt((String) argval);
			}
			catch (Exception e)
			{
			}
		}
		return ret;
	}
	
	/**
	 *  Parses an long value.
	 *  
	 *  @param argval Argument value.
	 *  @return Value.
	 */
	public static final long getLongValue(Object argval)
	{
		return getLongValue(argval, -1L);
	}
	
	/**
	 *  Parses an long value.
	 *  
	 *  @param argval Argument value. 
	 *  @param defaultval Default value if unparsable.
	 *  @return Value.
	 */
	public static final long getLongValue(Object argval, long defaultval)
	{
		long ret = defaultval;
		if (argval instanceof Long)
		{
			ret = (Long) argval;
		}
		else if (argval instanceof String)
		{
			try
			{
				ret = Long.parseLong((String) argval);
			}
			catch (Exception e)
			{
			}
		}
		return ret;
	}
}
