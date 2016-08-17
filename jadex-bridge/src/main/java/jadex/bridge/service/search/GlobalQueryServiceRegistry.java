package jadex.bridge.service.search;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * 
 */
public class GlobalQueryServiceRegistry extends ServiceRegistry
{
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The global query delay. */
	protected long delay;
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final ISubscriptionIntermediateFuture<T> ret = super.addQuery(query);
	
		// Emulate persistent query by searching periodically
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					
					
					if(!ret.isDone())
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this);
					
					return IFuture.DONE;
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		super.removeQuery(query);
		
		
	}
}
