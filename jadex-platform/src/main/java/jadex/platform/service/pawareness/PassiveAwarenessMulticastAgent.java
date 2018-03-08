package jadex.platform.service.pawareness;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.binary.SBinarySerializer;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;

/**
 *  Implements the passive awareness system service (management).
 */
@Agent
public class PassiveAwarenessMulticastAgent	extends PassiveAwarenessBaseAgent
{
	//-------- attributes --------
	
	/** The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255). */
	// Long name to avoid name clash with platform settings -adress(service) true/false
	protected String	multicastaddress	= "239.1.2.3";
	
	/** The receiver port. */
	@AgentArgument
	protected int port	= 55667;
	
	/** The socket to send/receive. */
	protected MulticastSocket socket;
	
	//-------- agent lifecycle --------

	/**
	 *  At startup create a multicast socket for listening.
	 */
	public void	start() throws Exception
	{
		socket = new MulticastSocket(port);
		// Does not receive messages on same host if disabled.
		socket.setLoopbackMode(true);
		socket.joinGroup(InetAddress.getByName(multicastaddress));
		
		System.out.println(agent + " loopback is: "+socket.getLoopbackMode());
		InetSocketAddress	isa	= new InetSocketAddress(multicastaddress, port);
		for(Enumeration<NetworkInterface> nis=NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); )
		{
			NetworkInterface	ni	= nis.nextElement();
			try
			{
				socket.joinGroup(isa, ni);
				System.out.println(agent + " joined: " + ni+", "+ni.getInterfaceAddresses());
			}
			catch(Exception e)
			{
//				System.out.println("Cannot bind " + ni + " to multicast address: "+e);
			}
		}

		// Send own info initially.
		sendInfo();
		
		// TODO: send info on address changes?
		
		// Start listening thread.
		IDaemonThreadPoolService	dtps	= SServiceProvider.getLocalService(agent, IDaemonThreadPoolService.class);
		dtps.executeForever(new Runnable()
		{
			@Override
			public void run()
			{
				// todo: max ip datagram length (is there a better way to determine length?)
				byte[]	buffer = new byte[8192];
				
				while(true)
				{
					try
					{
						final DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
						socket.receive(pack);
						InputStream	is	= new ByteArrayInputStream(buffer, 0, pack.getLength());
						@SuppressWarnings("unchecked")
						Collection<TransportAddress>	addresses	= (Collection<TransportAddress>)SBinarySerializer.readObjectFromStream(is, agent.getClassLoader());
						System.out.println(agent + " received: "+addresses);
					}
					catch(Throwable e)
					{
						System.err.println("Multicast awareness failed to read datagram: "+SUtil.getExceptionStacktrace(e));
					}
				}
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Send address info to listening platforms.
	 */
	protected IFuture<Void>	sendInfo()
	{
		Future<Void>	ret	= new Future<Void>();
		ITransportAddressService tas = SServiceProvider.getLocalService(agent, ITransportAddressService.class);
		tas.getAddresses().addResultListener(new ExceptionDelegationResultListener<List<TransportAddress>, Void>(ret)
		{
			@Override
			public void customResultAvailable(List<TransportAddress> addresses)
			{
				try
				{
					System.out.println(agent+" sending: "+addresses);
					byte[]	data	= SBinarySerializer.writeObjectToByteArray(addresses, agent.getClassLoader());
					DatagramPacket p = new DatagramPacket(data, data.length, new InetSocketAddress(multicastaddress, port));
					socket.send(p);
				}
				catch(Exception e)
				{
					SUtil.throwUnchecked(e);
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				// shouldn't happen?
			}
		});
		return ret;
	}

	public static void main(String[] args)
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getDefault();
		config.addComponent(PassiveAwarenessMulticastAgent.class);
		config.setAwareness(false);	// old awareness
		Starter.createPlatform(config, args).get();
	}
}
