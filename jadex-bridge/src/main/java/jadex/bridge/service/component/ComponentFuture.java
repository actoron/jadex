package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The component future ensures that future result/exception notifications
 *  are executed on the calling component thread.
 */
public class ComponentFuture extends Future
{
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentFuture(final IExternalAccess ea, final IComponentAdapter adapter, final IFuture source)
	{
		source.addResultListener(new IResultListener()
		{
			public void resultAvailable(final Object result)
			{
				if(adapter.isExternalThread())
				{
					ea.scheduleStep(new IComponentStep()
					{
						public Object execute(IInternalAccess ia)
						{
							setResult(result);
							return null;
						}
					});
				}
				else
				{
					setResult(result);
				}
			}
			
			public void exceptionOccurred(final Exception exception)
			{
				if(adapter.isExternalThread())
				{
					ea.scheduleStep(new IComponentStep()
					{
						public Object execute(IInternalAccess ia)
						{
							setException(exception);
							return null;
						}
					});
				}
				else
				{
					setException(exception);
				}
			}
		});
	}
}
