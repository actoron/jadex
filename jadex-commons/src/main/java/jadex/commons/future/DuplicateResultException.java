package jadex.commons.future;

import jadex.commons.SReflect;

/**
 *  Exception indicating that the result of a future was set twice.
 */
public class DuplicateResultException	extends RuntimeException
{
	//-------- constants --------
	
	/** Two results. */
	public static final int	TYPE_RESULT_RESULT	= 1;
	
	/** First result then exception. */
	public static final int	TYPE_RESULT_EXCEPTION	= 2;
	
	/** First exception then result. */
	public static final int	TYPE_EXCEPTION_RESULT	= 3;
	
	/** Two exceptions. */
	public static final int	TYPE_EXCEPTION_EXCEPTION	= 4;
	
	//-------- attributes --------
	
	/** The type. */
	protected int	type;
	
	/** The future. */
	protected IFuture<?>	future;
	
	/** The first result. */
	protected Object	first;
	
	/** The second result. */
	protected Object	second;
	
	//-------- constructors --------
	
	/**
	 *  Create a duplicate result exception.
	 */
	public DuplicateResultException(int type, IFuture<?> future, Object first, Object second)
	{
		this.type	= type;
		this.future	= future;
		this.first	= first;
		this.second	= second;
		printStackTrace();
		if(first instanceof Exception)
			((Exception)first).printStackTrace();
		if(second instanceof Exception)
			((Exception)second).printStackTrace();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the future.
	 */
	public IFuture<?>	getFuture()
	{
		return future;
	}
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return type==TYPE_RESULT_RESULT ? SReflect.getInnerClassName(getClass())+"(result1="+first+", result2="+second+")"
				: type==TYPE_RESULT_EXCEPTION ? SReflect.getInnerClassName(getClass())+"(result1="+first+", exception2="+second+")"
				: type==TYPE_EXCEPTION_RESULT ? SReflect.getInnerClassName(getClass())+"(exception1="+first+", result2="+second+")"
				: SReflect.getInnerClassName(getClass())+"(exception1="+first+", exception2="+second+")";
	}
	
	/**
	 *  Prints also stack trace of first exception, if available.
	 */
	public void printStackTrace()
	{
		System.err.println("Future: "+future);
		if(future instanceof Future && ((Future<?>)future).creation!=null)
		{
			((Future<?>)future).creation.printStackTrace();
		}
		if(future instanceof Future && ((Future<?>)future).first!=null)
		{
			((Future<?>)future).first.printStackTrace();
		}
		super.printStackTrace();
	}
}
