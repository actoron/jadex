package jadex.bdi.runtime;

import java.io.Serializable;

/**
 *  A filter checks if an object matches
 *  some given description.
 */
public interface IFilter
{
	//-------- constants --------

	/** A filter that never matches. */
	public static final IFilter NEVER = new ConstantFilter(false);

	/** A filter that always matches. */
	public static final IFilter ALWAYS = new ConstantFilter(true);

	//-------- methods --------

	/**
	 *  Match an object against the filter.
	 *  Exceptions are interpreted as non-match.
	 *  @param object The object.
	 *  @return True, if the filter matches.
	 */
	public boolean filter(Object object)	throws Exception;
}

//-------- helper classes --------

/**
 *  A constant filter always returns the same value,
 *  regardless of the object to match.
 */
class ConstantFilter	implements IFilter, Serializable
{
	//-------- attributes --------

	/** The constant value. */
	protected boolean	val;

	//-------- constructors --------

	/** Create a constant filter. */
	public ConstantFilter(boolean val)
	{
		this.val	= val;
	}

	//-------- methods --------

	/** IFilter interface. */
	public boolean filter(Object o)
	{
		return val;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ConstantFiler("+val+")";
	}
}

