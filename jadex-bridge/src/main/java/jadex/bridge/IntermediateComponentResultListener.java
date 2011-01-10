package jadex.bridge;

import jadex.commons.IIntermediateResultListener;

/**
 * 
 */
public class IntermediateComponentResultListener extends ComponentResultListener implements IIntermediateResultListener
{
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public IntermediateComponentResultListener(IIntermediateResultListener listener, IComponentAdapter adapter)
	{
		super(listener, adapter);
	}

	//-------- IIntermediateResultListener interface --------
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void intermediateResultAvailable(final Object result)
	{
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable()
				{
					public void run()
					{
						((IIntermediateResultListener)listener).intermediateResultAvailable(result);
					}
					
					public String toString()
					{
						return "intermediateResultAvailable("+result+")_#"+this.hashCode();
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
			((IIntermediateResultListener)listener).intermediateResultAvailable(result);
		}
	}
	
	/**
     *  Declare that the future is finished.
     */
    public void finished()
    {
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable()
				{
					public void run()
					{
						((IIntermediateResultListener)listener).finished();
					}
					
					public String toString()
					{
						return "setFinished()_#"+this.hashCode();
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
			((IIntermediateResultListener)listener).finished();
		}
    }
}
