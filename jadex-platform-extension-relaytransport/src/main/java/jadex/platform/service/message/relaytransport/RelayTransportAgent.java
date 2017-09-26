package jadex.platform.service.message.relaytransport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jadex.base.IStarterConfiguration;
import jadex.base.PlatformConfiguration;
import jadex.bridge.BasicComponentIdentifier;
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
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
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
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent implementing relay routing.
 */
@Agent(autoprovide=Boolean3.TRUE)
// todo: see SuperpeerRegistrySynchronizationAgent
//@Arguments({
//	@Argument(name="superpeers", clazz=String.class, defaultvalue="\"platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}\""),
//})
@ProvidedServices({
//		@ProvidedService(type=ITransportService.class, implementation=@Implementation(expression="$pojoagent", proxytype=Implementation.PROXYTYPE_RAW)),
		@ProvidedService(type=ITransportService.class),
		@ProvidedService(type=IRoutingService.class)
})
public class RelayTransportAgent implements ITransportService, IRoutingService
{
	/** True/false if the is a relay as opposed to using a relay. */
	public static final String PROPERTY_RELAY = "relay";
	
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
	
	/** Transport priority = low */
	protected static final int PRIORITY = (Integer.MAX_VALUE >>> 1);
	
	/** ID of forwarding messages. */
	public static final String FORWARD_DEST = "__fw_dest__";
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	/** Flag if the platform is itself a relay. */
//	protected boolean isrelay;
	
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
	protected List<IComponentIdentifier> keepaliveconnections;
	
	/** Directly connected platforms. */
	protected PassiveLeaseTimeSet<IComponentIdentifier> directconnections;
	
	/** Routing information (target platform / next route hop + cost). */
	protected LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>> routes;
	
	/**
	 *  Creates the agent.
	 */
	public RelayTransportAgent()
	{
		keepaliveconnections = new ArrayList<IComponentIdentifier>();
	}
	
	/**
	 *  Agent initialization.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		System.out.println("Started relay transport");
		Map<String, Object> args = agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		
		relayping = SConfigParser.getLongValue(args.get(PROPERTY_RELAY_HEARTBEAT), relayping);
		
		maxhops = SConfigParser.getIntValue(args.get(PROPERTY_MAX_HOPS), maxhops);
		
		secservice = SServiceProvider.getLocalService(agent, ISecurityService.class);
		
		int cachesize = SConfigParser.getIntValue(PROPERTY_ROUTING_CACHE_SIZE, 5000);
		routes = new LRU<IComponentIdentifier, Tuple2<IComponentIdentifier, Integer>>(cachesize, null, true);
		
		directconnections = new PassiveLeaseTimeSet<IComponentIdentifier>(relayping << 2);
		
		String setstr = (String) args.get(PROPERTY_RELAYS);
//		setstr="relay,ws,127.0.0.1:8080";
		
		List<TransportAddress> relayaddrs = new ArrayList<TransportAddress>();
		if (setstr != null)
		{
			String[] ids = setstr.split(",");
			int max = ids.length / 3;
			for (int i = 0; i < max; ++i)
			{
				int off = i * 3;
				relays.add(new BasicComponentIdentifier("RelayTransport" + ids[off]));
				TransportAddress addr = new TransportAddress(new BasicComponentIdentifier(ids[off]), ids[off+1], ids[off+2]);
				relayaddrs.add(addr);
			}
			if (relayaddrs.size() > 0)
			{
				ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class);
				tas.addManualAddresses(relayaddrs).get();
			}
		}
		
		IMessageFeature msgfeat = agent.getComponentFeature(IMessageFeature.class);
		boolean isrelay = SConfigParser.getBoolValue(args.get(PROPERTY_RELAY));
		if (isrelay)
		{
			msgfeat.setAllowUntrusted(true);
			
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
						directconnections.add((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER));
						directconnections.checkStale();
						agent.getComponentFeature(IMessageFeature.class).sendReply(header, new Ack());
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
			agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if (checkReconnect())
					{
						reconnect();
					}
					else
					{
						IMessageFeature msgfeat = agent.getComponentFeature(IMessageFeature.class);
						for (final IComponentIdentifier con : keepaliveconnections)
						{
							msgfeat.sendMessageAndWait(con, new Ping()).addResultListener(new IResultListener<Object>()
							{
								public void resultAvailable(Object result)
								{
								}
								
								public void exceptionOccurred(Exception exception)
								{
									if (checkReconnect())
									{
										keepaliveconnections.remove(con);
										reconnect();
									}
								}
							});
						}
					}
					
					return IFuture.DONE;
				}
			});
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  (Re-)connect to a keep-alive set.
	 * 
	 */
	protected IFuture<Void> reconnect()
	{
		final Future<Void> ret = new Future<Void>();
		agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IMessageFeature msgfeat = ia.getComponentFeature(IMessageFeature.class);
				for (final IComponentIdentifier id : relays)
				{
					msgfeat.sendMessageAndWait(id, new Ping()).addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							if (!checkReconnect())
								keepaliveconnections.add(id);
						}
						
						public void exceptionOccurred(Exception exception)
						{
						}
					});
				}
				return IFuture.DONE;
			}
		});
		return ret;
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
		if (agent.getComponentIdentifier().equals(sender))
		{
			ret.setException(new IllegalArgumentException("Cannot transport relay messages using relay."));
		}
		else
		{
			agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IComponentIdentifier destination = (IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER);
					discoverRoute(destination, new LinkedHashSet<IComponentIdentifier>()).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
					{
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(exception);
						}
						
						public void resultAvailable(Collection<Integer> result)
						{
						}
						
						public void intermediateResultAvailable(Integer result)
						{
							ret.setResult(PRIORITY);
						}
						
						public void finished()
						{
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
			ISerializationServices serserv = (ISerializationServices) PlatformConfiguration.getPlatformValue(agent.getComponentIdentifier().getRoot(), IStarterConfiguration.DATA_SERIALIZATIONSERVICES);
			byte[] bheader = serserv.encode(header, agent, header);
			
			newbody = SUtil.mergeData(bheader, body);
			
			newheader = new MsgHeader();
			newheader.addProperty(FORWARD_DEST, header.getProperty(IMsgHeader.RECEIVER));
		}
		
		IInternalMessageFeature intmsgfeat = agent.getComponentFeature(IInternalMessageFeature.class);
		if (directconnections.contains(newheader.getProperty(FORWARD_DEST)))
		{
			List<byte[]> unpacked = SUtil.splitData(newbody);
			byte[] bheader = unpacked.get(0);
			ISerializationServices serserv = (ISerializationServices) PlatformConfiguration.getPlatformValue(agent.getComponentIdentifier().getRoot(), IStarterConfiguration.DATA_SERIALIZATIONSERVICES);
			newheader = (IMsgHeader) serserv.decode(null, agent, bheader);
			newheader.addProperty(FORWARD_DEST, newheader.getProperty(IMsgHeader.RECEIVER));
			newbody = unpacked.get(1);
			
			return intmsgfeat.sendToTransports(newheader, newbody);
		}
		
		newheader.addProperty(IMsgHeader.SENDER, agent.getComponentIdentifier());
		
		Tuple2<IComponentIdentifier, Integer> route = routes.get(destination);
		if (route != null)
		{
			newheader.addProperty(IMsgHeader.RECEIVER, route.getFirstEntity());
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
				Tuple2<IComponentIdentifier, Integer> route = routes.get(destination);
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
	public IIntermediateFuture<Integer> discoverRoute(final IComponentIdentifier destination, LinkedHashSet<IComponentIdentifier> hops)
	{
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		
		if (hops.contains(agent.getComponentIdentifier()) || hops.size() + 1 > maxhops)
		{
			ret.setException(new IllegalStateException("Loop detected or TTL exceeded."));
			ret.setFinished();
			return ret;
		}
		
		if (directconnections.contains(destination))
		{
			ret.addIntermediateResult(0);
			ret.setFinished();
			return ret;
		}
		
		Tuple2<IComponentIdentifier, Integer> route = routes.get(destination);
		if (route != null)
		{
			ret.addIntermediateResult(0);
			ret.setFinished();
			return ret;
		}
		
		final LinkedHashSet<IComponentIdentifier> newhops = new LinkedHashSet<IComponentIdentifier>(hops);
		newhops.add(agent.getComponentIdentifier());
		agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				FutureBarrier<IRoutingService> bar = new FutureBarrier<IRoutingService>();
				
				if (relays == null || relays.size() == 0)
				{
					ret.setException(new IllegalStateException("No route to receiver found."));
					return IFuture.DONE;
				}
				
				for (IComponentIdentifier relayid : relays)
					bar.addFuture(SServiceProvider.getService(agent, relayid, IRoutingService.class));
				Collection<IRoutingService> routingservices = bar.waitForResultsIgnoreFailures(null).get();
				
				if (routingservices == null || routingservices.size() == 0)
				{
					ret.setException(new IllegalStateException("No route to receiver found."));
					return IFuture.DONE;
				}
				
				final int[] count = new int[1];
				count[0] = routingservices.size();
				for (IRoutingService routingsrv : routingservices)
				{
					final IComponentIdentifier routetarget = ((IService) routingsrv).getServiceIdentifier().getProviderId();
					routingsrv.discoverRoute(destination, newhops).addIntermediateResultListener(new IIntermediateResultListener<Integer>()
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
							Tuple2<IComponentIdentifier, Integer> route = routes.get(destination);
							if (route == null || route.getSecondEntity() > result)
								routes.put(destination, new Tuple2<IComponentIdentifier, Integer>(routetarget, result));
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
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Checks if the number of keepalive connections is incorrect.
	 *  
	 *  @return True, if incorrect
	 */
	protected boolean checkReconnect()
	{
		return keepaliveconnections.size() < keepalivecount || (keepalivecount < 0 && keepaliveconnections.size() < relays.size());
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
