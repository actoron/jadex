package jadex.platform.service.pawareness;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.commons.Boolean3;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;


/**
 * Base agent for all passive awareness implementations (discovery and
 * management). Adds addresses to transport service and collects and notifies
 * about new platforms.
 */
// TODO: passively remove platforms based on validity durations
@Service
@Agent(autoprovide = Boolean3.TRUE)
public abstract class PassiveAwarenessBaseAgent implements IPassiveAwarenessService
{
	// -------- attributes --------

	/** The agent. */
	@Agent
	protected IInternalAccess	agent;

	/** The current search, if any. */
	protected IntermediateFuture<IComponentIdentifier>	search;

	/** The subscriptions. */
	protected Set<SubscriptionIntermediateFuture<IComponentIdentifier>>	subscriptions;

	/** The currently known platforms. */
	protected Set<IComponentIdentifier>	platforms;

	// -------- agent lifecycle --------

	/**
	 * Start the service.
	 * 
	 * @throws Exception, e.g. when required socket can not be opened.
	 */
	@ServiceStart
	public void start() throws Exception
	{
		subscriptions = new LinkedHashSet<SubscriptionIntermediateFuture<IComponentIdentifier>>();
		platforms = new LinkedHashSet<IComponentIdentifier>();

		// Send own info initially.
		sendInfo(null);

		// TODO: send info on address changes?
	}

	/**
	 * Stop the service.
	 */
	@ServiceShutdown
	public void shutdown() throws Exception
	{
		for(SubscriptionIntermediateFuture<IComponentIdentifier> sub : subscriptions)
		{
			// Undone, because might be cancelled already, but not yet removed.
			sub.setFinishedIfUndone();
		}
	}

	// -------- IPassiveAwarenessService --------

	/**
	 * Try to find other platforms and finish after timeout. Immediately returns
	 * known platforms and concurrently issues a new search, waiting for replies
	 * until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier> searchPlatforms()
	{
		if(search == null)
		{
			System.out.println("New search");
			search = new IntermediateFuture<IComponentIdentifier>();

			// Add initial results
			for(IComponentIdentifier platform : platforms)
			{
				search.addIntermediateResult(platform);
			}
			// issue search request to trigger replies from platforms
			sendInfo(null).addResultListener(new IResultListener<Void>()
			{
				@Override
				public void resultAvailable(Void result)
				{
					// TODO: timeout from service call
					agent.getComponentFeature(IExecutionFeature.class)
						.waitForDelay(Starter.getRemoteDefaultTimeout(agent.getComponentIdentifier()), true)
						.addResultListener(new IResultListener<Void>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							search.setFinished();
							search = null;
						}

						@Override
						public void resultAvailable(Void result)
						{
							search.setFinished();
							search = null;
						}
					});
				}

				@Override
				public void exceptionOccurred(Exception exception)
				{
					search.setFinished();
					search = null;
				}
			});
		}
		else
		{
			System.out.println("old search");
		}

		return search;
	}


	/**
	 * Immediately return known platforms and continuously publish newly found
	 * platforms. Does no active searching.
	 */
	public ISubscriptionIntermediateFuture<IComponentIdentifier> subscribeToNewPlatforms()
	{
		SubscriptionIntermediateFuture<IComponentIdentifier> sub = new SubscriptionIntermediateFuture<IComponentIdentifier>();
		subscriptions.add(sub);

		// Add initial results
		for(IComponentIdentifier platform : platforms)
		{
			search.addIntermediateResult(platform);
		}

		return sub;
	}

	// -------- template methods --------

	/**
	 * To be called whenever platform addresses are discovered. May be called
	 * from external threads.
	 */
	protected void discovered(final Collection<TransportAddress> addresses, final Object source)
	{
		// Ignore my own addresses.
		// TODO: what if data source and platform(s) of addresses differ (e.g. no point-to-point awareness)
		if(addresses!=null && !addresses.isEmpty() && !agent.getComponentIdentifier().getRoot().equals(addresses.iterator().next().getPlatformId()))
		{
			agent.getLogger().info("discovered: " + addresses);
			agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Add addresses to facilitate communication
					// TODO: cleanup after removal?
					ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class);
					tas.addManualAddresses(addresses).addResultListener(new IResultListener<Void>()
					{
						@Override
						public void resultAvailable(Void result)
						{
							// Extract platforms from address list
							Set<IComponentIdentifier> new_platforms = new LinkedHashSet<IComponentIdentifier>();
							for(TransportAddress address : addresses)
							{
								if(!agent.getComponentIdentifier().getRoot().equals(address.getPlatformId()))
									new_platforms.add(address.getPlatformId());
							}

							// Add platforms and notify about new.
							for(IComponentIdentifier platform : new_platforms)
							{
								if(platforms.add(platform))
								{
									if(search != null)
									{
										search.addIntermediateResult(platform);
									}

									for(SubscriptionIntermediateFuture<IComponentIdentifier> sub : subscriptions)
									{
										sub.addIntermediateResult(platform);
									}
								}
							}

							if(source != null)
							{
								sendInfo(source);
							}
						}

						@Override
						public void exceptionOccurred(Exception exception)
						{
							// shouldn't happen?
						}
					});

					return IFuture.DONE;
				}
			});
		}
	}

	/**
	 * Send the info to other platforms.
	 * 
	 * @param source If set, send only to source as provided in discovered().
	 */
	protected abstract void doSendInfo(List<TransportAddress> addresses, Object source) throws Exception;

	// -------- helper methods --------

	/**
	 * Send address info to listening platforms.
	 */
	protected IFuture<Void> sendInfo(final Object source)
	{
		final Future<Void> ret = new Future<Void>();
		ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class);
		tas.getAddresses().addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, Void>(ret)
		{
			@Override
			public void customResultAvailable(List<TransportAddress> addresses) throws Exception
			{
				agent.getLogger().info("sending: "+addresses);
				doSendInfo(addresses, source);
				ret.setResult(null);
			}
		});
		return ret;
	}
}
