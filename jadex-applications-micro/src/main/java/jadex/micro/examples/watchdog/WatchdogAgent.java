package jadex.micro.examples.watchdog;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *  The watchdog agent pings other watchdogs and issues an action,
 *  when a watchdog becomes unavailable.
 */
@ProvidedServices(@ProvidedService(type=IWatchdogService.class, implementation=@Implementation(expression="$component")))
@RequiredServices(@RequiredService(name="watchdogs", type=IWatchdogService.class, multiple=true,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL, dynamic=true)))
@Arguments(@Argument(clazz=long.class, name="delay", description="Delay between pings.", defaultvalue="3000"))
@Service
public class WatchdogAgent	extends MicroAgent	implements IWatchdogService
{
	//-------- attributes --------
	
	/** The found watchdogs. */
	protected Map	watchdogs;
	
	//-------- agent body --------
	
	/**
	 *  Agent startup.
	 */
	public IFuture<Void> agentCreated()
	{
		try
		{
			this.watchdogs	= new LinkedHashMap();
			final long	delay	= ((Number)getArgument("delay")).longValue();
			
			scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					final IComponentStep	step	= this;
					Object[]	keys	= watchdogs.keySet().toArray();
					final IResultListener	crl	= new CounterResultListener(keys.length, new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							// Pinging finished: Search for new watchdogs.
							getRequiredServices("watchdogs").addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result)
								{
	//								System.out.println("Found: "+result);
									if(result instanceof Collection)
									{
										for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
										{
											IWatchdogService	watchdog	= (IWatchdogService)it.next();
											if(!watchdog.getInfo().equals(getInfo()))
											{
												watchdogs.put(watchdog.getInfo(), watchdog);
											}
										}
									}
									
									waitFor(delay, step);
								}
								
								public void exceptionOccurred(Exception exception)
								{
//									throw exception instanceof RuntimeException ? (RuntimeException)exception : new RuntimeException(exception);
									getLogger().warning("Exception occurred: "+exception);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							throw exception instanceof RuntimeException ? (RuntimeException)exception : new RuntimeException(exception);
							getLogger().warning("Exception occurred: "+exception);
						}
					});
	
					// Ping known watchdogs
	//				System.out.println("Pinging: "+SUtil.arrayToString(keys));
					for(int i=0; i<keys.length; i++)
					{
						final Object	key	= keys[i];
						IWatchdogService	watchdog	= (IWatchdogService)watchdogs.get(key);
						watchdog.ping().addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								crl.resultAvailable(null);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("Watchdog triggered: "+key);
								watchdogs.remove(key);
								crl.resultAvailable(null);
							}
						});
					}
					
					return IFuture.DONE;
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return IFuture.DONE;
	}
	
	//-------- IWatchdogService implementation --------
	
	/**
	 *  Get the information about this watchdog.
	 *  @return The information.
	 */
	public String	getInfo()
	{
		return getComponentIdentifier().getName();
	}
	
	/**
	 *  Test if this watchdog is alive.
	 */
	public IFuture ping()
	{
		return IFuture.DONE;
	}
}
