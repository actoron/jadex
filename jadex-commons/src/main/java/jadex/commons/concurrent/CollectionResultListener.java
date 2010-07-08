package jadex.commons.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 *  Collection result listener collects a number of results and return a collection.
 */
public class CollectionResultListener	implements IResultListener
{
	//-------- attributes --------
	
	/** The number of sub listeners to wait for. */
	protected int num;
	
	/** The original result collection. */
	protected Collection	results;

	/** The synchronized result collection. */
	protected Collection	sresults;

	/** The delegate result listener. */
	protected IResultListener	delegate;
	
	/** Flag to indicate that the delegate already has been notified. */
	protected boolean	notified;

	
	//-------- constructors --------
	
	/**
	 *  Create a new collection listener.
	 *  @param num The expected number of results.
	 *  @param delegate	The delegate result listener.
	 */
	public CollectionResultListener(int num, IResultListener delegate)
	{
		this(num, delegate, new ArrayList());
	}
	
	/**
	 *  Create a new collection listener.
	 *  @param num The expected number of (additional) results.
	 *  @param results	The collection to be used for collecting results.
	 *  @param delegate	The delegate result listener.
	 */
	public CollectionResultListener(int num, IResultListener delegate, Collection results)
	{
		this.results	= results;
		this.sresults	= Collections.synchronizedCollection(results);
		this.num = num + this.sresults.size();
		this.delegate	= delegate;
//		System.out.println("CollectionResultListener: "+this+", "+num);
		
		if(num==this.sresults.size())
		{
			this.notified	= true;
//			System.out.println("collecting finished: "+this+", "+this.sresults.size());
			delegate.resultAvailable(null, results);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Called when some result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(Object source, Object result)
	{
		boolean	notify	= false;
		synchronized(this)
		{
			if(!notified)
			{
				sresults.add(result);
//				System.out.println("resultAvailable: "+this+", "+this.sresults.size());
				notify	= num==this.sresults.size();
				notified	= notify;
			}
		}

		if(notify)
		{
//			System.out.println("collecting finished: "+this+", "+this.sresults.size());
			delegate.resultAvailable(null, results);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Object source, Exception exception)
	{
		boolean	notify	= false;
		synchronized(this)
		{
			if(!notified)
			{
				notify	= true;
				notified	= true;
			}
		}

		if(notify)
		{
//			System.out.println("exceptionOcurred: "+this+", "+this.sresults.size());
			delegate.exceptionOccurred(null, exception);
		}
	}
}
