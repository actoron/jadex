package jadex.micro.quickstart;

import java.net.URL;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
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
@ProvidedServices(@ProvidedService(type=ITimeService.class))
public class TimeProviderAgent	implements ITimeService
{
	//-------- attributes --------
		
	/** The location (determined at startup). */
	protected String	location	= determineLocation();
	
	/** The subscriptions to be informed about the time. */
	protected Set<SubscriptionIntermediateFuture<String>>	subscriptions
		= new LinkedHashSet<SubscriptionIntermediateFuture<String>>();
	
	//-------- ITimeService interface --------
	
	/**
	 *  Get the location of the platform, where the time service runs.
	 *  The location is a constant value for each service, therefore it can be cached
	 *  and no future is needed.
	 */
	public String	getLocation()
	{
		return location;
	}
	
	/**
	 *  Subscribe to the time service.
	 *  Every couple of seconds, a string with the current time will be
	 *  sent to the subscriber.
	 */
	public ISubscriptionIntermediateFuture<String>	subscribe()
	{
		// Add a subscription to the set of subscriptions.
		final SubscriptionIntermediateFuture<String>	ret	= new SubscriptionIntermediateFuture<String>();
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
	
	//-------- agent life cycle --------
	
	/**
	 *  Due to annotation, called once after agent is initialized.
	 *  The internal access parameter is optional and is injected automatically.
	 */
	@AgentBody
	public void body(IInternalAccess ia)
	{
		// The execution feature provides methods for controlling the execution of the agent.
		IExecutionFeature	exe	= ia.getComponentFeature(IExecutionFeature.class);
		
		// Execute a step every 5000 milliseconds, start from next full 5000 milliseconds
		exe.repeatStep(5000-System.currentTimeMillis()%5000, 5000, new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Notify all subscribers
				for(SubscriptionIntermediateFuture<String> subscriber: subscriptions)
				{
					// Add the current time as intermediate result.
					// The if-undone part is used to ignore errors,
					// when subscription was cancelled in the mean time.
					subscriber.addIntermediateResultIfUndone(new Date().toString());
				}
				
				return IFuture.DONE;
			}
		});
	}
	
	//-------- helper methods --------
	
	/**
	 *  Determine the location of the local platform.
	 */
	protected static String	determineLocation()
	{
		String	ret;
		try
		{
			// These free-to-try geoip services have (almost) the same result format.
//			Scanner scanner	= new Scanner(new URL("http://ipinfo.io/json").openStream(), "UTF-8");
//			Scanner scanner	= new Scanner(new URL("http://api.petabyet.com/geoip/").openStream(), "UTF-8");
//			Scanner scanner	= new Scanner(new URL("http://freegeoip.net/json/").openStream(), "UTF-8");	// use "country_name"
			Scanner scanner	= new Scanner(new URL("http://ip-api.com/json").openStream(), "UTF-8");
			
			// Very simple JSON parsing, matches ..."key": "value"... parts to find country and city.
			String	country	= null;
			String	city	= null;
			scanner.useDelimiter(",");
			while(scanner.findWithinHorizon("\"([^\"]*)\"[^:]*:[^\"]*\"([^\"]*)\"", 0)!=null)
			{
				String	key	= scanner.match().group(1);
				String	val	= scanner.match().group(2);
				if("country".equals(key))
//				if("country_name".equals(key))
				{
					country	= val;
				}
				else if("city".equals(key))
				{
					city	= val;
				}
			}
			scanner.close();
			
			ret	= city!=null ? country!=null ? city+", "+country : city
				: country!=null ? country : "unknown";
				
		}
		catch(Exception e)
		{
			// ignore
			ret	= "unknown";
		}
		
		return ret;
	}

	/**
	 *  Start a Jadex platform and the TimeProviderAgent.
	 */
	public static void	main(String[] args)
	{
		PlatformConfiguration	config	= PlatformConfiguration.getMinimalRelayAwareness();
		config.addComponent(TimeProviderAgent.class.getName()+".class");
		Starter.createPlatform(config).get();
	}
}
