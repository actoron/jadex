package jadex.micro.quickstart;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  The time provider periodically sends out time values to all subscribers.
 *  For simplicity, the agent implements the time service itself.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITimeService.class, scope=ServiceScope.GLOBAL))
public class TimeProviderAgent	implements ITimeService
{
	//-------- attributes --------
		
	/** The subscriptions to be informed about the time. */
	protected Map<SubscriptionIntermediateFuture<String>, DateFormat>	subscriptions	= new LinkedHashMap<>();
	
	//-------- ITimeService interface --------
	
	/**
	 *  Get the location of the platform, where the time service runs.
	 *  The location is a constant value for each service, therefore it can be cached
	 *  and no future is needed.
	 */
	public IFuture<String>	getLocation()
	{
		String	location	= SUtil.getGeoIPLocation();
		return new Future<>(location);
	}
	
	/**
	 *  Subscribe to the time service.
	 *  Every couple of seconds, a string with the current time will be
	 *  sent to the subscriber.
	 */
	public ISubscriptionIntermediateFuture<String>	subscribe(DateFormat format)
	{
		// Add a subscription to the set of subscriptions.
		final SubscriptionIntermediateFuture<String>	ret	= new SubscriptionIntermediateFuture<String>();
		subscriptions.put(ret, format);
		
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
	
	//-------- agent life cycle --------
	
	/**
	 *  Due to annotation, called once after agent is initialized.
	 *  The internal access parameter is optional and is injected automatically.
	 */
	@AgentBody
	public void body(IInternalAccess ia)
	{
		// The execution feature provides methods for controlling the execution of the agent.
		IExecutionFeature	exe	= ia.getFeature(IExecutionFeature.class);
		
		// Execute a step every 5000 milliseconds, start from next full 5000 milliseconds
		exe.repeatStep(5000-System.currentTimeMillis()%5000, 5000, new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Notify all subscribers
				for(SubscriptionIntermediateFuture<String> subscriber: subscriptions.keySet())
				{
					DateFormat	df	= subscriptions.get(subscriber);
					String	time	= df.format(new Date());
					
					// Add the current time as intermediate result.
					// The if-undone part is used to ignore errors,
					// when subscription was cancelled in the mean time.
					subscriber.addIntermediateResultIfUndone(time+"; "+new Date().toString());
				}
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Start a Jadex platform and the TimeProviderAgent.
	 */
	public static void	main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.setPlatformName("timeprovider_*");
		config.addComponent(TimeProviderAgent.class);
		config.setLoggingLevel(Level.WARNING);
		Starter.createPlatform(config, args).get();
	}
}
