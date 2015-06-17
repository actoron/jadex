package jadex.commons.future;


import java.util.ArrayList;
import java.util.Collection;


/**
 *  Collection result listener collects a number of results and return a collection.
 */
public class CollectionResultListener<E> implements IResultListener<E>, IUndoneResultListener<E>
{
	//-------- attributes --------
	
	/** The number of sub listeners to wait for. */
	protected int num;
	
	/** The original result collection. */
	protected Collection<E>	results;

	/** The delegate result listener. */
	protected IResultListener<Collection<E>>delegate;
	
	/** Flag to indicate that the delegate already has been notified. */
	protected boolean notified;

	/** Flag to indicate that failures should be ignored and only valid results returned. */
	protected boolean ignorefailures;

	/** The undone flag. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new collection listener that uses default exception handling/logging.
	 *  @param num The expected number of results.
	 *  @param resultDelegate	The functional delegate result listener.
	 */
	public CollectionResultListener(int num, IFunctionalResultListener<Collection<E>> resultDelegate)
	{
		this(num, resultDelegate, null);
	}
	
	/**
	 *  Create a new collection listener.
	 *  @param num The expected number of results.
	 *  @param resultDelegate	The functional delegate result listener.
	 * @param exceptionDelegate The functional delegate exception listener.
	 *        Passing <code>null</code> enables default exception logging.
	 */
	public CollectionResultListener(int num, IFunctionalResultListener<Collection<E>> resultDelegate, IFunctionalExceptionListener exceptionDelegate)
	{
		this(num, false, resultDelegate, exceptionDelegate);
	}
	
	/**
	 *  Create a new collection listener that stops on failures.
	 *  @param num The expected number of results.
	 *  @param delegate	The delegate result listener.
	 */
	public CollectionResultListener(int num, IResultListener<Collection<E>> delegate)
	{
		this(num, false, delegate);
	}
	
	/**
	 * Create a new collection listener.
	 * 
	 * @param num The expected number of results.
	 * @param ignorefailures When set to true failures will be tolerated and
	 *        just not be added to the result collection.
	 * @param resultDelegate The functional delegate result listener.
	 * @param exceptionDelegate The functional delegate exception listener.
	 *        Passing <code>null</code> enables default exception logging.
	 */
	public CollectionResultListener(int num, boolean ignorefailes, IFunctionalResultListener<Collection<E>> resultDelegate, IFunctionalExceptionListener exceptionDelegate)
	{
		this(num, ignorefailes, SResultListener.createResultListener(resultDelegate, exceptionDelegate));
	}
	
	/**
	 *  Create a new collection listener.
	 *  @param num The expected number of results.
	 *  @param ignorefailures When set to true failures will be 
	 *  	tolerated and just not be added to the result collection.
	 *  @param delegate	The delegate result listener.
	 */
	public CollectionResultListener(int num, boolean ignorefailures, IResultListener<Collection<E>> delegate)
	{
		this.num = num;
		this.ignorefailures	= ignorefailures;
		this.delegate	= delegate;
		this.results	= new ArrayList<E>();
//		System.out.println("CollectionResultListener: "+this+", "+num);
		
		if(num==0)
		{
			this.notified	= true;
//			System.out.println("collecting finished: "+this+", "+this.sresults.size());
			delegate.resultAvailable(results); // todo: undone???
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Called when some result is available.
	 * @param result The result.
	 */
	public void resultAvailable(E result)
	{
		boolean	notify	= false;
		synchronized(this)
		{
			if(!notified)
			{
				results.add(result);
				notify	= num==this.results.size();
				notified	= notify;
			}
		}

		if(notify)
		{
//			System.out.println("collecting finished: "+this+", "+this.sresults.size());
			if(undone && delegate instanceof IUndoneIntermediateResultListener)
			{
				((IUndoneIntermediateResultListener<E>)delegate).resultAvailableIfUndone(results);
			}
			else
			{
				delegate.resultAvailable(results);
			}
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		boolean	notify	= false;
		synchronized(this)
		{
			if(ignorefailures)
			{
				num--;
				notify	= num==this.results.size();
				notified	= notify;
			}
			else if(!notified)
			{
				notify	= true;
				notified	= true;
			}
		}

		if(notify)
		{
//			System.out.println("exceptionOcurred: "+this+", "+this.sresults.size());
//			
			if(ignorefailures)
			{
				if(undone && delegate instanceof IUndoneIntermediateResultListener)
				{
					((IUndoneIntermediateResultListener<E>)delegate).resultAvailableIfUndone(results);
				}
				else
				{
					delegate.resultAvailable(results);
				}
			}
			else
			{
				if(undone && delegate instanceof IUndoneIntermediateResultListener)
				{
					((IUndoneIntermediateResultListener<E>)delegate).exceptionOccurredIfUndone(exception);
				}
				else
				{
					delegate.exceptionOccurred(exception);
				}
			}
		}
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result)
	{
		undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		undone = true;
		exceptionOccurred(exception);
	}

	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
	
	/**
	 *  Get the result count.
	 *  @return The result count.
	 */
	public int getResultCount()
	{
		return results.size();
	}
}
