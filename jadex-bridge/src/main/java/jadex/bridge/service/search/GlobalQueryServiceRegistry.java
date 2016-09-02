package jadex.bridge.service.search;

import java.util.Timer;
import java.util.TimerTask;

import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DuplicateRemovalIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Registry that allows for adding global queries with local registry.
 *  Uses remote searches to emulate the persistent query.
 */
public class GlobalQueryServiceRegistry extends ServiceRegistry
{
//	/** The agent. */
//	protected IInternalAccess agent;
	
	/** The timer. */
	protected Timer timer;
	
	/** The global query delay. */
	protected long delay;

	/**
	 *  Create a new GlobalQueryServiceRegistry.
	 */
	public GlobalQueryServiceRegistry(long delay)
	{
		this.delay = delay;
	}

	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		super.addQuery(query).addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
			
		// Emulate persistent query by searching periodically
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			final DuplicateRemovalIntermediateResultListener<T> lis = new DuplicateRemovalIntermediateResultListener<T>(new UnlimitedIntermediateDelegationResultListener<T>(ret))
			{
				public byte[] objectToByteArray(Object service)
				{
					return super.objectToByteArray(((IService)service).getServiceIdentifier());
				}
			};
			
			waitForDelay(delay, new Runnable()
			{
				public void run()
				{
//					Class<T> mytype = query.getType()==null? null: (Class<T>)query.getType().getType0();
//					searchRemoteServices(query.getOwner(), mytype, query.getFilter()).addIntermediateResultListener(lis);
					searchRemoteServices(query.getOwner(), query.getType(), query.getFilter()).addIntermediateResultListener(lis);
					
					if(!ret.isDone())
						waitForDelay(delay, this);
					else
						System.out.println("stopping global query polling: "+query);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Wait for delay and execute runnable.
	 */
	protected void waitForDelay(long delay, final Runnable run)
	{
		if(timer==null)
			timer = new Timer(true);
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				run.run();
			}
		}, delay);
	}
}
