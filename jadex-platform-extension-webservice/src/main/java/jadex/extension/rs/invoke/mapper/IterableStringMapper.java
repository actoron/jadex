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
	protected IValueMapper submapper;
	
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
	public IterableStringMapper(String delim, IValueMapper submapper)
	{
		this(null, delim, null, submapper);		
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
		this(prefix, delim, postfix, null);
	}
	
	/**
	 * 
	 */
	public IterableStringMapper(String prefix, String delim, String postfix, IValueMapper submapper)
	{
		this.prefix = prefix;
		this.delim = delim;
		this.postfix = postfix;
		this.submapper = submapper;
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
			Object next = it.next();
			if(submapper!=null)
				next = submapper.convertValue(next);
			ret.append(next);
			if(delim!=null && it.hasNext())
				ret.append(delim);
		}
		if(postfix!=null)
			ret.append(postfix);
		return ret.toString();
	}
}