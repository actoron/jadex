# The Time User Agent

This chapter shows how to discover and use the time service.

## Agent Implementation

Create Java file `TimeUserAgent.java` in the package `jadex.micro.quickstart` and paste the contents as shown below.

```java
package jadex.micro.quickstart;

import java.text.DateFormat;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.RequiredService;

/**
 *  Simple agent that uses globally available time services.
 */
@Agent
public class TimeUserAgent
{
    /**
     *  Subscribe to any newly found time service and print the results when they arrive.
     */
    @OnService(requiredservice = @RequiredService(scope = ServiceScope.GLOBAL))
    public void    addTimeService(ITimeService timeservice)
    {
        String location = timeservice.getLocation().get();
        DateFormat format = DateFormat.getDateTimeInstance();
        ISubscriptionIntermediateFuture<String> subscription = timeservice.subscribe(format);
        while(subscription.hasNextIntermediateResult())
        {
            String time = subscription.getNextIntermediateResult();
            String platform = ((IService)timeservice).getServiceId().getProviderId().getPlatformName();
            System.out.println("New time received from "+platform+" in "+location+": "+time);
        }
    }

    /**
     *  Start a Jadex platform and the TimeUserAgent.
     */
    public static void main(String[] args)
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimalComm();
        config.addComponent(TimeUserAgent.class);
        Starter.createPlatform(config, args).get();
    }
}
```

### Execute the Agent

Start the Jadex platform and the agent with the provided `main()` method. In case there are any time services online, you should see their time printed to the console in periodic updates. In the next chapter you will learn how to start a local time provider.

The details of the time user agent are explained in the following subsections. Furthermore, you can see [Platform.Starting a Platform](../../platform/platform.md#starting-a-platform) for details on platform configurations.

### Class Name and `@Agent` Annotation

To identify the class as an agent that can be started on the platform, the `@Agent` annotation is used. Since Jadex 4.x agent classes need no longer end with 'Agent', but in our examples we keep this as a useful convention.

### The Required Service Declaration and Injection

A Jadex agent may use services provided by other agents. An agent might search for arbitrary services dynamically (in code), but it also can declare required services using annotations. A declaration of a required service is advantageous, because it makes the dependencies of an agent more explicit. Furthermore, the declarative specification of a required service allows separating details, such as service binding, from the agent implementation.

The `@OnService(requiredservice=...)` annotation to the `addTimeService()` method states that while the agent is alive a continuous service query should be executed. The method will then be called with every found service that matches the type of the method argument (i.e. `ITimeService`) and the required service declaration (`@RequiredService(scope = ServiceScope.GLOBAL)`). Therefore all time service implementations running on some world wide Jadex platform will be found and passed to the method `addTimeService()`.

## The Agent Behavior

In the `addTimeService()`, the agent subscribes to each found time service and receives the client side of the subscription future as described in the [last section](02%20Time%20Service%20Interface.md#the-subscribe-method). In the while loop, `hasNextIntermediateResult()` blocks until the next time notification becomes available and can be fetched by `getNextIntermediateResult()`.

[//]: # (*todo: describe IService and service identifier?*)

[//]: # (*todo: describe main method details?*)
