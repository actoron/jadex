package jadex.javaparser;

import java.util.HashMap;
import java.util.Map;

import jadex.commons.IValueFetcher;

/**
 *  Simple default implementation of a value fetcher useful for
 *  basic usage scenarios or as a base for extending. 
 */
public class SimpleValueFetcher implements IValueFetcher
{
	//-------- attributes --------
	
	/** The values. */
	protected Map values;
	
	/** The parent fetcher if any. */
	protected IValueFetcher	parent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new fetcher.
	 */
	public SimpleValueFetcher()
	{
	}
	
	/**
	 *  Create a new fetcher.
	 */
	public SimpleValueFetcher(IValueFetcher parent)
	{
		this.parent	= parent;
	}
	
	//-------- IValueFetcher methods --------
	
	/**
	 *  Fetch a value via its name.
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name)
	{
		Object ret	= null;
		
		if(name==null)
		{
			throw new RuntimeException("Name must not be null.");
		}
		else if(values!=null && values.containsKey(name))
		{
			ret = values.get(name);
		}
		else if(parent!=null)
		{
			ret	= parent.fetchValue(name);
		}
		else
		{
			throw new RuntimeException("Unknown parameter: "+name);
		}
		
		return ret;
	}
	
//	/**
//	 *  Fetch a value via its name from an object.
//	 *  @param name The name.
//	 *  @param object The object.
//	 *  @return The value.
//	 */
//	public Object fetchValue(String name, Object object)
//	{
//		Object	ret;
//		if(parent!=null)
//		{
//			ret	= parent.fetchValue(name, object);
//		}
//		else
//		{
//			throw new RuntimeException("Unkown object type: "+name);
//		}
//		
//		return ret;
//	}

	//-------- additional methods --------
	
	/**
	 *  Set a value.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public void setValue(String key, Object value)
	{
		if(values==null)
			values = new HashMap();
		values.put(key, value);
	}
	
	/**
	 *  Set values.
	 *  @param values The values map.
	 */
	public void setValues(Map<String, Object> values)
	{
		if(this.values==null)
			this.values = new HashMap();
		this.values.putAll(values);
	}
}
