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
		Map	oldvalues = thread.getLastEdge()!=null ? thread.getData(flag ? thread.getLastEdge().getTarget().getName() : thread.getLastEdge().getSource().getName()) : null;
		if(oldvalues!=null && oldvalues.containsKey(name))
			value	= oldvalues!=null ? oldvalues.get(name) : null;
		else
			value	= thread.getData(name);
		
		if(value==null && fetcher!=null)
			value	= fetcher.fetchValue(name);
		
		return value;
	}
}
