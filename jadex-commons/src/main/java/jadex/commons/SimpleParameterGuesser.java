package jadex.commons;

import java.util.Collection;

/**
 *  Guess parameter based on type.
 */
public class SimpleParameterGuesser implements IParameterGuesser
{
	/** The parent. */
	protected IParameterGuesser parent;

	/** The values. */
	protected Collection<?> values;
	
	/**
	 *  Create a new guesser.
	 */
	public SimpleParameterGuesser(IParameterGuesser parent)
	{
		this(parent, null);
	}
	
	/**
	 *  Create a new guesser.
	 */
	public SimpleParameterGuesser(Collection<?> values)
	{
		this(null, values);
	}
	
	/**
	 *  Create a new guesser.
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
	public Object guessParameter(Class<?> type, boolean exact)
	{
		Object ret = null;
		boolean found = false;
		if(values!=null)
		{
			for(Object val: values)
			{
				if(val!=null && ((exact && val.getClass().equals(SReflect.getWrappedType(type)))
					|| (!exact && SReflect.isSupertype(type, val.getClass()))))
				{
					ret = val;
					found = true;
					break;
				}
			}
		}
		if(!found && parent!=null)
		{
			ret = parent.guessParameter(type, exact); // throws exception if not found
			found = ret != null;
		}

		if(!found)
			throw new RuntimeException("No parameter value found for type: "+type);
		
		return ret;
	}
	
	/**
	 *  Get the parent guesser.
	 *  @return The parent guesser.
	 */
	public IParameterGuesser getParent()
	{
		return parent;
	}
	
	/**
	 *  Set the parent.
	 *  @param parent The parent.
	 */
	public void setParent(IParameterGuesser parent)
	{
		if(this.parent!=null)
			throw new IllegalStateException("Already has a parent!");
		this.parent = parent;
	}
	
}
