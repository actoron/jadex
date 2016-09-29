# The Time User Agent


This chapter shows how to discover and use the time service.

## Agent Implementation

Create Java file *TimeUserAgent.java* in the package *jadex.micro.quickstart* and paste the contents as shown below.


```java

package jadex.micro.quickstart;

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
}
```


## Execute the Agent


Start the Jadex platform with its main class ```jadex.base.Starter```. Add the directory containing the compiled classes of your agent to the Jadex Control Center (JCC). Select the TimeUserAgent.class and click *Start*.

In case there are any time services online, you should see their time printed to the console in periodic updates. If no time service is found after 30 seconds, a *jadex.bridge.service.search.ServiceNotFoundException* is printed.

See [AC Tutorial.Chapter 02: Installation](../AC%20Tutorial/02%20Installation)  for details on setting up a launch configuration and starting agents in the JCC.

The details of the agent are explained in the following subsections.

## Class Name and @Agent Annotation

To identify the class as an agent that can be started on the platform, the @Agent annotation is used. Furthermore, to speed up the scanning for startable agents, all agent classes must end with 'Agent' by convention. Before the 'Agent' part, any valid Java identifier can be used.

## The Required Service Declaration and Injection

A Jadex agent may use services provided by other agents. An agent might search for arbitrary services dynamically, but it also can declare required services as part of its public interface. A declaration of a required service is advantageous, because it makes the dependencies of an agent more explicit. Furthermore, the declarative specification of a required service allows separating details, such as service binding, from the agent implementation.

The time user agent declares the usage of the ITimeService by the @RequiredService annotation. The annotation here states, that the agent is interested in multiple instances of the service at once (```multiple=true```) and that all platforms world wide should be searched for available services (```binding=@Binding(scope=Binding.SCOPE_GLOBAL)```).

The required service declaration is given the name ```timeservices``` and is used for the corresponding field of the class. The ```@AgentService``` annotation fo the field states that at startup, the field should be injected with the found required services as given in the required service declaration with the same name. Here a type of IIntermediateFuture is used to receive each service immediately when found as shown in the agent body.

## The Agent Body

The agent body is executed after the agent is started. Using the @AgentBody annotation a method can be designated as agent body.

The body first adds a listener to the timeservices field, to receive updates whenever a timeservice is found. For each found service, the ```intermediateResultAvailable()``` method is called. In this method, the agent subscribes to the found time service by adding another listener. This listener is informed about each new time notified by the specific time service.
