package jadex.bpmn.runtime;

import java.util.HashMap;
import java.util.Map;

import jadex.commons.IValueFetcher;

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
	
	/** The ifdefined map. */
	protected Map<String, Boolean> ifdef;
	
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

//	/**
//	 *  Fetch a named value from an object.
//	 *  @param name The name.
//	 *  @param object The object.
//	 *  @return The fetched value.
//	 */
//	public Object fetchValue(String name, Object object)
//	{
//		Object ret;
//		if(object instanceof Map && ((Map)object).containsKey(name))
//			ret = ((Map)object).get(name);
//		else if(object instanceof IMessageAdapter && ((IMessageAdapter)object).getParameterMap().containsKey(name))
//			ret = ((IMessageAdapter)object).getValue(name);
//		else if("$thread".equals(name))
//			ret = thread;
//		else if(fetcher!=null)
//			ret = fetcher.fetchValue(name, object);
//		else
//			throw new UnsupportedOperationException();
//	
//		return ret;
//	}
	
	/**
	 *  Fetch a named value.
	 *  @param name The name.
	 *  @return The fetched value.
	 */
	public Object fetchValue(String name)
	{
		boolean	found	= false;
		Object	value	= null;
		
		// we do not need a loop here as hasParameterValue() is build recursively
		// Check for parameter value.
//		for(ProcessThread t=thread; t!=null && !found; t=t.getParent() )
//		{
			if(thread.hasParameterValue(name))
			{
				value	= thread.getParameterValue(name);
				found	= true;
			}
			// todo: remove this sucking stuff below
			String paramname = name.startsWith("$")? name.substring(1) : name;
			if(thread.hasParameterValue(paramname))
			{
				value	= thread.getParameterValue(paramname);
				found	= true;
			}
//		}
				
		if(!found)
		{
			if("$thread".equals(name))
			{
				value = thread;
				found = true;
			}
			else if("$ifdef".equals(name) || "ifdef".equals(name))
			{
				if(ifdef==null)
					ifdef = new IfDefMap();

				value = ifdef;
				found = true;
			}
		}
		
		// Ask contained fetcher.
		if(!found && fetcher!=null)
		{
			value	= fetcher.fetchValue(name);
		}
		else if(!found)
		{
			throw new RuntimeException("Parameter not found: "+name);
		}
		
		return value;
	}
	
	/**
	 * 
	 */
	public class IfDefMap extends HashMap<String, Boolean>
	{
		public Boolean get(Object key) 
		{
//			System.out.println("isdef: "+thread.hasParameterValue((String)key)+" "+key);
			return thread.hasParameterValue((String)key)? Boolean.TRUE: Boolean.FALSE;
		}
	}
}
