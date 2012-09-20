package jadex.bridge;

import jadex.base.Starter;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.IIntermediateResultListener;

import java.util.Collection;

/**
 *  Intermediate listener that invokes listeners on component thread.
 */
public class IntermediateComponentResultListener<E> extends ComponentResultListener<Collection<E>> implements IIntermediateResultListener<E>
{
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public IntermediateComponentResultListener(IIntermediateResultListener<E> listener, IComponentAdapter adapter)
	{
		super(listener, adapter);
	}

	//-------- IIntermediateResultListener interface --------
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void intermediateResultAvailable(final E result)
	{
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable()
				{
					public void run()
					{
						((IIntermediateResultListener<E>)listener).intermediateResultAvailable(result);
					}
					
					public String toString()
					{
						return "intermediateResultAvailable("+result+")_#"+this.hashCode();
					}
				});
			}
			catch(Exception e)
			{
				// listener.exceptionOccurred(e); must not be called more than once!
				// Will be called in finished.
//				listener.exceptionOccurred(e);
			}
		}
		else
		{
			((IIntermediateResultListener<E>)listener).intermediateResultAvailable(result);
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
						((IIntermediateResultListener<E>)listener).finished();
					}
					
					public String toString()
					{
						return "setFinished()_#"+this.hashCode();
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						listener.exceptionOccurred(e);
					}
				});
			}
		}
		else
		{
			((IIntermediateResultListener<E>)listener).finished();
		}
    }
}
