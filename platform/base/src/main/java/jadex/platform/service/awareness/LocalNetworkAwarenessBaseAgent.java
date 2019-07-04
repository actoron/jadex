package jadex.platform.service.awareness;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.binary.SBinarySerializer;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.awareness.IAwarenessService;
import jadex.bridge.service.types.registry.SlidingCuckooFilter;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;

/**
 *  Implements passive awareness via multicast.
 */
@Service
@Agent(autoprovide = Boolean3.TRUE,
	predecessors="jadex.platform.service.address.TransportAddressAgent",
	successors="jadex.platform.service.registry.SuperpeerClientAgent",
	autostart=Boolean3.FALSE
)
public abstract class LocalNetworkAwarenessBaseAgent	implements IAwarenessService
{
	//-------- agent arguments --------
	
	/** The address. */
	@AgentArgument
	protected String address;
	
	/** The receiver port. */
	@AgentArgument
	protected int port;
	
	/** The search delay (time that is waited for responses from platforms) as factor of default/service call timeout (default 0.333..., i.e. a third of default/service call timeout). */
	@AgentArgument
	protected double waitfactor	= 0.3333333333333333;
	
	//-------- attributes --------

	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	/** The current search, if any. */
	protected IntermediateFuture<IComponentIdentifier> search;
	
	/** The duplicate filter of the current search, if any. */
	protected SlidingCuckooFilter	filter;

	/** The currently known platforms. */
	protected Map<IComponentIdentifier, List<TransportAddress>>	platforms;

	/** The newly found platforms. */
	protected Map<IComponentIdentifier, List<TransportAddress>>	newplatforms;

	/** The socket to send. */
	protected DatagramSocket sendsocket;
	
	/** The socket to receive. */
	protected DatagramSocket recvsocket;
	
	/** Last time platforms where searched. */
//	protected long lastsearchplatform = 0;

	// -------- agent lifecycle --------

	/**
	 *  At startup create a multicast socket for listening.
	 */
	@ServiceStart
	public void	start() throws Exception
	{
		platforms = new LinkedHashMap<IComponentIdentifier, List<TransportAddress>>();
		
		// Does not receive messages on same host if disabled.
//		recvsocket.setLoopbackMode(true);
//		System.out.println(agent + " loopback is: "+recvsocket.getLoopbackMode());
//		InetSocketAddress	isa	= new InetSocketAddress(multicastaddress, port);
//		for(Enumeration<NetworkInterface> nis=NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); )
//		{
//			NetworkInterface	ni	= nis.nextElement();
//			try
//			{
//				recvsocket.joinGroup(isa, ni);
//				System.out.println(agent + " joined: " + ni+", "+ni.getInterfaceAddresses());
//			}
//			catch(Exception e)
//			{
////				System.out.println("Cannot bind " + ni + " to multicast address: "+e);
//			}
//		}

		// Start listening thread.
		IDaemonThreadPoolService dtps = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IDaemonThreadPoolService.class));
		dtps.executeForever(new Receiver(recvsocket, true));
		// Also listen for single-cast response messages -> TODO: use NIO to spare one thread
		dtps.executeForever(new Receiver(sendsocket, false));

		// Send own info initially.
		sendInfo(address, port);

		// TODO: send info on address changes?
	}
	
//	sendsocket	= new DatagramSocket(0);
//	recvsocket = new MulticastSocket(port);
//	recvsocket.joinGroup(InetAddress.getByName(address));

	/**
	 * Stop the service.
	 */
	@ServiceShutdown
	public void shutdown() throws Exception
	{
//		recvsocket.leaveGroup(InetAddress.getByName(multicastaddress));
		recvsocket.close();
		sendsocket.close();
	}

	// -------- IAwarenessService --------

	/**
	 * Try to find other platforms and finish after timeout. Immediately returns
	 * known platforms and concurrently issues a new search, waiting for replies
	 * until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier> searchPlatforms()
	{
//		lastsearchplatform = System.currentTimeMillis();
		if(search == null)
		{
			long timeout = ServiceCall.getCurrentInvocation()!=null ? ServiceCall.getCurrentInvocation().getTimeout() : 0;
			
//			System.out.println("New search");
			search	= new IntermediateFuture<IComponentIdentifier>();
			newplatforms	= new LinkedHashMap<>();	// always remember platforms from last search
			filter	= new SlidingCuckooFilter(); 

			// Add initial results
			for(IComponentIdentifier platform : platforms.keySet())
			{
				if(!filter.contains(platform.toString()))
				{
					filter.insert(platform.toString());
					search.addIntermediateResult(platform);
				}
			}
			// issue search request to trigger replies from platforms
			sendInfo(address, port).addResultListener(new IResultListener<Void>()
			{
				@Override
				public void resultAvailable(Void result)
				{
					// Search for other platforms
					long delay = timeout>0 ? (long)(timeout*waitfactor) : Starter.getScaledDefaultTimeout(agent.getId(), waitfactor);
					if(delay==-1)
						delay = (long)(SUtil.DEFTIMEOUT_DEFAULT*waitfactor);
					//System.out.println("delay is: "+delay);
					agent.getFeature(IExecutionFeature.class)
						.waitForDelay(delay, true)
						.addResultListener(new IResultListener<Void>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							done();
						}

						@Override
						public void resultAvailable(Void result)
						{
							done();
						}
					});
				}

				@Override
				public void exceptionOccurred(Exception exception)
				{
					done();
				}
				
				private void	done()
				{
					// null first, set later as it might trigger new search
					IntermediateFuture<IComponentIdentifier>	fut	= search;
					search = null;
					filter	= null;
					
					// Forget older platforms not found again.
					platforms	= newplatforms;
					newplatforms	= null;
					
					fut.setFinishedIfUndone();
				}
			});
		}
//		else
//		{
//			System.out.println("old search");
//		}

		return search;
	}
	
	/**
	 *  Try to find other platforms while providing a quick answer.
	 *  Services should respond to a call as close to instantaneous as possible, but
	 *  with an upper bound of less than 1 second.
	 *  Issues a new search, but answers using known platforms. On first request
	 */
//	public IFuture<Set<IComponentIdentifier>> searchPlatformsFast()
//	{
//		Future<Set<IComponentIdentifier>> ret = new Future<Set<IComponentIdentifier>>();
//		if (System.currentTimeMillis() - lastsearchplatform > 60000)
//		{
//			System.out.println("Never searched, do search: " + lastsearchplatform);
//			searchPlatforms().addResultListener(new IResultListener<Collection<IComponentIdentifier>>()
//			{
//				public void resultAvailable(Collection<IComponentIdentifier> result)
//				{
//					ret.setResult(new HashSet<IComponentIdentifier>(result));
//				};
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					ret.setResult(Collections.emptySet());
//				}
//			});
//		}
//		else
//		{
//			System.out.println("Fast mode on: " + lastsearchplatform);
//			// Trigger search, but answer immediately.
//			searchPlatforms();
//			ret.setResult(new HashSet<IComponentIdentifier>(platforms.keySet()));
//		}
//		return ret;
//	}

	// -------- template methods --------

	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<List<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid)
	{
		return new Future<List<TransportAddress>>(platforms.containsKey(platformid)
			? new ArrayList<TransportAddress>(platforms.get(platformid)) : Collections.emptyList());
	}

	// -------- helper methods --------

	/**
	 * Send address info to a given multi or unicast address.
	 */
	protected IFuture<Void> sendInfo(final String address, final int port)
	{
		final Future<Void> ret = new Future<Void>();
		ITransportAddressService tas = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ITransportAddressService.class));
		tas.getAddresses().addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, Void>(ret)
		{
			@Override
			public void customResultAvailable(List<TransportAddress> addresses) throws Exception
			{
				agent.getLogger().info("sending: "+addresses);
//				System.out.println("sending: "+addresses);
				byte[]	data	= SBinarySerializer.writeObjectToByteArray(addresses, agent.getClassLoader());
				DatagramPacket p = new DatagramPacket(data, data.length, new InetSocketAddress(address, port));
				sendsocket.send(p);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	//-------- helper classes --------
	
	/**
	 *  Code for receiver thread.
	 */
	class Receiver implements Runnable
	{
		DatagramSocket	socket;
		boolean	reply;
		
		Receiver(DatagramSocket socket, boolean reply)
		{
			this.socket	= socket;
			this.reply	= reply;
		}
		
		@Override
		public void run()
		{
			// todo: max ip datagram length (is there a better way to determine length?)
			byte[]	buffer = new byte[8192];
			
			while(!socket.isClosed())
			{
				try
				{
					DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
					socket.receive(pack);
					InputStream	is	= new ByteArrayInputStream(buffer, 0, pack.getLength());
					@SuppressWarnings("unchecked")
					List<TransportAddress>	addresses	= (List<TransportAddress>)SBinarySerializer.readObjectFromStream(is, agent.getClassLoader());
					
//					System.out.println(agent +" receiving: "+addresses);
					
					// Ignore my own addresses.
					// TODO: what if data source and platform(s) of addresses differ (e.g. no point-to-point awareness)
					if(addresses!=null && !addresses.isEmpty())
					{
						IComponentIdentifier	sender	= addresses.iterator().next().getPlatformId();
						if(!agent.getId().getRoot().equals(sender))
						{
							agent.getLogger().info("discovered: " + addresses);
							agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
							{
								@Override
								public IFuture<Void> execute(IInternalAccess ia)
								{
									platforms.put(sender, addresses);
									if(search!=null && !filter.contains(sender.toString()))
									{
										filter.insert(sender.toString());
										newplatforms.put(sender, addresses);	// only add when search is running
										search.addIntermediateResultIfUndone(sender);
									}
									
									if(reply)
									{
										sendInfo(pack.getAddress().getHostAddress(), pack.getPort());
									}
									return IFuture.DONE;
								}
							});
						}
					}
				}
				catch(Throwable e)
				{
//					System.out.println(agent +" failed to read datagram: "+e+", "+this);
					agent.getLogger().warning("Awareness failed to read datagram: "+e);//SUtil.getExceptionStacktrace(e));
				}
			}
		}
	}
}
