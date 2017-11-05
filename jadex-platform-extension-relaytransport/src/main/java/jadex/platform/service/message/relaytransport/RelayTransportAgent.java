package jadex.platform.service.message.relaytransport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.IUntrustedMessageHandler;
import jadex.bridge.component.impl.IInternalMessageFeature;
import jadex.bridge.component.impl.MsgHeader;
import jadex.bridge.component.impl.remotecommands.ProxyInfo;
import jadex.bridge.component.impl.remotecommands.ProxyReference;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
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
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Feature;
import jadex.micro.annotation.Features;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.serialization.RemoteMethodInvocationHandler;
import jadex.platform.service.transport.AbstractTransportAgent;

/**
 *  Agent implementing relay routing.
 */
@Agent(autoprovide=Boolean3.TRUE)
// todo: see SuperpeerRegistrySynchronizationAgent
//@Arguments({
//	@Argument(name="superpeers", clazz=String.class, defaultvalue="\"platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}\""),
//})
@ProvidedServices({
		@ProvidedService(type=ITransportService.class),
		@ProvidedService(type=IRoutingService.class, name="routing")
})
@Features(additional=true, value=@Feature(type=IMessageFeature.class, clazz=RelayMessageComponentFeature.class))
@Service
public class RelayTransportAgent implements ITransportService, IRoutingService
{
	/** True/false if the transport allows forwarding. */
	public static final String PROPERTY_FORWARDING = "forwarding";
	
	/** Maxmimum number of relays to use. */
	public static final String PROPERTY_RELAY_COUNT = "relaycount";
	
	/** Maxmimum number of connections per keepalive set. */
	public static final String PROPERTY_RELAYS = "relays";
	
	/** Delay of keepalive messages. */
	public static final String PROPERTY_RELAY_HEARTBEAT = "relayheartbeat";
	
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
	protected boolean forwarding;
	
	/** Maintain a connection to at least this number of relays. */
	protected int keepalivecount = 1;
	
	/** Delay of keepalive messages. */
	protected long relayping = 30000;
	
	/** Timestamp of the next clean for direct routes. */
	protected long nextclean = System.currentTimeMillis();
	
	/** Maximum allowed routing hops. */
	protected int maxhops = 16;
	
	/** List of relays. */
	protected List<IComponentIdentifier> relays = new ArrayList<IComponentIdentifier>();
	
	/** List of working connections to relays. */
	protected Set<IComponentIdentifier> keepaliveconnections;
	
	/** Cache for routing service proxies. */
	protected LRU<IComponentIdentifier, IRoutingService> routingservicecache;
	
	/** Directly connected platforms. */
	protected PassiveLeaseTimeSet<IComponentIdentifier> directconns;
	
	/** Routing information (target platform / next route hop + cost). */
	protected LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>> routes;
	
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
		this.cms = SServiceProvider.getLocalService(agent, IComponentManagementService.class, Binding.SCOPE_PLATFORM, false);
		intmsgfeat = (IInternalMessageFeature) agent.getComponentFeature(IMessageFeature.class);
		System.out.println("Started relay transport");
		Map<String, Object> args = agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		
		relayping = SConfigParser.getLongValue(args.get(PROPERTY_RELAY_HEARTBEAT), relayping);
		
		maxhops = SConfigParser.getIntValue(args.get(PROPERTY_MAX_HOPS), maxhops);
		
		secservice = SServiceProvider.getLocalService(agent, ISecurityService.class);
		
		int cachesize = SConfigParser.getIntValue(PROPERTY_ROUTING_CACHE_SIZE, 5000);
		routes = new LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>>(cachesize, null, true);
		
		directconns = new PassiveLeaseTimeSet<IComponentIdentifier>(relayping << 2);
		
		String setstr = (String) args.get(PROPERTY_RELAYS);
		List<TransportAddress> relayaddrs = new ArrayList<TransportAddress>();
		if (setstr != null)
		{
//			System.out.println("addr: " + setstr);
			Pattern urlpat = Pattern.compile("[a-zA-Z]+://[a-zA-Z0-9]+@.+:[0-9]+");
			String[] ids = setstr.split(",");
//			int max = ids.length / 3;
//			for (int i = 0; i < max; ++i)
			for (int i = 0; i < ids.length; ++i)
			{
//				int off = i * 3;
//				relays.add(new BasicComponentIdentifier("rt@" + ids[off]));
//				TransportAddress addr = new TransportAddress(new BasicComponentIdentifier(ids[off]), ids[off+1], ids[off+2]);
//				relayaddrs.add(addr);
				
				String url = ids[i].trim();
				Matcher m = urlpat.matcher(url);
				if (m.matches())
				{
					int protend = url.indexOf(':');
					String prot = url.substring(0, protend);
					int nameend = url.indexOf('@', protend + 1);
					String name = url.substring(protend + 3, nameend);
					String addr = url.substring(nameend + 1);
//					System.out.println("Relay: " + prot + " " + name + " " + addr);
					relays.add(new BasicComponentIdentifier("rt@" + name));
					TransportAddress ta = new TransportAddress(new BasicComponentIdentifier(name), prot, addr);
					relayaddrs.add(ta);
				}
				else
				{
//					System.out.println("Unknown relay specification: " + ids[i] + ". Format: transport://platformname@hostname:port");
					agent.getLogger().warning("Unknown relay specification: " + ids[i] + ". Format: transport://platformname@hostname:port");
				}
			}
			if (relayaddrs.size() > 0)
			{
				ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class);
				tas.addManualAddresses(relayaddrs).get();
			}
		}
		
		IMessageFeature msgfeat = agent.getComponentFeature(IMessageFeature.class);
		msgfeat.setAllowUntrusted(true);
		forwarding = SConfigParser.getBoolValue(args.get(PROPERTY_FORWARDING));
		if (forwarding)
		{
			System.out.println("Relay transport in forwarding mode.");
			
			msgfeat.addMessageHandler(new IUntrustedMessageHandler()
			{
				public boolean isRemove()
				{
					return false;
				}
				
				public boolean isHandling(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
				{
					return msg instanceof Ping || (msg instanceof byte[] && header.getProperty(FORWARD_DEST) != null);
				}
				
				public void handleMessage(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
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
						agent.getComponentFeature(IMessageFeature.class).sendReply(header, ack);
					}
					else
					{
						sendMessage(header, (byte[]) msg);
					}
				}
			});
		}
		else
		{
			msgfeat.addMessageHandler(new IUntrustedMessageHandler()
			{
				public boolean isRemove()
				{
					return false;
				}
				
				public boolean isHandling(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
				{
					return msg instanceof byte[] && header.getProperty(FORWARD_DEST) != null;
				}
				
				public void handleMessage(IMsgSecurityInfos secinfos, IMsgHeader header, Object msg)
				{
					IComponentIdentifier fwdest = (IComponentIdentifier) header.getProperty(FORWARD_DEST);
					if (fwdest != null && agent.getComponentIdentifier().getRoot().equals(fwdest))
						sendMessage(header, (byte[]) msg);
				}
			});
		}
		
		if (relays.size() > 0 && keepalivecount > 0)
		{
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IMessageFeature msgfeat = ia.getComponentFeature(IMessageFeature.class);
					if (keepaliveconnections.size() < keepalivecount)
					{
						for (final IComponentIdentifier id : relays)
						{
							if (!keepaliveconnections.contains(id))
							{
								System.out.println("Sending to " + id);
								msgfeat.sendMessageAndWait(id, new Ping()).addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										System.out.println("Got answer " + id);
										if (keepaliveconnections.size() < keepalivecount)
											keepaliveconnections.add(id);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										System.out.println("Got exception:  " + exception);
									}
								});
							}
						}
					}
					else
					{
						for (final IComponentIdentifier id : keepaliveconnections)
						{
							msgfeat.sendMessageAndWait(id, new Ping()).addResultListener(new IResultListener<Object>()
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
					agent.getComponentFeature(IExecutionFeature.class).waitForDelay(relayping >>> 1, this);
					return IFuture.DONE;
				}
			});
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
		
		IComponentIdentifier sender = (IComponentIdentifier) header.getProperty(IMsgHeader.SENDER);
		IComponentIdentifier rec = (IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER);
		agent.getLogger().info("Relay is ready: "+rec);
		if ("relay".equals(rec.getRoot().toString()))
			return new Future<Integer>(new Exception());
		if (agent.getComponentIdentifier().equals(sender) || relays.contains(rec.getRoot()))
		{
			ret.setException(new IllegalArgumentException("Cannot transport relay messages using relay."));
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
		
		return ret;
	}
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(final IMsgHeader header, final byte[] body)
	{
		final IComponentIdentifier destination = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot();
		IMsgHeader newheader = header;
		byte[] newbody = body;
		
		if (header.getProperty(FORWARD_DEST) == null)
		{
			ISerializationServices serserv = (ISerializationServices) Starter.getPlatformValue(agent.getComponentIdentifier().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
			byte[] bheader = serserv.encode(header, agent, header);
			bheader = secservice.encryptAndSign(header, bheader).get();
			
			newbody = SUtil.mergeData(bheader, body);
			
			newheader = new MsgHeader();
			
			newheader.addProperty(FORWARD_SENDER, ((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER)).getRoot());
			newheader.addProperty(FORWARD_DEST, ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot());
		}
		
		IComponentIdentifier fwdest = (IComponentIdentifier) newheader.getProperty(FORWARD_DEST);
		
		if (agent.getComponentIdentifier().getRoot().equals(fwdest))
		{
			final List<byte[]> unpacked = SUtil.splitData(newbody);
			final IComponentIdentifier source = (IComponentIdentifier) header.getProperty(FORWARD_SENDER);
			final ISerializationServices serser = (ISerializationServices) Starter.getPlatformValue(agent.getComponentIdentifier().getRoot(), Starter.DATA_SERIALIZATIONSERVICES);
			
//			System.out.println("Final receiver, delivering to component: " + body);
			AbstractTransportAgent.deliverRemoteMessage(agent, secservice, cms, serser, source, unpacked.get(0), unpacked.get(1));
			
			return IFuture.DONE;
		}
		
		if (hasDirectConnection(fwdest))
		{
			newheader.addProperty(IMsgHeader.RECEIVER, new BasicComponentIdentifier("rt@" + fwdest));
			return intmsgfeat.sendToTransports(newheader, newbody);
		}
		
		newheader.addProperty(IMsgHeader.SENDER, agent.getComponentIdentifier());
		
		Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(destination);
		if (route != null)
		{
			newheader.addProperty(IMsgHeader.RECEIVER, route.getFirstEntity());
//			System.out.println("sending to route target: " + route.getFirstEntity());
			return intmsgfeat.sendToTransports(newheader, newbody);
		}
		
		final Future<Void> ret = new Future<Void>();
		final IMsgHeader fnewheader = newheader;
		final byte[] fnewbody = newbody;
		discoverRoute(destination, new LinkedHashSet<IComponentIdentifier>()).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
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
				Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(destination);
				if (route != null)
				{
					fnewheader.addProperty(IMsgHeader.RECEIVER, route.getFirstEntity());
					IInternalMessageFeature intmsgfeat = agent.getComponentFeature(IInternalMessageFeature.class);
					intmsgfeat.sendToTransports(fnewheader, fnewbody).addResultListener(new DelegationResultListener<Void>(ret));
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
	public IIntermediateFuture<Integer> discoverRoute(final IComponentIdentifier dest, LinkedHashSet<IComponentIdentifier> hops)
	{
//		System.out.println("Discover route on " + agent.getComponentIdentifier());
		final IComponentIdentifier destination = dest.getRoot();
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		
		if (hops.contains(agent.getComponentIdentifier()) || hops.size() + 1 > maxhops)
		{
			ret.setException(new IllegalStateException("Loop detected or TTL exceeded."));
			return ret;
		}
		
		if (hasDirectConnection(destination))
		{
			ret.addIntermediateResult(0);
			ret.setFinished();
			return ret;
		}
		
		Tuple2<IComponentIdentifier, Integer> route = getRouteFromCache(destination);
		if (route != null)
		{
			ret.addIntermediateResult(route.getSecondEntity());
			ret.setFinished();
			return ret;
		}
		
		final LinkedHashSet<IComponentIdentifier> newhops = new LinkedHashSet<IComponentIdentifier>(hops);
		newhops.add(agent.getComponentIdentifier());
		agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Map<IComponentIdentifier, IRoutingService> routingservices = new HashMap<IComponentIdentifier, IRoutingService>();
				for (IComponentIdentifier relayid : keepaliveconnections)
				{
					IRoutingService rs = getRoutingService(relayid);
					if (rs != null && ((IService) rs).isValid().get())
						routingservices.put(relayid, rs);
				}
				
				final Future<Void> directroute = new Future<Void>();
				if (routingservices == null || routingservices.size() == 0)
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
						if (!forwarding)
						{
							ret.setException(new RuntimeException("No route found: "+exception));
							return;
						}
						
						// Try to acquire more relays with service search.
						
						List<IRoutingService> filteredaddrs = new ArrayList<IRoutingService>();
						try
						{
							Collection<IRoutingService> addrs = SServiceProvider.getServices(agent, IRoutingService.class, Binding.SCOPE_GLOBAL).get(MAX_ROUTING_SERVICE_DELAY);
							for (IRoutingService rs : addrs)
							{
								IComponentIdentifier rsprov = ((IService) rs).getServiceIdentifier().getProviderId();
								if (!routingservices.containsKey(rsprov) && !agent.getComponentIdentifier().equals(rsprov))
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
								final IComponentIdentifier routetarget = ((IService) rs).getServiceIdentifier().getProviderId();
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
	protected IRoutingService getRoutingService(IComponentIdentifier relay)
	{
		IRoutingService ret = routingservicecache.get(relay);
		
		if (ret == null)
		{
			Class<?>[] interfaces = new Class[] { IRoutingService.class, IService.class };
			
			ProxyInfo pi = new ProxyInfo(interfaces);
			IServiceIdentifier si = BasicService.createServiceIdentifier(relay, "routing", IRoutingService.class, IRoutingService.class, null, Binding.SCOPE_GLOBAL, true);
			RemoteReference rr = new RemoteReference(relay, si);
			ProxyReference pr = new ProxyReference(pi, rr);
			
			ret = (IRoutingService) ProxyFactory.newProxyInstance(agent.getClassLoader(), 
				interfaces, new RemoteMethodInvocationHandler(agent, pr));
			
			routingservicecache.put(relay, ret);
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
