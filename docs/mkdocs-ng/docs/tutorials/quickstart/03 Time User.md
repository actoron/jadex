# The Time User Agent


This chapter shows how to discover and use the time service.

## Agent Implementation

Create Java file *TimeUserAgent.java* in the package *jadex.micro.quickstart* and paste the contents as shown below.


```java
package jadex.micro.quickstart;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
@RequiredServices(@RequiredService(name="timeservices", type=ITimeService.class, multiple=true,
	binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
public class TimeUserAgent
{
	/**
	 *  The time services are searched and added at agent startup.
	 */
	@AgentService//(retrycnt=10, retrydelay=10000)
	public void	addTimeService(ITimeService timeservice)
	{
		ISubscriptionIntermediateFuture<String>	subscription	= timeservice.subscribe();
		while(subscription.hasNextIntermediateResult())
		{
			String	time	= subscription.getNextIntermediateResult();
			String	platform	= ((IService)timeservice).getServiceIdentifier().getProviderId().getPlatformName();
			System.out.println("New time received from "+platform+" at "+timeservice.getLocation()+": "+time);
		}
	}
	
	/**
	 *  Start a Jadex platform and the TimeUserAgent.
	 */
	public static void	main(String[] args)
	{
		PlatformConfiguration	config	= PlatformConfiguration.getDefault();
		config.addComponent(TimeUserAgent.class.getName()+".class");
		Starter.createPlatform(config).get();
	}
}
```


## Execute the Agent


Start the Jadex platform and the agent with the provided *main()* method. In case there are any time services online, you should see their time printed to the console in periodic updates. In the next chapter you will learn how to start a local time provider.

The details of the time user agent are explained in the following subsections. Furthermore, you can see [Platform.Starting a Platform](../../../platform/platform/#starting-a-platform) for details on platform configurations and [Tools.JCC Overview](../../../tools/02 JCC Overview/) for details on the Jadex control center window (JCC).

## Class Name and @Agent Annotation

To identify the class as an agent that can be started on the platform, the @Agent annotation is used. Furthermore, to speed up the scanning for startable agents, all agent classes must end with 'Agent' by convention. Before the 'Agent' part, any valid Java identifier can be used.

## The Required Service Declaration and Injection

A Jadex agent may use services provided by other agents. An agent might search for arbitrary services dynamically, but it also can declare required services as part of its public interface. A declaration of a required service is advantageous, because it makes the dependencies of an agent more explicit. Furthermore, the declarative specification of a required service allows separating details, such as service binding, from the agent implementation.

The time user agent declares the usage of the ITimeService by the @RequiredService annotation. The annotation here states, that the agent is interested in multiple instances of the service at once (```multiple=true```) and that all platforms world wide should be searched for available services (```binding=@Binding(scope=Binding.SCOPE_GLOBAL)```).

The ```@AgentService``` annotation to the ```addTimeService()``` method states that at startup a service search should be started and the method should be called with every found service as given in the required service declaration. The corresponding required service declaration is found by matching the method name to the name given in the @RequireService annotation.

## The Agent Behavior

The method is called for each found service. In this method, the agent subscribes to the found time service by adding another listener. This listener is informed about each new time notified by the specific time service.
