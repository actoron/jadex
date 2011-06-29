package jadex.commons.future;

import jadex.commons.SReflect;

/**
 *  Exception indicating that the result of a future was set twice.
 */
public class DuplicateResultException	extends RuntimeException
{
	//-------- attributes --------
	
	/** The future. */
	protected IFuture	future;
	
	/** The first result. */
	protected Object	first;
	
	/** The second result. */
	protected Object	second;
	
	//-------- constructors --------
	
	/**
	 *  Create a duplicate result exception.
	 */
	public DuplicateResultException(IFuture future, Object first, Object second)
	{
		this.future	= future;
		this.first	= first;
		this.second	= second;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the future.
	 */
	public IFuture	getFuture()
	{
		return future;
	}
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return SReflect.getInnerClassName(getClass())+"(first="+first+", second="+second+")";
	}
}
