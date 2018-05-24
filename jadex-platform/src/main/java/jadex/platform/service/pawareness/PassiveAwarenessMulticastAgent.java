package jadex.platform.service.pawareness;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Collection;
import java.util.List;

import jadex.binary.SBinarySerializer;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;

/**
 *  Implements passive awareness via multicast.
 */
@Agent
public class PassiveAwarenessMulticastAgent	extends PassiveAwarenessBaseAgent
{
	//-------- attributes --------
	
	/** The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255). */
	// Long name to avoid name clash with platform settings -adress(service) true/false
	protected String	multicastaddress	= "232.0.9.1";
	
	/** The receiver port. */
	@AgentArgument
	protected int port	= 32091;
	
	/** The socket to send. */
	protected DatagramSocket sendsocket;
	
	/** The socket to receive. */
	protected MulticastSocket recvsocket;
	
	//-------- agent lifecycle --------

	/**
	 *  At startup create a multicast socket for listening.
	 */
	public void	start() throws Exception
	{
		sendsocket	= new DatagramSocket(0);
		recvsocket = new MulticastSocket(port);
		recvsocket.joinGroup(InetAddress.getByName(multicastaddress));
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
		IDaemonThreadPoolService	dtps	= SServiceProvider.getLocalService(agent, IDaemonThreadPoolService.class);
		dtps.executeForever(new Receiver(recvsocket));
		// Also listen for single-cast response messages -> TODO: use NIO to spare one thread
		dtps.executeForever(new Receiver(sendsocket));

		super.start();
	}
	
	@Override
	public void shutdown()	throws Exception
	{
		super.shutdown();
		recvsocket.leaveGroup(InetAddress.getByName(multicastaddress));
		recvsocket.close();
		sendsocket.close();
	}
	
	//-------- methods --------
	
	/**
	 *  Send the info to other platforms.
	 *  @param source	If set, send only to source as provided in discovered().
	 */
	@Override
	protected void	doSendInfo(List<TransportAddress> addresses, Object source) throws Exception
	{
		byte[]	data	= SBinarySerializer.writeObjectToByteArray(addresses, agent.getClassLoader());
		DatagramPacket p = source !=null
			? new DatagramPacket(data, data.length, ((DatagramPacket)source).getAddress(), ((DatagramPacket)source).getPort())
			: new DatagramPacket(data, data.length, new InetSocketAddress(multicastaddress, port));
		sendsocket.send(p);
	}
	
	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<Collection<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid)
	{
		// TODO: Implement
		return new Future<Collection<TransportAddress>>(new UnsupportedOperationException("Unimplemented for multicast."));
	}
	
	//-------- helper classes --------
	
	/**
	 *  Code for receiver thread.
	 */
	class Receiver implements Runnable
	{
		DatagramSocket	socket;
		
		Receiver(DatagramSocket socket)
		{
			this.socket	= socket;
		}
		
		@Override
		public void run()
		{
			// todo: max ip datagram length (is there a better way to determine length?)
			byte[]	buffer = new byte[8192];
			
			while(true)
			{
				try
				{
					DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
					socket.receive(pack);
					InputStream	is	= new ByteArrayInputStream(buffer, 0, pack.getLength());
					@SuppressWarnings("unchecked")
					Collection<TransportAddress>	addresses	= (Collection<TransportAddress>)SBinarySerializer.readObjectFromStream(is, agent.getClassLoader());
					discovered(addresses, pack);
				}
				catch(Throwable e)
				{
					agent.getLogger().warning("Multicast awareness failed to read datagram: "+e);//SUtil.getExceptionStacktrace(e));
				}
			}
		}
	}
}
