package jadex.bridge;

import java.util.Collection;

import jadex.base.Starter;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IUndoneIntermediateResultListener;

/**
 *  Intermediate listener that invokes listeners on component thread.
 */
public class IntermediateComponentResultListener<E> extends ComponentResultListener<Collection<E>> 
	implements IIntermediateResultListener<E>, IUndoneIntermediateResultListener<E>
{
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public IntermediateComponentResultListener(IIntermediateResultListener<E> listener, IInternalAccess component)
	{
		super(listener, component);
	}

	//-------- IIntermediateResultListener interface --------
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void intermediateResultAvailable(final E result)
	{
		if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			try
			{
				component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(undone && listener instanceof IUndoneIntermediateResultListener)
						{
							((IUndoneIntermediateResultListener<E>)listener).intermediateResultAvailableIfUndone(result);
						}
						else
						{
							((IIntermediateResultListener<E>)listener).intermediateResultAvailable(result);
						}
						return IFuture.DONE;
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
			if(undone && listener instanceof IUndoneIntermediateResultListener)
			{
				((IUndoneIntermediateResultListener<E>)listener).intermediateResultAvailableIfUndone(result);
			}
			else
			{
				((IIntermediateResultListener<E>)listener).intermediateResultAvailable(result);
			}
		}
	}
	
	/**
     *  Declare that the future is finished.
     */
    public void finished()
    {
    	if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			try
			{
				component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(undone && listener instanceof IUndoneIntermediateResultListener)
						{
							((IUndoneIntermediateResultListener<E>)listener).finishedIfUndone();
						}
						else
						{
							((IIntermediateResultListener<E>)listener).finished();
						}
						return IFuture.DONE;
					}
					
					public String toString()
					{
						return "setFinished()_#"+this.hashCode();
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(component.getComponentIdentifier(), new Runnable()
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
    
    /**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result)
	{
		this.undone = true;
		intermediateResultAvailable(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finishedIfUndone()
    {
    	this.undone = true;
    	finished();
    }
}
