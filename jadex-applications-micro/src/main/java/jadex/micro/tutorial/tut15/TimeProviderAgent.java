package jadex.micro.tutorial.tut15;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *  The time provider periodically sends out time values to all subscribers.
 *  For simplicity, the agent implements the time service itself.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITimeService.class,
	implementation=@Implementation(expression="$pojoagent")))
public class TimeProviderAgent	implements ITimeService, IComponentStep<Void>
{
	//-------- attributes --------
	
	/** The jadex component that executes the time provider agent.
	 *  Gets automatically injected at agent startup. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The subscriptions to be informed about the time. */
	protected Set<SubscriptionIntermediateFuture<Date>>	subscriptions
		= new LinkedHashSet<SubscriptionIntermediateFuture<Date>>();
	
	//-------- agent lifecycle methods --------
	
	/**
	 *  Due to annotation, called once after agent is initialized.
	 *  Also used as step and thus called periodically as rersult of waitFor().
	 */
	@AgentBody
	public IFuture<Void> execute(IInternalAccess ia)
	{
		// Calculate date of last full five seconds.
		Date	d = new Date(System.currentTimeMillis()-System.currentTimeMillis()%5000);

		// Notify all subscribers
		for(SubscriptionIntermediateFuture<Date> subscriber: subscriptions)
		{
			// Add the current time as imetrmediate result.
			// The if-undone part is used to ignore errors,
			// when subscription was cancelled in the mean time.
			subscriber.addIntermediateResultIfUndone(d);
		}
		
		// Wait until the next full five seconds.
		long	millis	= d.getTime()+5000-System.currentTimeMillis();
		ia.waitForDelay(millis, this);
		
		// Return empty non-finished future to keep agent alive.
		return new Future<Void>();
	}
	
	//-------- ITimeService interface --------
	
	/**
	 *  Get the name of the platform, where the time service runs.
	 *  Name is a constant value for each service, therefore it can be cached
	 *  and no future is needed.
	 */
	public String	getName()
	{
		return agent.getComponentIdentifier().getPlatformName();
	}
	
	/**
	 *  Subscribe to the time service.
	 *  Every couple of seconds, the current time will be
	 *  sent to the subscriber.
	 */
	public ISubscriptionIntermediateFuture<Date>	subscribe()
	{
		// Add a subscription to the set of subscriptions.
		final SubscriptionIntermediateFuture<Date>	ret	= new SubscriptionIntermediateFuture<Date>();
		subscriptions.add(ret);
		
		ret.setTerminationCommand(new TerminationCommand()
		{
			/**
			 *  The termination command allows to be informed, when the subscription ends,
			 *  e.g. due to a communication error or when the service user explicitly
			 *  cancels the subscription.
			 */
			public void terminated(Exception reason)
			{
				System.out.println("removed subscriber due to: "+reason);
				subscriptions.remove(ret);
			}
		});
		
		return ret;
	}
}
