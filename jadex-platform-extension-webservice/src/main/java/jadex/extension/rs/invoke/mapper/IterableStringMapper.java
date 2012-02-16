package jadex.extension.rs.invoke.mapper;

import java.util.Iterator;

import jadex.commons.SReflect;
import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 */
public class IterableStringMapper implements IValueMapper
{
	protected String delim;
	protected String prefix;
	protected String postfix;
	
	/**
	 * 
	 */
	public IterableStringMapper()
	{
		this(null, ",", null);
	}
	
	/**
	 * 
	 */
	public IterableStringMapper(String delim)
	{
		this(null, delim, null);		
	}
	
	/**
	 * 
	 */
	public IterableStringMapper(String prefix, String delim)
	{
		this(prefix, delim, null);
	}
	
	/**
	 * 
	 */
	public IterableStringMapper(String prefix, String delim, String postfix)
	{
		this.prefix = prefix;
		this.delim = delim;
		this.postfix = postfix;
	}
	
	/**
	 * 
	 */
	public Object convertValue(Object value) throws Exception
	{
		StringBuffer ret = new StringBuffer();
		if(prefix!=null)
			ret.append(prefix);
		for(Iterator<?> it = SReflect.getIterator(value); it.hasNext(); )
		{
			ret.append(it.next());
			if(delim!=null && it.hasNext())
				ret.append(delim);
		}
		if(postfix!=null)
			ret.append(postfix);
		return ret.toString();
	}
}
