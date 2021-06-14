package jadex.micro.quickstart;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
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
        final IComponentIdentifier    client    = ServiceCall.getCurrentInvocation().getCaller();
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
                System.out.println("removed subscriber "+client+" due to: "+reason);
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
