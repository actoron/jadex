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
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public ProcessThreadValueFetcher(ProcessThread thread)
	{
		this.thread = thread;;
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
		if(object instanceof Map)
			return ((Map)object).get(name);
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
		Map	oldvalues = thread.getLastEdge()!=null ? thread.getContext(thread.getLastEdge().getSource().getName()) : null;
		Object	value	= oldvalues!=null ? oldvalues.get(name) : null;
		if(value==null)
		{
			value	= thread.getContext(name);
		}
		return value;
	}
}
