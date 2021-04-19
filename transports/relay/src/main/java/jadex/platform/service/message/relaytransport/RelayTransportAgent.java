package jadex.platform.service.message.relaytransport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.IAutoLock;
import jadex.commons.collection.IRwMap;
import jadex.commons.collection.LRU;
import jadex.commons.collection.RwMapWrapper;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Feature;
import jadex.micro.annotation.Features;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.transport.AbstractTransportAgent;

/**
 *  Agent implementing relay routing.
 */
//@Agent(autoprovide=Boolean3.TRUE)
@Agent(name="rt",
	autostart=Boolean3.TRUE)
@Arguments({
	// todo: see SuperpeerRegistrySynchronizationAgent
//	@Argument(name="superpeers", clazz=String.class, defaultvalue="\"platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}\""),
//	@Argument(name="addresses", clazz=String.class, defaultvalue="\"ws://ssp@ssp.activecomponents.org:80\""),	// TODO: wss, TODO: set in PlatformAgent???
})
@ProvidedServices({
		@ProvidedService(type=ITransportService.class, scope=ServiceScope.PLATFORM)
})
@Features(additional=true, replace=true, value=@Feature(type=IMessageFeature.class, clazz=RelayMessageComponentFeature.class))
@Service
//@Tags("\"forwarding=\"+$args.forwarding")
public class RelayTransportAgent implements ITransportService, IRoutingService
{
	/** ID of sender of forwarding messages. */
	public static final String FORWARD_SENDER = "__fw_sender__";
	
	/** ID of forwarding messages. */
	public static final String FORWARD_DEST = "__fw_dest__";

	/** Maximum time spent on finding routing services. */
//	protected static final long MAX_ROUTING_SERVICE_DELAY = 3000;
	
	/** Transport priority = low */
	protected static final int PRIORITY = Integer.MIN_VALUE / 2;
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The cms (cached for speed). */
//	protected IComponentManagementService	cms;
	
	/** Security service. */
	//@AgentServiceQuery
	@OnService(query=Boolean3.TRUE, required=Boolean3.TRUE)
	protected ISecurityService secservice;
	
	/** Execution feature. */
	@AgentFeature
	protected IExecutionFeature execfeat;
	
	/** Relay transport agent's internal message feature. */
	protected IInternalMessageFeature intmsgfeat;
	
	/** Flag if the transport allows forwarding. */
	@AgentArgument
	protected boolean forwarding;
	
	/** Maintain a connection to at least this number of relays. */
	@AgentArgument
	protected int keepalivecount = 1;
	
	/** Delay of keepalive messages. */
	@AgentArgument
	protected long keepaliveinterval = -1;
	
	/** Maximum time spent on finding routing services. */
	@AgentArgument
	protected int routingdelay = 2000;
	
	/** Set to true for more verbose output. */
	@AgentArgument
	protected boolean debug;
	
	/** Size of the routing cache. */
	@AgentArgument
	protected int cachesize = 5000;
	
	/** Timestamp of the next clean for direct routes. */
	protected long nextclean = System.currentTimeMillis();
	
	/** Maximum allowed routing hops. */
//	@AgentArgument
//	protected int maxhops = 4;
	
	/** List of relays. */
	protected LinkedHashSet<IComponentIdentifier> relays = new LinkedHashSet<IComponentIdentifier>();
	
	/** List of working connections to relays. */
	protected Map<IComponentIdentifier, ISubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>>> keepaliveconnections;
	
	/** Component step used to connect to relays. */
	protected IComponentStep<Void> connectstep;
	
	/** Cache for routing service proxies. */
	protected LRU<IComponentIdentifier, IRoutingService> routingservicecache;
	
	/** Directly connected platforms. */
	protected IRwMap<IComponentIdentifier, SubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>>> directconns;
	
	/** Routing information (target platform / next route hop + cost). */
	protected IRwMap<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>> routecache;
	
	/** Update future to shorten timeout when keepalive update happens. */
	protected Future<Void> keepaliveupdatefuture = new Future<>();
	
	/** Update future to shorten timeout when query update happens. */
	protected Future<Void> relayupdatefuture = new Future<>();
	
	/**
	 *  Creates the agent.
	 */
	public RelayTransportAgent()
	{
//		keepaliveconnections = new LinkedHashSet<IComponentIdentifier>();
		keepaliveconnections = new HashMap<>();
		routingservicecache = new LRU<IComponentIdentifier, IRoutingService>(10);
	}
	
	/**
	 *  Agent initialization.
	 */
	//@AgentCreated
	@OnInit
	public IFuture<Void> start()
	{
		Future<Void>	ret	= new Future<>();
		if (keepaliveinterval < 0)
			keepaliveinterval = Starter.getDefaultTimeout(agent.getId().getRoot());
		if (keepaliveinterval < 0)
			keepaliveinterval = 30000;
		//keepaliveinterval /= 30;
		
//		this.cms = ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(IComponentManagementService.class);
		intmsgfeat = (IInternalMessageFeature) agent.getFeature(IMessageFeature.class);
		if(debug)
			System.out.println(agent+": started relay transport");
		
		routecache = new RwMapWrapper<>(new LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>>(cachesize, null, true));
		
		directconns = new RwMapWrapper<>(new HashMap<>());
		
		ServiceQuery<IRoutingService> query = new ServiceQuery<>(IRoutingService.class);
		query.setScope(ServiceScope.GLOBAL).setExcludeOwner(true);//.setServiceTags("forwarding=true");
		agent.getFeature(IRequiredServicesFeature.class).addQuery(query).addResultListener(new IntermediateEmptyResultListener<IRoutingService>()
		{
			public void intermediateResultAvailable(IRoutingService result)
			{
				relays.add(((IService) result).getServiceId().getProviderId().getRoot());
				if (debug)
					System.out.println(agent + ": Got query update, releasing wait future.");
				relayupdatefuture.setResult(null);
				relayupdatefuture = new Future<>();
			}

			public void exceptionOccurred(Exception exception)
			{
				if(!ret.setExceptionIfUndone(exception))
				{
					agent.killComponent(exception);
				}
			}
		});
		
		if (forwarding)
			setupForwarding();
		else
			setupClient();
		
		ret.setResultIfUndone(null);
		return ret;
	}
	
	@OnEnd
	public IFuture<Void> stop()
	{
		while (!keepaliveconnections.isEmpty())
		{
			ISubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> sub =
				keepaliveconnections.remove(keepaliveconnections.entrySet().iterator().next().getKey());
			sub.terminate();
		}
		
		try (IAutoLock l = directconns.writeLock())
		{
			while (!directconns.isEmpty())
			{
				SubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> sub =
						directconns.remove(directconns.entrySet().iterator().next().getKey());
				sub.setFinished();
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public ITerminableFuture<Integer> sendMessage(IMsgHeader header, byte[] encheader, byte[] body)
	{
		IComponentIdentifier fwdest = header.getReceiver().getRoot();
		
		SubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> direct = directconns.get(fwdest);
		if (direct != null)
		{
			TerminableFuture<Integer> ret = new TerminableFuture<>();
			ret.setException(new IllegalStateException("Direct connection available."));
			return ret;
		}
		
		
		IComponentIdentifier fwsender = ((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER)).getRoot();
		
		body = SUtil.mergeData(encheader, body);
		
		if(debug)
		{
			System.out.println(agent+": preparing forward package for " + fwdest + " from " + fwsender + " orig header " + header);
		}
		
		header = new MsgHeader();
		
		header.addProperty(FORWARD_SENDER, fwsender);
		header.addProperty(FORWARD_DEST, fwdest);
		
		IMsgHeader fh = header;
		return forwardMessage(header, body);
	}
	
	public ITerminableFuture<Integer> forwardMessage(final IMsgHeader header, final byte[] body)
	{
		IComponentIdentifier fwdest = (IComponentIdentifier) header.getProperty(FORWARD_DEST);
		if(debug)
		{
			IComponentIdentifier fwsender = ((IComponentIdentifier) header.getProperty(FORWARD_SENDER)).getRoot();
			System.out.println(agent+": processing forward package for " + fwdest + " from " + fwsender);
		}
		
		if (agent.getId().getRoot().equals(fwdest))
		{
			final List<byte[]> unpacked = SUtil.splitData(body);
			final IComponentIdentifier source = (IComponentIdentifier) header.getProperty(FORWARD_SENDER);
			final ISerializationServices serser = (ISerializationServices) Starter.getPlatformValue(agent.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
			
			AbstractTransportAgent.deliverRemoteMessage(agent, secservice, serser, source, unpacked.get(0), unpacked.get(1));
			
			TerminableFuture<Integer> ret = new TerminableFuture<>();
			ret.setResult(PRIORITY);
			return ret;
		}
		
		final TerminableFuture<Integer> ret = new TerminableFuture<>();
		final AtomicBoolean notcanceled = new AtomicBoolean(true);
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				notcanceled.set(false);
			}
		});
		
		SubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> direct = directconns.get(fwdest);
		if (direct != null)
		{
			header.addProperty(IMsgHeader.RECEIVER, getRtComponent(fwdest));
			
			if (debug)
				System.out.println(agent + ": Found direct connection for package destination " + fwdest + " new header: " + header);
			
			direct.addIntermediateResult(new Tuple2<IMsgHeader, byte[]>(header, body));
			ret.setResult(PRIORITY);
			
			return ret;
		}
		
		header.addProperty(IMsgHeader.SENDER, agent.getId());
		
		Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(fwdest);
		if (route != null)
		{
			if (debug)
				System.out.println(agent + " forwarding via known route: " + route.getFirstEntity() + " to " + fwdest);
			header.addProperty(IMsgHeader.RECEIVER, getRtComponent(route.getFirstEntity()));
			
//			ServiceCall.getOrCreateNextInvocation().setTimeout(routingdelay);
			getRoutingService(getRtComponent(route.getFirstEntity())).forwardMessage(header, body).then(priority -> 
			{
				ret.setResult(PRIORITY);
			}).	catchEx(ex ->
			{
				routecache.remove(fwdest);
				ret.setException(ex);
			});
			return ret;
		}
		
		boolean[] notsent = new boolean[] { true };
		discoverRoute(fwdest, new LinkedHashSet<IComponentIdentifier>()).done(ex -> {
			if (notsent[0])
			{
				ret.setException(new IllegalStateException("No route to receiver " + fwdest + " found."));
			}
		}).next(hops -> {
			if (notsent[0])
			{
				notsent[0] = false;
				Tuple2<IComponentIdentifier, Integer> froute = getRouteFromCache(fwdest);
				ServiceCall.getOrCreateNextInvocation().setTimeout(routingdelay);
				getRoutingService(getRtComponent(froute.getFirstEntity())).forwardMessage(header, body).then(prio -> 
				{
					ret.setResultIfUndone(PRIORITY);
				}).	catchEx(ex ->
				{
					routecache.remove(fwdest);
					ret.setException(ex);
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Attempts to find a route to a destination.
	 * 
	 *  @param destination The destination.
	 *  @param hops Previous hops.
	 *  @return Route cost when routing via this route (multiple returns with different costs possible).
	 */
	public IIntermediateFuture<Integer> discoverRoute(final IComponentIdentifier dest, final LinkedHashSet<IComponentIdentifier> hops)
	{
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		
		final IComponentIdentifier destination = dest.getRoot();
		
		// Check if destination can be reached directly or if we are already at the destination.
		if (directconns.containsKey(destination) || agent.getId().getRoot().equals(destination))
		{
			ret.addIntermediateResult(0);
			ret.setFinished();
			return ret;
		}
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				
				if (directconns.containsKey(destination))
				{
					ret.addIntermediateResult(0);
					ret.setFinished();
					return IFuture.DONE;
				}
				
				Set<IComponentIdentifier> rls = null;
				if (forwarding)
				{
					rls = new HashSet<>(relays);
					rls.remove(agent.getId().getRoot());
				}
				else
				{
					rls = keepaliveconnections.keySet();
				}
				
				if (rls.size() == 0)
				{
					ret.setFinished();
					return IFuture.DONE;
				}
				
				final IComponentIdentifier[] frls = rls.toArray(new IComponentIdentifier[rls.size()]);
				AtomicInteger count = new AtomicInteger(0);
				
				for (int i = 0; i < rls.size(); ++i)
				{
					IComponentIdentifier relplat = frls[i];
					if (!hops.contains(relplat))
					{
						IRoutingService rs = getRoutingService(getRtComponent(relplat));
						ServiceCall.getOrCreateNextInvocation().setTimeout(routingdelay);
						hops.add(agent.getId().getRoot());
						rs.discoverRoute(destination, hops).next(hops -> {
							ret.addIntermediateResult(hops + 1);
							
							updateRouteCache(destination, relplat, hops);
						}).done(ex -> {
							int num = count.incrementAndGet();
							if (num == frls.length)
								ret.setFinished();
						});
					}
					else
					{
						int num = count.incrementAndGet();
						if (num == frls.length)
							ret.setFinished();
					}
				}
				
				return IFuture.DONE;
			}
		});
		
		((IInternalExecutionFeature) execfeat).addSimulationBlocker(ret);
		return ret;
	}
	
	public ISubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> connect()
	{
		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller().getRoot();
		SubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> ret = new SubscriptionIntermediateFuture<Tuple2<IMsgHeader,byte[]>>();
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				directconns.remove(client);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});		
		
		directconns.put(client, ret);
		
		SFuture.avoidCallTimeouts(ret, agent, true);
//		SFuture.avoidCallTimeouts(ret, agent);
		
		return ret;
	}
	
	/**
	 *  Sets the transport up to allow forwarding / relay mode.
	 */
	protected void setupForwarding()
	{
		System.out.println("Relay transport in forwarding mode.");
		
		agent.getFeature(IProvidedServicesFeature.class).addService("routing", IRoutingService.class, RelayTransportAgent.this, null, ServiceScope.GLOBAL);
	}
	
	/**
	 *  Sets the transport up to be a relay client.
	 */
	protected void setupClient()
	{
		if (keepalivecount > 0)
		{
			connectstep = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if (keepaliveconnections.size() < keepalivecount)
					{
						for (final IComponentIdentifier id : relays)
						{
							if(debug)
								System.out.println(agent+": sending to " + id);
							
							ISubscriptionIntermediateFuture<Tuple2<IMsgHeader, byte[]>> conn =  getRoutingService(id).connect();
							keepaliveconnections.put(id, conn);
							conn.done(ex -> {
								keepaliveconnections.remove(id);
							});
							
							conn.next(msg -> {
								IMsgHeader header = msg.getFirstEntity();
								IComponentIdentifier fwdest = (IComponentIdentifier) header.getProperty(FORWARD_DEST);
								if (agent.getId().getRoot().equals(fwdest))
								{
									byte[] body = msg.getSecondEntity();
									final List<byte[]> unpacked = SUtil.splitData(body);
									final IComponentIdentifier source = (IComponentIdentifier) header.getProperty(FORWARD_SENDER);
									final ISerializationServices serser = (ISerializationServices) Starter.getPlatformValue(agent.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
									
									AbstractTransportAgent.deliverRemoteMessage(agent, secservice, serser, source, unpacked.get(0), unpacked.get(1));
								}
							});
						}
					}
					
					if (keepaliveconnections.size() < keepalivecount)
					{
						try
						{
							relayupdatefuture.get(keepaliveinterval, true);
						}
						catch (Exception e)
						{
						}
						agent.getFeature(IExecutionFeature.class).scheduleStep(this);
					}
					else
					{
						agent.getFeature(IExecutionFeature.class).waitForDelay(keepaliveinterval >>> 1, this, true);
					}
					return IFuture.DONE;
				}
			};
			agent.getFeature(IExecutionFeature.class).scheduleStep(connectstep);
		}
	}
	
	/**
	 *  Gets a proxy of the routing service of a known relay.
	 *  
	 *  @param relay The relay ID.
	 *  @return Service proxy.
	 */
	protected IRoutingService getRoutingService(IComponentIdentifier relayplatform)
	{
		relayplatform = relayplatform.getRoot();
		IRoutingService ret = routingservicecache.get(relayplatform);
		
		if (ret == null)
		{
			IComponentIdentifier relay = getRtComponent(relayplatform);
			IServiceIdentifier si = BasicService.createServiceIdentifier(relay, new ClassInfo(IRoutingService.class), null, "routing", null, ServiceScope.GLOBAL, null, true);
			ret = (IRoutingService)RemoteMethodInvocationHandler.createRemoteServiceProxy(agent, si);
			routingservicecache.put(relayplatform, ret);
		}
		
		return ret;
	}

	/**
	 *  Gets a route from cache.
	 *  
	 *  @param target The target.
	 *  @return The route, or null if not found.
	 */
	protected Tuple2<IComponentIdentifier, Integer> getRouteFromCache(IComponentIdentifier target)
	{
		Tuple2<IComponentIdentifier, Integer> ret = null;
		ret = routecache.get(target);
		return ret;
	}
	
	protected void updateRouteCache(IComponentIdentifier target, IComponentIdentifier route, int hops)
	{
		boolean update = false;
		try (IAutoLock l = routecache.readLock())
		{
			Tuple2<IComponentIdentifier, Integer> t = routecache.get(target);
			update = t == null || t.getSecondEntity() > hops;
		}
		
		if (update)
		{
			try (IAutoLock l = routecache.writeLock())
			{
				Tuple2<IComponentIdentifier, Integer> t = routecache.get(target);
				if (t == null || t.getSecondEntity() > hops)
				{
					routecache.put(target, new Tuple2<>(route, hops));
				}
			}
		}
	}
	
	/**
	 *  Gets the relay transport component for a platform.
	 *  
	 *  @param platformid The platform ID.
	 *  @return ID of the relay transport component.
	 */
	protected IComponentIdentifier getRtComponent(IComponentIdentifier platformid)
	{
		return new ComponentIdentifier("rt", platformid);
	}
}
