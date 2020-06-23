# Time Provider Agent

This chapter shows how to implement and publish the time service.

## Agent Implementation

Create Java file `TimeProviderAgent.java` in the package `jadex.micro.quickstart` and paste the contents as shown below.

```java
package jadex.micro.quickstart;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  The time provider periodically sends out time values to all subscribers.
 *  For simplicity, the agent implements the time service itself.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITimeService.class, scope=ServiceScope.GLOBAL))
public class TimeProviderAgent implements ITimeService
{
    //-------- attributes --------

    /** The subscriptions to be informed about the time. */
    protected Map<SubscriptionIntermediateFuture<String>, DateFormat> subscriptions = new LinkedHashMap<>();

    //-------- ITimeService interface --------

    /**
     *  Get the location of the platform, where the time service runs.
     */
    public IFuture<String> getLocation()
    {
        String location = SUtil.getGeoIPLocation();
        return new Future<>(location);
    }

    /**
     *  Subscribe to the time service.
     */
    public ISubscriptionIntermediateFuture<String> subscribe(DateFormat format)
    {
        final SubscriptionIntermediateFuture<String> ret = new SubscriptionIntermediateFuture<String>();
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
    @OnStart
    public void body(IInternalAccess ia)
    {
        // Execute a step every 5000 milliseconds
        ia.repeatStep(0, 5000, new IComponentStep<Void>()
        {
            @Override
            public IFuture<Void> execute(IInternalAccess ia)
            {
                // Notify all subscribers
                for(SubscriptionIntermediateFuture<String> subscriber: subscriptions.keySet())
                {
                    DateFormat df = subscriptions.get(subscriber);
                    String time = df.format(new Date());

                    // Add the current time as intermediate result.
                    // The if-undone part is used to ignore errors,
                    // when subscription was cancelled in the mean time.
                    subscriber.addIntermediateResultIfUndone(time);
                }

                return IFuture.DONE;
            }
        });
    }

    /**
     *  Start a Jadex platform and the TimeProviderAgent.
     */
    public static void main(String[] args)
    {
        IPlatformConfiguration config = PlatformConfigurationHandler.getMinimalComm();
        config.addComponent(TimeProviderAgent.class);
        Starter.createPlatform(config, args).get();
    }
}
```

### Execute the Agent

Start the Jadex platform and the time provider agent with the provided `main()` method. Afterwards (or beforehand) start the time user agent with its `main()` method. The two platforms will connect automatically and the time user will find the time provider and print out its time update messages.

The details of the time provider agent are explained in the following subsections. Furthermore, you can see [Remote Communication](../../remote/remote.md#awareness)) for details on automatic platform discovery.

## Service Annotations

The `@ProvidedService` annotation tells the Jadex runtime that this agent provides a service of type `ITimeService.class` and that it should be visible to all platforms world wide (`scope=ServiceScope.GLOBAL`). The `@Service` annotation furthermore states, that the agent class implements the service itself instead of having a separate class for the service implementation.

See, e.g.,  [Services.Providing Services](../../services/services.md#providing-services)) for more details on the @ProvidedService annotation.

## Object Attributes

The `subscriptions` field is a `java.util.Map` of the current subscriptions to the time service and their corresponding `java.text.DateFormat`s. The object type `jadex.commons.future.SubscriptionIntermediateFuture` represents the server side of the subscription future as described for the [time service interface's `subscribe()` method](02%20Time%20Service%20Interface.md#the-subscribe-method).

## The `subscribe()` Method

On each call, the `subscribe()` method creates a new subscription future object for the new subscriber and returns that object. The subcription future object is also added to the subscriptions list, so it can be notified about each new time message.

Furthermore, a termination command is set on the subscription future. This command is executed when the subscription ends either due to a network error or when a client explicitly cancels the subscription. In the command, the time provider agent prints out a message and removes the subscription from the list.

## The Agent Life Cycle

The notification behavior of the time provider is captured in the `body()` method. It is annotated with `@OnStart` to state that it should be called once after the agent is started.

The repeated notification is modeled by a so called component step, which is scheduled using the *IInternalAccess*. Each Jadex component is internally operated by a set of features that handle the different aspects like execution and required or provided services. These features are bundled and made available through the so called **internal** access, that is, . The API of the available features can be found [here](https://www.activecomponents.org/forward.html?type=javadoc&path=index.html?jadex/bridge/component/package-summary.html).

Using the *repeatStep()* method of the execution feature, a step is scheduled every 5 seconds. In this step, the time provider iterates through all current subscriptions and sends the next time message using the *addIntermediateResultIfUndone()* method. The 'IfUndone' part threby states that errors should be ignored, e.g. when the subscription was just cancelled.

---
[[Back: 03 Time User](03%20Time%20User.md) | [Next: 05 Summary](05%20Summary.md)]
