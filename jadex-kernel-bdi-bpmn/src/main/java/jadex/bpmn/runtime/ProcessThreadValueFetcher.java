package jadex.bpmn.runtime;

import jadex.javaparser.IValueFetcher;

import java.util.Map;

/**
 *  Value fetcher for process threads.
 */
public class ProcessThreadValueFetcher implements IValueFetcher
{
	//-------- attributes --------
	
	/** The process thread. */
	protected ProcessThread thread;

	/** The activity selection flag. */
	protected boolean flag;
	
	/** The fall back value fetcher (if any). */
	protected IValueFetcher fetcher;
	
	//-------- constructors --------
	
	/**
	 *  Create a value fetcher for a given process.
	 *  @param thread	The process thread.
	 *  @param flag	Flag to indicate that values should be fetched from the next activity (otherwise previous activity is used).
	 *  @param fetcher	The fall back fetcher, if any. 
	 */
	public ProcessThreadValueFetcher(ProcessThread thread, boolean flag, IValueFetcher fetcher)
	{
		this.thread = thread;
		this.flag	= flag;
		this.fetcher	 = fetcher;
	}
	
	//-------- methods --------

	/**
	 *  Fetch a named value from an object.
	 *  @param name The name.
	 *  @param object The object.
	 *  @return The fetched value.
	 */
	public Object fetchValue(String name, Object object)
	{
		if(object instanceof Map && ((Map)object).containsKey(name))
			return ((Map)object).get(name);
		else if(fetcher!=null)
			return fetcher.fetchValue(name, object);
		else
			throw new UnsupportedOperationException();
	}
	
	/**
	 *  Fetch a named value.
	 *  @param name The name.
	 *  @return The fetched value.
	 */
	public Object fetchValue(String name)
	{
		Object	value;

		if(thread.hasParameterValue(name))
			value	= thread.getParameterValue(name);
		else if(fetcher!=null)
			value	= fetcher.fetchValue(name);
		else
			throw new RuntimeException("Parameter not found: "+name);
		
		return value;
	}
}
