package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IntermediateFuture;

/**
 *  The component future ensures that intermediate future notifications
 *  are executed on the calling component thread.
 */
public class ComponentIntermediateFuture extends IntermediateFuture
{
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentIntermediateFuture(final IExternalAccess ea, final IComponentAdapter adapter, final IIntermediateFuture source)
	{
		source.addResultListener(new IIntermediateResultListener()
		{
			public void resultAvailable(final Object result)
			{
				if(adapter.isExternalThread())
				{
//					if(adapter.getDescription().getState().equals(IComponentDescription.STATE_SUSPENDED))
//						adapter.getLogger().warning("Warning schedule step on suspended component");

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
			
			public void intermediateResultAvailable(final Object result)
			{
				if(adapter.isExternalThread())
				{
//					if(adapter.getDescription().getState().equals(IComponentDescription.STATE_SUSPENDED))
//						adapter.getLogger().warning("Warning schedule step on suspended component");
	
					ea.scheduleStep(new IComponentStep()
					{
						public Object execute(IInternalAccess ia)
						{
							addIntermediateResult(result);
							return null;
						}
					});
				}
				else
				{
					addIntermediateResult(result);
				}
			}
			
			public void finished()
			{
				if(adapter.isExternalThread())
				{
//					if(adapter.getDescription().getState().equals(IComponentDescription.STATE_SUSPENDED))
//						adapter.getLogger().warning("Warning schedule step on suspended component");
	
					ea.scheduleStep(new IComponentStep()
					{
						public Object execute(IInternalAccess ia)
						{
							setFinished();
							return null;
						}
					});
				}
				else
				{
					setFinished();
				}
			}
			
			public void exceptionOccurred(final Exception exception)
			{
				if(adapter.isExternalThread())
				{
//					if(adapter.getDescription().getState().equals(IComponentDescription.STATE_SUSPENDED))
//						adapter.getLogger().warning("Warning schedule step on suspended component");
	
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
