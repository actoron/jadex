package jadex.commons;

import java.util.Collection;

/**
 * 
 */
public class SimpleParameterGuesser implements IParameterGuesser
{
	/** The parent. */
	protected IParameterGuesser parent;

	/** The values. */
	protected Collection<?> values;
	
	/**
	 * 
	 */
	public SimpleParameterGuesser(IParameterGuesser parent)
	{
		this(parent, null);
	}
	
	/**
	 * 
	 */
	public SimpleParameterGuesser(IParameterGuesser parent, Collection<?> values)
	{
		this.parent = parent;
		this.values = values;
	}
	
	/**
	 *  Guess a parameter.
	 *  @param type The type.
	 *  @return The mapped value. 
	 */
	public Object guessParameter(Class<?> type)
	{
		Object ret = null;
		if(parent!=null)
		{
			ret = parent.guessParameter(type);
		}
		else if(values!=null)
		{
			for(Object val: values)
			{
				if(val!=null && SReflect.isSupertype(val.getClass(), type))
				{
					ret = val;
					break;
				}
			}
		}
		return ret;
	}
}
