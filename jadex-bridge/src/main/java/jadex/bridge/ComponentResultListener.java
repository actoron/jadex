package jadex.bridge;

import jadex.commons.future.IResultListener;

/**
 *  The result listener for executing listener invocations as a component step.
 */
public class ComponentResultListener<E> implements IResultListener<E>
{
	//-------- attributes --------
	
	/** The result listener. */
	protected IResultListener<E> listener;
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener<E> listener, IComponentAdapter adapter)
	{
		if(listener==null)
			throw new NullPointerException("Listener must not null.");
		this.listener = listener;
		this.adapter = adapter;
	}

	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void resultAvailable(final E result)
	{
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable()
				{
					public void run()
					{
						listener.resultAvailable(result);
					}
					
					public String toString()
					{
						return "resultAvailable("+result+")_#"+this.hashCode();
					}
				});
			}
			catch(Exception e)
			{
				listener.exceptionOccurred(e);
			}
		}
		else
		{
			listener.resultAvailable(result);
		}		
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(final Exception exception)
	{
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable()
				{
					public void run()
					{
						listener.exceptionOccurred(exception);
					}
					
					public String toString()
					{
						return "exceptionOccurred("+exception+")_#"+this.hashCode();
					}
				});
			}
			catch(Exception e)
			{
				listener.exceptionOccurred(e);
			}
		}
		else
		{
			listener.exceptionOccurred(exception);
		}
	}
}
