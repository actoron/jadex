package jadex.base.service.awareness;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.ICommand;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;
import jadex.service.threadpool.IThreadPoolService;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 
 */
public class AwarenessAgent extends MicroAgent
{
	/** The multicast internet address. */
	protected InetAddress address;
	
	/** The receiver port. */
	protected int port;
	
	/** The socket. */
	protected DatagramSocket socket;
	
	/** The delay. */
	protected long delay;
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		try
		{
			this.address = InetAddress.getByName("224.0.0.0");
			this.port = 55667;
			this.socket =  new DatagramSocket();
			this.delay = 5000;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// Start the receiver thread.
		SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
		.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				SServiceProvider.getService(getServiceProvider(), IThreadPoolService.class)
					.addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IThreadPoolService tp = (IThreadPoolService)result;
						tp.execute(new Runnable()
						{
							public void run()
							{
								// todo: max ip datagram length (is there a better way to determine length?)
								byte buf[] = new byte[65535];
								while(true)
								{
									try
									{
										MulticastSocket s = new MulticastSocket(port);
										s.joinGroup(address);
										
										DatagramPacket pack = new DatagramPacket(buf, buf.length);
										s.receive(pack);
										
										byte[] target = new byte[pack.getLength()];
										System.arraycopy(buf, 0, target, 0, pack.getLength());
										
										AwarenessInfo info = (AwarenessInfo)JavaReader.objectFromByteArray(target, ls.getClassLoader());
										System.out.println(getComponentIdentifier()+" received: "+info);
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
							}
						});
					}
			
					public void exceptionOccurred(Object source, Exception exception)
					{
						exception.printStackTrace();
					}
				}));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		}));
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		IExternalAccess root = getParent();
		while(root.getParent()!=null)
			root = root.getParent();
		final IComponentIdentifier cid = root.getComponentIdentifier();
		
		ICommand send = new ICommand()
		{
			public void execute(Object args)
			{
				send(address, port, new AwarenessInfo(cid));
				waitFor(delay, this);
			}
		};
		send.execute(this);
	}
	
	/**
	 * Start sending of message
	 */
	public void send(final InetAddress receiver, final int port, final AwarenessInfo info)
	{
		SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				try
				{
					ILibraryService ls = (ILibraryService)result;
					byte[] data = JavaWriter.objectToByteArray(info, ls.getClassLoader());
					DatagramPacket packet = new DatagramPacket(data, data.length, receiver, port);
					socket.send(packet);
					System.out.println(getComponentIdentifier()+" sent '"+info+"' to "+receiver+":"+port);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		}));
	}

}
