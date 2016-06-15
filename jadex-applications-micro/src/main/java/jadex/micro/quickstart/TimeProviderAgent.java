package jadex.micro.quickstart;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.concurrent.TimeoutException;
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
@ProvidedServices(@ProvidedService(type=ITimeService.class))
public class TimeProviderAgent	implements ITimeService, IComponentStep<Void>
{
	//-------- attributes --------
	
	/** The jadex component that executes the time provider agent.
	 *  Gets automatically injected at agent startup. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The location (determined at startup). */
	protected String	location	= determineLocation();
	
	/** The subscriptions to be informed about the time. */
	protected Set<SubscriptionIntermediateFuture<String>>	subscriptions
		= new LinkedHashSet<SubscriptionIntermediateFuture<String>>();
	
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
		for(SubscriptionIntermediateFuture<String> subscriber: subscriptions)
		{
			// Add the current time as intermediate result.
			// The if-undone part is used to ignore errors,
			// when subscription was cancelled in the mean time.
			subscriber.addIntermediateResultIfUndone(d.toString());
		}
		
		// Wait until the next full five seconds.
		long	millis	= d.getTime()+5000-System.currentTimeMillis();
		ia.getComponentFeature(IExecutionFeature.class).waitForDelay(millis, this);
		
		// Return empty non-finished future to keep agent alive.
		return new Future<Void>();
	}
	
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
	
	//-------- helper methods --------
	
	/**
	 *  Determine the location of the local platform.
	 */
	protected static String	determineLocation()
	{
		final Future<String>	fut	= new Future<String>();
		new Thread(new Runnable()
		{
			public void run()
			{
				String	ret	= "unknown";
				try
				{
					// Get geo location, e.g.
					// 134.100.11.200,DE,Germany,HH,Hamburg,Hamburg,22767,Europe/Berlin,53.55,10.00,0
					Scanner scanner	= new Scanner(new URL("http://freegeoip.net/csv/").openStream(), "UTF-8");
					scanner.findInLine("([^,]*),");
					scanner.findInLine("([^,]*),");
					scanner.findInLine("([^,]*),");
					ret	= scanner.match().group(1);	// Country
					scanner.findInLine("([^,]*),");
					scanner.findInLine("([^,]*),");
					ret	= scanner.match().group(1) + ", " + ret;	// City
					scanner.close();
				}
				catch(Exception e)
				{
					// freegeoip sometimes has connection timeouts :-(
					System.err.println("Cannot determine location: "+e);
				}
				fut.setResult(ret);
			}
		}).start();

		try
		{
			return fut.get(Starter.getScaledRemoteDefaultTimeout(IComponentIdentifier.LOCAL.get(), 0.8));
		}
		catch(TimeoutException e)
		{
			return "unknown";
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		InputStream	is	= new URL("http://freegeoip.net/csv/").openStream();
		byte[]	buf	= new byte[1234];
		int read;
		while((read=is.read(buf))!=-1)
		{
			System.out.write(buf, 0, read);
		}
		is.close();
	}
}
