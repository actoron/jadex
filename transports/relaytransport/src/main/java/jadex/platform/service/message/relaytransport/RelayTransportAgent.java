package jadex.platform.service.message.relaytransport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IUntrustedMessageHandler;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Tags;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Boolean3;
import jadex.commons.SConfigParser;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.collection.PassiveLeaseTimeSet;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Feature;
import jadex.micro.annotation.Features;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.platform.service.transport.AbstractTransportAgent;

/**
 *  Agent implementing relay routing.
 */
//@Agent(autoprovide=Boolean3.TRUE)
@Agent(autostart=@Autostart(value=Boolean3.TRUE, name="rt",
	predecessors="jadex.platform.service.registryv2.SuperpeerClientAgent"))
@Arguments({
	// todo: see SuperpeerRegistrySynchronizationAgent
//	@Argument(name="superpeers", clazz=String.class, defaultvalue="\"platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}\""),
//	@Argument(name="addresses", clazz=String.class, defaultvalue="\"ws://ssp1@ngrelay1.actoron.com:80\""),	// TODO: wss, TODO: set in PlatformAgent???
})
@ProvidedServices({
		@ProvidedService(type=ITransportService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM),
		//@ProvidedService(type=IRoutingService.class, name="routing", scope="%{$args.forwarding ? jadex.bridge.service.RequiredServiceInfo.SCOPE_GLOBAL : jadex.bridge.service.RequiredServiceInfo.SCOPE_PLATFORM}")
})
@Features(additional=true, replace=true, value=@Feature(type=IMessageFeature.class, clazz=RelayMessageComponentFeature.class))
@Service
//@Tags("\"forwarding=\"+$args.forwarding")
public class RelayTransportAgent implements ITransportService, IRoutingService
{
	/** Maxmimum number of relays to use. */
	public static final String PROPERTY_RELAY_COUNT = "relaycount";
	
	/** Maximum routing hops. */
	public static final String PROPERTY_MAX_HOPS = "maxhops";
	
	/** Routing table cache size. */
	public static final String PROPERTY_ROUTING_CACHE_SIZE = "routingsize";
	
	/** ID of sender of forwarding messages. */
	public static final String FORWARD_SENDER = "__fw_sender__";
	
	/** ID of forwarding messages. */
	public static final String FORWARD_DEST = "__fw_dest__";

	/** Maximum time spent on finding routing services. */
	protected static final long MAX_ROUTING_SERVICE_DELAY = 3000;
	
	/** Transport priority = low */
	protected static final int PRIORITY = Integer.MIN_VALUE / 2;
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The cms (cached for speed). */
	protected IComponentManagementService	cms;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	/** Relay transport agent's internal message feature. */
	protected IInternalMessageFeature intmsgfeat;
	
	/** Flag if the transport allows forwarding. */
	@AgentArgument
	protected boolean forwarding;
	
	/** Relay addresses to use. */
	//@AgentArgument()
	//protected String addresses;
	
	/** Flag if the transport should dynamically acquire more routing services. */
	@AgentArgument
	protected boolean dynamicrouting;
	
	/** Maintain a connection to at least this number of relays. */
	@AgentArgument
	protected int keepalivecount = 1;
	
	/** Delay of keepalive messages. */
	@AgentArgument
	protected long keepaliveinterval = 30000;
	
	/** Set to true for more verbose output. */
	@AgentArgument
	protected boolean debug;
	
	/** Timestamp of the next clean for direct routes. */
	protected long nextclean = System.currentTimeMillis();
	
	/** Maximum allowed routing hops. */
	protected int maxhops = 4;
	
	/** List of relays. */
	protected LinkedHashSet<IComponentIdentifier> relays = new LinkedHashSet<IComponentIdentifier>();
	
	/** List of working connections to relays. */
	protected Set<IComponentIdentifier> keepaliveconnections;
	
	/** Component step used to connect to relays. */
	protected IComponentStep<Void> connectstep;
	
	/** Cache for routing service proxies. */
	protected LRU<IComponentIdentifier, IRoutingService> routingservicecache;
	
	/** Directly connected platforms. */
	protected PassiveLeaseTimeSet<IComponentIdentifier> directconns;
	
	/** Routing information (target platform / next route hop + cost). */
	protected LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>> routes;
	
	/** Update future to shorten timeout when keepalive update happens. */
	protected Future<Void> keepaliveupdatefuture = new Future<>();
	
	/** Update future to shorten timeout when query update happens. */
	protected Future<Void> queryupdatefuture = new Future<>();
	
	/**
	 *  Creates the agent.
	 */
	public RelayTransportAgent()
	{
		keepaliveconnections = new LinkedHashSet<IComponentIdentifier>();
		routingservicecache = new LRU<IComponentIdentifier, IRoutingService>(10);
	}
	
	/**
	 *  Agent initialization.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		this.cms = ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(IComponentManagementService.class);
		intmsgfeat = (IInternalMessageFeature) agent.getFeature(IMessageFeature.class);
		if(debug)
			System.out.println(agent+": started relay transport");
		Map<String, Object> args = agent.getFeature(IArgumentsResultsFeature.class).getArguments();
		
		maxhops = SConfigParser.getIntValue(args.get(PROPERTY_MAX_HOPS), maxhops);
		
		secservice = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ISecurityService.class));
		
		int cachesize = SConfigParser.getIntValue(PROPERTY_ROUTING_CACHE_SIZE, 5000);
		routes = new LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>>(cachesize, null, true);
		
		directconns = new PassiveLeaseTimeSet<IComponentIdentifier>(keepaliveinterval << 2);
		
		ServiceQuery<IRoutingService> query = new ServiceQuery<>(IRoutingService.class);
		query.setScope(RequiredServiceInfo.SCOPE_GLOBAL).setExcludeOwner(true);//.setServiceTags("forwarding=true");
		agent.getFeature(IRequiredServicesFeature.class).addQuery(query).addResultListener(new IIntermediateResultListener<IRoutingService>()
		{
			public void intermediateResultAvailable(IRoutingService result)
			{
				relays.add(((IService) result).getId().getProviderId().getRoot());
				if (debug)
					System.out.println(agent + ": Got query update, releasing wait future.");
				queryupdatefuture.setResult(null);
				queryupdatefuture = new Future<>();
			}

			public void resultAvailable(Collection<IRoutingService> result)
			{
			}

			public void exceptionOccurred(Exception exception)
			{
			}

			public void finished()
			{
			}
		});
		
		IMessageFeature msgfeat = agent.getFeature(IMessageFeature.class);
//		forwarding = SConfigParser.getBoolValue(args.get(PROPERTY_FORWARDING));
		if (forwarding)
		{
			System.out.println("Relay transport in forwarding mode.");
			
			agent.getFeature(IProvidedServicesFeature.class).addService("routing", IRoutingService.class, RelayTransportAgent.this, null, RequiredServiceInfo.SCOPE_GLOBAL);
			
			IUntrustedMessageHandler handler = new IUntrustedMessageHandler()
			{
				public boolean isRemove()
				{
					return false;
				}
				
				public boolean isHandling(ISecurityInfo secinfos, IMsgHeader header, Object msg)
				{
					return msg instanceof Ping || (msg instanceof byte[] && header.getProperty(FORWARD_DEST) != null);
				}
				
				public void handleMessage(ISecurityInfo secinfos, IMsgHeader header, Object msg)
				{
					if (msg instanceof Ping)
					{
						synchronized(directconns)
						{
//							System.out.println("State: dc=" + directconns.size() + " rsc=" + routingservicecache.size() + " routes=" + routes.size());
							directconns.add(((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER)).getRoot());
							directconns.checkStale();
						}
//						System.out.println("Got ping, dc size: " + directconnections.size());
						
						Ack ack = new Ack();
						agent.getFeature(IMessageFeature.class).sendReply(header, ack);
					}
					else
					{
						sendMessage(header, (byte[]) msg);
					}
				}
			};
			((RelayMessageComponentFeature) msgfeat).setRelayMessageHandler(handler);
			msgfeat.addMessageHandler(handler);
		}
		else
		{
			((RelayMessageComponentFeature) msgfeat).setRelayMessageHandler(new IUntrustedMessageHandler()
			{
				public boolean isRemove()
				{
					return false;
				}
				
				public boolean isHandling(ISecurityInfo secinfos, IMsgHeader header, Object msg)
				{
					return msg instanceof byte[] && header.getProperty(FORWARD_DEST) != null;
				}
				
				public void handleMessage(ISecurityInfo secinfos, IMsgHeader header, Object msg)
				{
					IComponentIdentifier fwdest = (IComponentIdentifier) header.getProperty(FORWARD_DEST);
					if (fwdest != null && agent.getId().getRoot().equals(fwdest))
						sendMessage(header, (byte[]) msg);
				}
			});
			
			if (keepalivecount > 0)
			{
				connectstep = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IMessageFeature msgfeat = ia.getFeature(IMessageFeature.class);
						if (keepaliveconnections.size() < keepalivecount)
						{
							for (final IComponentIdentifier id : relays)
							{
								if(debug)
									System.out.println(agent+": sending to " + id);
								msgfeat.sendMessageAndWait(getRtComponent(id), new Ping()).addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										if(debug)
											System.out.println(agent+": got answer " + id);
										if (keepaliveconnections.size() < keepalivecount)
											keepaliveconnections.add(id);
										keepaliveupdatefuture.setResult(null);
										keepaliveupdatefuture = new Future<>();
									}
									
									public void exceptionOccurred(Exception exception)
									{
										if(debug)
											System.out.println(agent+": got exception:  " + exception);
									}
								});
							}
						}
						else
						{
							for (final IComponentIdentifier id : keepaliveconnections)
							{
								msgfeat.sendMessageAndWait(getRtComponent(id), new Ping()).addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
									}
									
									public void exceptionOccurred(Exception exception)
									{
										keepaliveconnections.remove(id);
									}
								});
							}
						}
						if (keepaliveconnections.size() < keepalivecount)
						{
							try
							{
								queryupdatefuture.get(keepaliveinterval, true);
								agent.getFeature(IExecutionFeature.class).scheduleStep(this);
							}
							catch (Exception e)
							{
							}
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
		
		return IFuture.DONE;
	}
	
	/**
	 *  Checks if the transport is ready.
	 * 
	 *  @param header Message header.
	 *  @return Transport priority, when ready
	 */
	public IFuture<Integer> isReady(final IMsgHeader header)
	{
		final Future<Integer> ret = new Future<Integer>();
		
//		IComponentIdentifier sender = (IComponentIdentifier) header.getProperty(IMsgHeader.SENDER);
		IComponentIdentifier rec = (IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER);
//		agent.getLogger().info("Relay is ready: "+rec);
		if (relays.contains(rec.getRoot()) || directconns.contains(rec.getRoot()))
		{
			ret.setException(new IllegalArgumentException("Cannot transport relay messages using relay."));
		}
		else
		{
			if (getRouteFromCache(rec.getRoot()) != null)
			{
				ret.setResult(PRIORITY);
			}
			else
			{
				agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						final IComponentIdentifier destination = (IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER);
						agent.getLogger().info("Relay discover route: "+destination);
						discoverRoute(destination, new LinkedHashSet<IComponentIdentifier>()).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
						{
							public void exceptionOccurred(Exception exception)
							{
								agent.getLogger().info("Relay discover route exception: "+destination+", "+exception);
								ret.setException(exception);
							}
							
							public void resultAvailable(Collection<Integer> result)
							{
								agent.getLogger().info("Relay discover route result: "+destination+", "+result);
							}
							
							public void intermediateResultAvailable(Integer result)
							{
								agent.getLogger().info("Relay discover route intermediate result: "+destination+", "+result);
								ret.setResult(PRIORITY);
							}
							
							public void finished()
							{
								agent.getLogger().info("Relay discover route finished: "+destination);
							}
						});
						return IFuture.DONE;
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(IMsgHeader header, byte[] body)
	{
		IComponentIdentifier fwdest = null;
		
		if (header.getProperty(FORWARD_DEST) == null)
		{
			fwdest = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot();
			
			IComponentIdentifier fwsender = ((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER)).getRoot();
			ISerializationServices serserv = (ISerializationServices) Starter.getPlatformValue(agent.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
			byte[] bheader = serserv.encode(header, agent, header);
			bheader = secservice.encryptAndSign(header, bheader).get();
			
			body = SUtil.mergeData(bheader, body);
			
			if(debug)
			{
				System.out.println(agent+": preparing forward package for " + fwdest + " from " + fwsender + " orig header " + header);
			}
			
			header = new MsgHeader();
			
			header.addProperty(FORWARD_SENDER, fwsender);
			header.addProperty(FORWARD_DEST, fwdest);
		}
		else
		{
			fwdest = (IComponentIdentifier) header.getProperty(FORWARD_DEST);
			if(debug)
			{
				IComponentIdentifier fwsender = ((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER)).getRoot();
				System.out.println(agent+": processing forward package for " + fwdest + " from " + fwsender);
			}
		}
		
		if (agent.getId().getRoot().equals(fwdest))
		{
			final List<byte[]> unpacked = SUtil.splitData(body);
			final IComponentIdentifier source = (IComponentIdentifier) header.getProperty(FORWARD_SENDER);
			final ISerializationServices serser = (ISerializationServices) Starter.getPlatformValue(agent.getId().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
			
//			System.out.println("Final receiver, delivering to component: " + body);
			AbstractTransportAgent.deliverRemoteMessage(agent, secservice, cms, serser, source, unpacked.get(0), unpacked.get(1));
			
			return IFuture.DONE;
		}
		
		if (hasDirectConnection(fwdest))
		{
			header.addProperty(IMsgHeader.RECEIVER, getRtComponent(fwdest));
			IFuture<Void> ret = intmsgfeat.sendToTransports(header, body);
			final IComponentIdentifier ffwdest = fwdest;
			ret.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					synchronized(directconns)
					{
						directconns.checkStale();
						directconns.remove(ffwdest);
					}
				}
			});
			return ret;
		}
		
		header.addProperty(IMsgHeader.SENDER, agent.getId());
		
		Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(fwdest);
		if (route != null)
		{
			header.addProperty(IMsgHeader.RECEIVER, getRtComponent(route.getFirstEntity()));
//			System.out.println("sending to route target: " + route.getFirstEntity() + " " + header.getProperty(FORWARD_DEST));
			return intmsgfeat.sendToTransports(header, body);
		}
		
		final Future<Void> ret = new Future<Void>();
		final IMsgHeader fheader = header;
		final byte[] fbody = body;
		final IComponentIdentifier ffwdest = fwdest;
		discoverRoute(fwdest, new LinkedHashSet<IComponentIdentifier>()).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
		{
			boolean notsent = true;
			
			public void exceptionOccurred(Exception exception)
			{
			}
			
			public void resultAvailable(Collection<Integer> result)
			{
			}
			
			public void intermediateResultAvailable(Integer result)
			{
				Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(ffwdest);
				if (route != null && notsent)
				{
					fheader.addProperty(IMsgHeader.RECEIVER, getRtComponent(route.getFirstEntity()));
					intmsgfeat.sendToTransports(fheader, fbody).addResultListener(new DelegationResultListener<Void>(ret));
					ret.addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							synchronized(routes)
							{
								routes.remove(ffwdest);
							}
						}
					});
					notsent = false;
				}
			}
			
			public void finished()
			{
				if (notsent)
					ret.setException(new IllegalStateException("No route to receiver found."));
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
		if (debug)
			System.out.println(agent+": discover route on " + agent.getId() + " for " + dest);
		
		final IComponentIdentifier destination = dest.getRoot();
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		
		if (hops.contains(agent.getId().getRoot()) || hops.size() + 1 > maxhops)
		{
			ret.setException(new IllegalStateException("Loop detected or TTL exceeded: " + agent.getId() + " " + Arrays.toString(hops.toArray())));
			return ret;
		}
		
		if (hasDirectConnection(destination) || agent.getId().getRoot().equals(destination))
		{
			ret.addIntermediateResult(0);
			ret.setFinished();
			return ret;
		}
		
		Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(destination);
		if (route != null)
		{
			if (hops.contains(route.getFirstEntity()))
			{
				synchronized(routes)
				{
					routes.remove(destination);
					route = null;
				}
			}
			else
			{
				ret.addIntermediateResult(route.getSecondEntity());
				ret.setFinished();
				return ret;
			}
		}
		
		final LinkedHashSet<IComponentIdentifier> newhops = new LinkedHashSet<IComponentIdentifier>(hops);
		newhops.add(agent.getId().getRoot());
		agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Map<IComponentIdentifier, IRoutingService> routingservices = new HashMap<IComponentIdentifier, IRoutingService>();
				Collection<IComponentIdentifier> rls = null;
				if (forwarding)
				{
					rls = relays;
				}
				else
				{
					if (keepaliveconnections.size() == 0)
					{
						if (debug)
							System.out.println(agent + ": Relays is 0, waiting for query update.");
						try
						{
							keepaliveupdatefuture.get(MAX_ROUTING_SERVICE_DELAY, true);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						if (debug)
							System.out.println(agent + ": Relays was 0, now:" + relays.size());
					}
					
					rls = keepaliveconnections;
				}
				
				for (IComponentIdentifier relayid : rls)
				{
					if (!newhops.contains(relayid))
					{
						IRoutingService rs = getRoutingService(relayid);
						boolean valid = false;
						if (rs != null)
						{
							try
							{
								valid = ((IService) rs).isValid().get();
							}
							catch (Exception e)
							{
							}
						}
						if (rs != null && valid)
							routingservices.put(relayid, rs);
					}
				}
				
//				System.out.println("Asking routing service for " + dest + ": " + Arrays.toString(routingservices.values().toArray()));
				
				final Future<Void> directroute = new Future<Void>();
				if (routingservices.size() == 0)
				{
					directroute.setException(new IllegalStateException("No routing services found, could not establish route to receiver."));
				}
				else
				{
					final int[] count = new int[1];
					count[0] = routingservices.size();
					
					for (final Map.Entry<IComponentIdentifier, IRoutingService> routingsrv : routingservices.entrySet())
					{
						final IComponentIdentifier routetarget = routingsrv.getKey();
						routingsrv.getValue().discoverRoute(destination, newhops).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
						{
							/** Flag if a route was found. */
							protected boolean routefound = false;
							
							public void exceptionOccurred(Exception exception)
							{
//								exception.printStackTrace();
								agent.getLogger().info("Relay "/*+routingsrv.getValue()*/+" routing exception: "+exception);
							}
							
							public void resultAvailable(Collection<Integer> result)
							{
								agent.getLogger().info("Relay "/*+routingsrv.getValue()*/+" result available: "+result);
							}
							
							public void intermediateResultAvailable(Integer result)
							{
								agent.getLogger().info("Relay "/*+routingsrv.getValue()*/+" intermediate result available: "+result);
								++result;
								routefound = true;
								synchronized(routes)
								{
									Tuple2<IComponentIdentifier, Integer> route = routes.get(destination);
									if (route == null || route.getSecondEntity() > result)
										routes.put(destination, new Tuple2<IComponentIdentifier, Integer>(routetarget, result));
								}
								ret.addIntermediateResult(result);
							}
							
							public void finished()
							{
								agent.getLogger().info("Relay "/*+routingsrv.getValue()*/+" finished.");
								--count[0];
								if (count[0] == 0)
								{
									if (routefound)
										directroute.setResult(null);
									else
										directroute.setException(new RuntimeException("No direct connection found."));
								}
							}
						});
					}
				}
				
				directroute.addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setFinished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if (!forwarding || !dynamicrouting)
						{
							ret.setException(exception);
//							ret.setException(new RuntimeException("No route found to " + destination + " found: "+exception));
							return;
						}
						
						// Try to acquire more relays with service search.
						
						List<IRoutingService> filteredaddrs = new ArrayList<IRoutingService>();
						try
						{
//							Collection<IRoutingService> addrs = SServiceProvider.getServices(agent, IRoutingService.class, Binding.SCOPE_GLOBAL).get(MAX_ROUTING_SERVICE_DELAY, true);
							Collection<IRoutingService> addrs = agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IRoutingService.class, RequiredService.SCOPE_GLOBAL).setServiceTags(new String[]{"forwarding=true"})).get(MAX_ROUTING_SERVICE_DELAY, true);
							for (IRoutingService rs : addrs)
							{
								IComponentIdentifier rsprov = ((IService) rs).getId().getProviderId();
								if (!routingservices.containsKey(rsprov) && !agent.getId().equals(rsprov))
								{
									filteredaddrs.add(rs);
								}
								
							}
							
							
						}
						catch (Exception e)
						{
						}
						
						if (filteredaddrs.size() > 0)
						{
							final int[] count = new int[1];
							count[0] = routingservices.size();
							for (IRoutingService rs : filteredaddrs)
							{
								final IComponentIdentifier routetarget = ((IService) rs).getId().getProviderId();
								rs.discoverRoute(destination, newhops).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
								{
									public void exceptionOccurred(Exception exception)
									{
									}
									
									public void resultAvailable(Collection<Integer> result)
									{
									}
									
									public void intermediateResultAvailable(Integer result)
									{
										++result;
										synchronized(routes)
										{
											Tuple2<IComponentIdentifier, Integer> route = routes.get(destination);
											if (route == null || route.getSecondEntity() > result)
												routes.put(destination, new Tuple2<IComponentIdentifier, Integer>(routetarget, result));
										}
										ret.addIntermediateResult(result);
									}
									
									public void finished()
									{
										--count[0];
										if (count[0] == 0)
											ret.setFinished();
									}
								});
							}
						}
						else
						{
							ret.setFinished();
						}
					}
				});
				
				return IFuture.DONE;
			}
		});
		
		return ret;
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
			IServiceIdentifier si = BasicService.createServiceIdentifier(relay, new ClassInfo(IRoutingService.class), null, "routing", null, RequiredService.SCOPE_GLOBAL, null, true);
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
		synchronized(routes)
		{
			ret = routes.get(target);
		}
		return ret;
	}
	
	/**
	 *  Checks if a direct connection to target exists.
	 *  
	 *  @param target The target.
	 *  @return True, if connection exists.
	 */
	protected boolean hasDirectConnection(IComponentIdentifier target)
	{
		if (target == null)
			return false;
		
		boolean ret = false;
		synchronized(directconns)
		{
			directconns.checkStale();
			ret = directconns.contains(target.getRoot());
		}
		return ret;
	}
	
	/**
	 *  Gets the relay transport component for a platform.
	 *  
	 *  @param platformid The platform ID.
	 *  @return ID of the relay transport component.
	 */
	protected IComponentIdentifier getRtComponent(IComponentIdentifier platformid)
	{
		return new BasicComponentIdentifier("rt", platformid);
	}
	
	/** Ping message. */
	public static class Ping
	{
		/**
		 *  Creates the ping message.
		 */
		public Ping()
		{
		}
	}
	
	/** Acknowledgement message. */
	public static class Ack
	{
		/**
		 *  Creates the ack message.
		 */
		public Ack()
		{
		}
	}
}
