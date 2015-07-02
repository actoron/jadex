package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Element that can be finished with processing.
 */
public abstract class RFinishableElement extends RProcessableElement
{
	//-------- attributes --------
	
	/** The exception. */
	protected Exception exception;
	
	/** The listeners. */
	protected List<IResultListener<Void>> listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new element.
	 */
	public RFinishableElement(MProcessableElement modelelement, Object pojoelement, IInternalAccess agent, Map<String, Object> vals, MConfigParameterElement config)
	{
		super(modelelement, pojoelement, agent, vals, config);
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new listener to get notified when the goal is finished.
	 *  @param listener The listener.
	 */
	public void addListener(IResultListener<Void> listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IResultListener<Void>>();
		
		if(isSucceeded())
		{
			listener.resultAvailable(null);
		}
		else if(isFailed())
		{
			listener.exceptionOccurred(exception);
		}
		else
		{
			listeners.add(listener);
		}
	}

	/**
	 *  Remove a listener.
	 */
	public void removeListener(IResultListener<Void> listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
	/**
	 *  Get the listeners.
	 *  @return The listeners.
	 */
	public List<IResultListener<Void>> getListeners()
	{
		return listeners;
	}

	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
	/**
	 *  Notify the listeners.
	 */
	public void notifyListeners()
	{
		if(getListeners()!=null)
		{
			for(IResultListener<Void> lis: getListeners())
			{
				if(isSucceeded())
				{
					lis.resultAvailable(null);
				}
				else if(isFailed())
				{
					lis.exceptionOccurred(exception);
				}
			}
		}
	}
	
	/**
	 *  Test if element is succeeded.
	 */
	public abstract boolean	isSucceeded();
	
	/**
	 *  Test if element is failed.
	 */
	public abstract boolean	isFailed();
	
	/**
	 *  Test if goal is finished.
	 *  @return True, if is finished.
	 */
	public boolean isFinished()
	{
		return isSucceeded() || isFailed();
	}
}
