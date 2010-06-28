package jadex.bridge;

import jadex.commons.concurrent.IResultListener;

/**
 *  The result listener for executing listener invocations as a component step.
 */
public class ComponentResultListener implements IResultListener
{
	//-------- attributes --------
	
	/** The result listener. */
	protected IResultListener listener;
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener listener, IComponentAdapter adapter)
	{
		this.listener = listener;
		this.adapter = adapter;
	}

	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param source The source component.
	 *  @param result The result.
	 */
	public void resultAvailable(final Object source, final Object result)
	{
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				listener.resultAvailable(source, result);
			}
			
			public String toString()
			{
				return "resultAvailable("+result+")_#"+this.hashCode();
			}
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param source The source component.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(final Object source, final Exception exception)
	{
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				listener.exceptionOccurred(source, exception);
			}
			
			public String toString()
			{
				return "exceptionOccurred("+exception+")_#"+this.hashCode();
			}
		});
	}
}
