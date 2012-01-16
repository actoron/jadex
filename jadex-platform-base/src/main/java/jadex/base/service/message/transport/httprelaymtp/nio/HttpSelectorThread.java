package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  The receiver connects to the relay server
 *  and accepts messages.
 */
public class HttpSelectorThread
{
	//-------- attributes --------
	
	/** The NIO Selector. */
	protected Selector	selector;

	/** Tasks for the NIO thread. */
	protected List<Runnable>	tasks;

	//-------- constructors --------
	
	/**
	 *  Create and start a new receiver.
	 * @throws IOException 
	 */
	public HttpSelectorThread(IExternalAccess access, String address) throws IOException
	{
		// ANDROID: the following line causes an exception in a 2.2
		// emulator, see:
		// http://code.google.com/p/android/issues/detail?id=9431
		// try this:
//		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
//		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		
		// Causes problem with maven too (only with Win firewall?)
		// http://www.thatsjava.com/java-core-apis/28232/
		selector	= Selector.open();
		tasks	= new ArrayList<Runnable>();
		
		new Thread(new Runnable()
		{
			public void run()
			{
//				System.out.println("starting selector thread");
				while(selector.isOpen())
				{
//					System.out.println("running selector thread");
					try
					{
						Runnable[]	atasks	= null;
						synchronized(tasks)
						{
							if(!tasks.isEmpty())
							{
								atasks	= tasks.toArray(new Runnable[tasks.size()]);
								tasks.clear();
							}
						}
						for(int i=0; atasks!=null && i<atasks.length; i++)
						{
							try
							{
								atasks[i].run();
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						
						// Wait for an event one of the registered channels
//						System.out.println("selector idle");
						selector.select();

						// Iterate over the set of keys for which events are available
						Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
						while(selectedKeys.hasNext())
						{
							SelectionKey key = selectedKeys.next();
							selectedKeys.remove();

							if(key.isValid())
							{
//								if(key.isConnectable())
//								{
//									this.handleConnect(key);
//								}
//								else if(key.isWritable())
//								{
//									this.handleWrite(key);
//								}
//								else if(key.isReadable())
//								{
//									this.handleRead(key);
//								}
							}
							else
							{
								key.cancel();
							}
						}
					}
					catch(Exception e)
					{
						// Key may be cancelled just after isValid() has been tested.
//						e.printStackTrace();
					}
				}
//				System.out.println("leaving selector thread");
			}
		}).start();
	}
	
	//-------- methods --------
	
	/**
	 *  Stop the receiver.
	 */
	public void	stop()
	{
		try
		{
			selector.close();
		}
		catch(IOException e)
		{
			// Shouldn't happen!?
			e.printStackTrace();
		}
	}
	
	/**
	 *  Add a send task.
	 */
	public void addSendTask(ManagerSendTask task, Future<Void> fut)
	{
		Runnable	r	= new Runnable()
		{
			public void run()
			{
			}
		};
		synchronized(tasks)
		{
			tasks.add(r);
		}
	}
}
