package jadex.extension.rs.invoke.mapper;

import java.util.Iterator;

import jadex.commons.SReflect;
import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 *  The iterable string mapper can be used to map an object
 *  that can be iterated over (e.g. collection, array, enumeration, ...)
 *  to a string with definable delimiter (e.g. ",").
 *  In addition a prefix and postfix can be given which are
 *  pre- and postpended to the string.
 *  A submapper can be used to handle more complex cases in
 *  a recursive way. For each element the submapper is invoked
 *  so that e.g. multidimensional arrays can be processed.
 */
public class IterableStringMapper implements IValueMapper
{
	//-------- attributes --------
	
	/** The delimiter. */
	protected String delim;
	
	/** The string prefix, */
	protected String prefix;
	
	/** The string postfix. */
	protected String postfix;
	
	/** The optional submapper. */
	protected IValueMapper submapper;
	
	//-------- constructors --------
	
	/**
	 *  Create a new string mapper.
	 */
	public IterableStringMapper()
	{
		this(null, ",", null);
	}
	
	/**
	 *  Create a new string mapper.
	 *  @param delim The delimiter that is placed between elements.
	 */
	public IterableStringMapper(String delim)
	{
		this(null, delim, null);		
	}
	
	/**
	 *  Create a new string mapper.
	 *  @param delim The delimiter that is placed between elements.
	 *  @param submapper The submapper that will be invoked for each element.
	 */
	public IterableStringMapper(String delim, IValueMapper submapper)
	{
		this(null, delim, null, submapper);		
	}
	
	/**
	 *  Create a new string mapper.
	 *  @param prefix The prefix for the result string.
	 *  @param delim The delimiter that is placed between elements.
	 */
	public IterableStringMapper(String prefix, String delim)
	{
		this(prefix, delim, null);
	}
	
	/**
	 *  Create a new string mapper.
	 *  @param prefix The prefix for the result string.
	 *  @param delim The delimiter that is placed between elements.
	 *  @param postfix The postfix for the result string.
	 */
	public IterableStringMapper(String prefix, String delim, String postfix)
	{
		this(prefix, delim, postfix, null);
	}
	
	/**
	 *  Create a new string mapper.
	 *  @param prefix The prefix for the result string.
	 *  @param delim The delimiter that is placed between elements.
	 *  @param postfix The postfix for the result string.
	 *  @param submapper The submapper that will be invoked for each element.
	 */
	public IterableStringMapper(String prefix, String delim, String postfix, IValueMapper submapper)
	{
		this.prefix = prefix;
		this.delim = delim;
		this.postfix = postfix;
		this.submapper = submapper;
	}
	
	//-------- methods --------
	
	/**
	 *  Convert the given value.
	 *  @param value The value to convert.
	 *  @return The converted value.
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