package org.activecomponents.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.activecomponents.udp.asymciphers.KeyExchangeGenerator;

/**
 *  Connection handler for UDP connections.
 *
 */
public class UdpConnectionHandler
{
	/** Cipher class ID for no encryption */
	public static final int NULL_CIPHER = 0;
	
	/** Cipher class ID for default encryption */
	public static final int DEFAULT_CIPHER = 1;
	
	/** The thread executor. */
	protected IThreadExecutor texec;
	
	/** Provider for randomly generated key exchanges. */
	protected KeyExchangeGenerator keyexgen;
	
	/** List of externally-provided key verifiers. */
	protected List<IKeyVerifier> keyverifiers;
	
	/** The socket. */
	protected DatagramSocket dgsocket;
	
	/** Current connections with remote hosts. */
	protected Map<SocketAddress, Connection> connections;
	
	/** Flag if the handler is active. */
	protected volatile boolean active;
	
	/** ID of the class used as cipher for symmetric encryption. */
	protected int symcipherclassid;
	
	/** Listeners for new connections. */
	protected List<IConnectionListener> connectionlisteners;
	
	/** Listeners for incoming messages and packets. */
	protected List<IIncomingListener> inclisteners;
	
	/** Internal listener for incoming messages and packets. */
	protected IIncomingListener internalinclistener;
	
	/**
	 *  Creates the connection handler.
	 *  
	 *  @param port The port.
	 *  @param inisocket Pre-initialized socket, overrides port argument, can be null.
	 *  @param symcipherclass Class used for symmetric encryption.
	 *  @throws SocketException Socket exception.
	 */
	public UdpConnectionHandler(int port, int symcipherclassid, boolean nonblocking, IIncomingListener[] initiallisteners)
	{
		this(port, null, symcipherclassid, nonblocking, initiallisteners);
	}
	
	/**
	 *  Creates the connection handler.
	 *  
	 *  @param port The port.
	 *  @param inisocket Pre-initialized socket, overrides port argument, can be null.
	 *  @param symcipherclass Class used for symmetric encryption.
	 *  @throws SocketException Socket exception.
	 */
	public UdpConnectionHandler(DatagramSocket inisocket, int symcipherclassid, boolean nonblocking, IIncomingListener[] initiallisteners)
	{
		this(-1, inisocket, symcipherclassid, nonblocking, initiallisteners);
	}
	
	/**
	 *  Creates the connection handler.
	 *  
	 *  @param port The port.
	 *  @param inisocket Pre-initialized socket, overrides port argument, can be null.
	 *  @param symcipherclass Class used for symmetric encryption.
	 *  @throws SocketException Socket exception.
	 */
	protected UdpConnectionHandler(int port, DatagramSocket inisocket, int symcipherclassid, boolean nonblocking, IIncomingListener[] initiallisteners)
	{
		dgsocket = inisocket;
		if (nonblocking)
		{
			inclisteners = Collections.synchronizedList(new ArrayList<IIncomingListener>());
			if (initiallisteners != null)
			{
				for (IIncomingListener l : initiallisteners)
				{
					inclisteners.add(l);
				}
			}
			internalinclistener = new IIncomingListener()
			{
				public void receivePacket(SocketAddress remoteaddress, byte[] data)
				{
					synchronized (inclisteners)
					{
						for (IIncomingListener l : inclisteners)
						{
							l.receivePacket(remoteaddress, data);
						}
					}
				}
				
				public void receiveMessage(SocketAddress remoteaddress, byte[] data)
				{
//					System.out.println("MSG MSG " +data);
					synchronized (inclisteners)
					{
						for (IIncomingListener l : inclisteners)
						{
							l.receiveMessage(remoteaddress, data);
						}
					}
				}
			};
		}
		this.symcipherclassid = symcipherclassid;
		while (dgsocket == null)
		{
			InetSocketAddress isa = new InetSocketAddress(port);
			try
			{
				dgsocket = new DatagramSocket(isa);
			}
			catch (BindException e)
			{
				++port;
			}
			catch(SocketException e)
			{
				throw new RuntimeException(e);
			}
		}
		keyexgen = new KeyExchangeGenerator("com.actoron.udp.asymciphers.ECDHExchange");
		
		// Enable to start pre-generation mode.
//		keyexgen.start(texec);
		
		keyverifiers = new ArrayList<IKeyVerifier>();
		keyverifiers.add(new NullKeyVerifier());
		active = false;
		connections = Collections.synchronizedMap(new HashMap<SocketAddress, Connection>());
		connectionlisteners = Collections.synchronizedList(new ArrayList<IConnectionListener>());
		connectionlisteners.add(new IConnectionListener()
		{
			public void peerDisconnected(Connection connection)
			{
				connections.remove(connection.getRemoteAddress());
			}
			
			public void peerConnected(Connection connection)
			{
			}
		});
	}
	
	/**
	 *  Starts the handling of incoming packages.
	 */
	public synchronized void start(IThreadExecutor texec)
	{
		this.texec = texec;
		if (active)
		{
			throw new IllegalStateException("Receiver already active.");
		}
		
		active = true;
		
		Runnable run = new Runnable()
		{
			public void run()
			{
				byte[] buf = new byte[65536];
				DatagramPacket dgp = new DatagramPacket(buf, buf.length);
				
				while(active)
				{
					try
					{
						buf[0] = -1;
						dgsocket.receive(dgp);
						int len = dgp.getLength();
//						System.out.println("PACKET " + len + " " + (len > 0? buf[0] : "fail"));
						
						Connection connection = connections.get(dgp.getSocketAddress());
//						System.out.println("PACKET Conn " + connection + " " + dgp.getSocketAddress());
						if (connection != null)
						{
							if (len > 0)
							{
								byte[] packet = new byte[len];
								System.arraycopy(buf, 0, packet, 0, packet.length);
								connection.rawPacketReceived(packet);
							}
						}
						else if (len > 1 && buf[0] == Connection.SYN)
						{
							final byte[] packet = new byte[len];
							System.arraycopy(buf, 0, packet, 0, packet.length);
							final Connection conn = new Connection(dgsocket, connectionlisteners, dgp.getSocketAddress(), UdpConnectionHandler.this.texec, keyexgen, symcipherclassid, keyverifiers, internalinclistener);
							synchronized (connections)
							{
								final SocketAddress saddr = dgp.getSocketAddress();
								if (!connections.containsKey(saddr));
								{
									connections.put(saddr, conn);
									UdpConnectionHandler.this.texec.run(new Runnable()
									{
										public void run()
										{
											conn.respond(packet, UdpConnectionHandler.this);
//											connections.remove(saddr);
										}
									});
								}
							}
						}
					}
					catch (IOException e)
					{
						dgsocket.disconnect();
						active = false;
					}
				}
				
//				try
//				{
//					dgsocket = new DatagramSocket(dgsocket.getLocalSocketAddress());
//				}
//				catch (SocketException e1)
//				{
//					e1.printStackTrace();
//				}
			}
		};
		texec.run(run);
	}
	
	/**
	 *  Stops the handling of incoming packages.
	 */
	public synchronized void stop()
	{
		active = false;
	}
	
	/**
	 *  Test.
	 */
	public static void main(String[] args) throws Exception
	{
		UdpConnectionHandler sh1 = new UdpConnectionHandler(5555, 1, false, null);
		sh1.start(new DaemonThreadExecutor());
		UdpConnectionHandler sh2 = new UdpConnectionHandler(6666, 1, false, null);
		sh2.start(new DaemonThreadExecutor());
		
		Connection c1 = sh1.connect("127.0.0.1", 6666);
		Connection c2 = sh2.getConnection("127.0.0.1", 5555);
		
		String string = "Hello";
		for (int i = 0; i < 15; ++i)
			string += string;
		System.out.println("Sending " + string.length() + " " + string);
		Charset utf8 = null;
		utf8 = Charset.forName("UTF-8");
//		c1.sendPacket(string.getBytes(utf8));
		c1.sendMessage(string.getBytes(utf8));
		
		String tstr = new String(c2.receive(), utf8);
		System.out.println("" + tstr.length() + " " + tstr);
		
		byte[] big = new byte[104857600];
		Random rand = new Random();
		rand.nextBytes(big);
		long ts = System.currentTimeMillis();
		c1.sendMessage(big);
		byte[] rbig = c2.receive();
		ts = System.currentTimeMillis() - ts;
		while(rbig != null)
		{
			System.out.println(ts);
			System.out.println(Arrays.equals(big, rbig));
			rbig = c2.receive(10000);
		}
		
	}
	
	/**
	 *  Attempts to connect to a host.
	 * 
	 *  @param host Remote host.
	 *  @param port Remote port.
	 *  @return The connection.
	 */
	public Connection connect(String host, int port)
	{
		InetSocketAddress isa = new InetSocketAddress(host, port);
		final Connection ret = new Connection(dgsocket, connectionlisteners, isa, texec, keyexgen, symcipherclassid, keyverifiers, internalinclistener);
		
		synchronized (connections)
		{
			Connection tmp = connections.get(isa);
			if (tmp != null)
			{
				if (tmp.isConnected())
					return tmp;
				
				// Connection available but not ready? Wait for it?
				return null;
			}
			connections.put(isa, ret);
		}
		
		ret.initiate(UdpConnectionHandler.this);
		
		if (ret.isConnected())
		{
			texec.run(new Runnable()
			{
				public void run()
				{
					ret.sendLoop();
				}
			});
			return ret;
		}
		connections.remove(isa);
		return null;
	}
	
	/**
	 *  Terminates a connection.
	 *  @param conn The connection.
	 */
	public void disconnect(Connection conn)
	{
		conn.disconnect();
		connections.remove(conn.getRemoteAddress());
	}
	
	/**
	 *  Gets the local port.
	 * 
	 *  @return The local port.
	 */
	public int getPort()
	{
		return dgsocket.getLocalPort();
	}
	
	/**
	 *  Gets the connection if available.
	 *  
	 * 	@param host The remote host.
	 *  @param port The remote port.
	 * 	@return The connection, null if unavailable.
	 */
	public Connection getConnection(String host, int port)
	{
		InetSocketAddress isa = new InetSocketAddress(host, port);
		return getConnection(isa);
		
	}
	/**
	 *  Gets the connection if available.
	 * 
	 * @param remoteaddr The remote address.
	 * @return The connection, null if unavailable.
	 */
	public Connection getConnection(SocketAddress remoteaddr)
	{
		Connection ret = connections.get(remoteaddr);
		if (ret != null && ret.isConnected())
			return ret;
		return null;
	}
	
	/**
	 *  Attempts to punch a hole in the local firewall for the remote host.
	 *  
	 *  @param remotehost The remote host.
	 *  @param remoteport The port the remote host is using.
	 */
	public void holePunch(String remotehost, int remoteport)
	{
		byte[] empty = new byte[0];
		InetSocketAddress remoteaddr = new InetSocketAddress(remotehost, remoteport);
		try
		{
			DatagramPacket dgp = new DatagramPacket(empty, 0, remoteaddr);
			for (int i = 0; i < 3; ++i)
			{
				dgsocket.send(dgp);
			}
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 *  Adds a connection listener.
	 * 
	 *  @param listener The listener.
	 */
	public void addConnectionListener(IConnectionListener listener)
	{
		connectionlisteners.add(listener);
	}
	
	/**
	 *  Removes a connection listener.
	 * 
	 *  @param listener The listener.
	 */
	public void removeConnectionListener(IConnectionListener listener)
	{
		connectionlisteners.remove(listener);
	}
	
	/**
	 *  Returns a cipher class ID for a class name.
	 *  @param symcipherclass The class name.
	 *  @return The ID.
	 */
	public static final int getCipherIdForClass(String symcipherclass)
	{
		int cipherclassid = Integer.MIN_VALUE;
		for (int i = 0; i < SymCipherManagement.CIPHERS.length; ++i)
		{
			if (SymCipherManagement.CIPHERS[i].equals(symcipherclass))
			{
				cipherclassid = i;
				break;
			}
		}
		if (cipherclassid == Integer.MIN_VALUE)
		{
			throw new RuntimeException("Cipher ID not found for class: " + symcipherclass);
		}
		return cipherclassid;
	}
	
	/**
	 *  Notifies connection listeners about a new connection.
	 *  
	 *  @param connection The connection.
	 */
	protected void fireConnectionEstablished(Connection connection)
	{
		synchronized (connectionlisteners)
		{
			for (IConnectionListener listener : connectionlisteners)
			{
				listener.peerConnected(connection);
			}
		}
	}
}
