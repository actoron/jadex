package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *  Test sending using new connections.
 */
public class NIOSendingBenchmark	extends AbstractRelayBenchmark
{
	//-------- constants --------
	
	/** The max number of open messages. */
	protected final int	MAX	= 2;
	
	//-------- attributes --------
	
	/** The open futures. */
	protected List<IFuture<Void>>	open;
	
	//-------- template methods --------
		
	/**
	 *  Init the time array and
	 *  open the connection.
	 */
	protected void setUp() throws Exception
	{
		open	= new LinkedList<IFuture<Void>>();
	}
	
	/**
	 *  Close the socket.
	 */
	protected void tearDown() throws Exception
	{
		while(!open.isEmpty())
		{
			open.remove(0).get(new ThreadSuspendable(), 20000);
		}
	}
	
	/**
	 *  Receive a message.
	 */
	protected void doSingleRun() throws Exception
	{
		// Prepare data.
		byte[]	id	= JavaWriter.objectToByteArray("benchmark", getClass().getClassLoader());
		byte[]	data	= new byte[SIZE];
		Random	rnd	= new Random();
		rnd.nextBytes(data);
		byte[]	msg	= new byte[4+id.length+4+data.length];
		System.arraycopy(SUtil.intToBytes(id.length), 0, msg, 0, 4);
		System.arraycopy(id, 0, msg, 4, id.length);
		System.arraycopy(SUtil.intToBytes(data.length), 0, msg, 4+id.length, 4);
		System.arraycopy(data, 0, msg,  4+id.length+4, data.length);
		
		open.add(sendMessage(msg));
		
		while(open.size()>MAX)
		{
			open.remove(0).get(new ThreadSuspendable(), 20000);			
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Asynchronoulsy send a message.
	 */
	protected IFuture<Void>	sendMessage(byte[] msg)
	{
		return IFuture.DONE;
	}
	
	//-------- NIO handler --------
	
	public class SelectorThread	implements Runnable
	{
		protected Selector	selector;
		
		public void run()
		{
			while(true)
			{
				try
				{
					// Wait for an event one of the registered channels
					selector.select();

					// Iterate over the set of keys for which events are available
					Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
					while(selectedKeys.hasNext())
					{
						SelectionKey key = selectedKeys.next();
						selectedKeys.remove();

						if(key.isValid())
						{
//							if(key.isAcceptable())
//							{
//								this.handleAccept(key);
//							}
							if(key.isReadable())
							{
								this.handleRead(key);
							}
							else if(key.isConnectable())
							{
								this.handleConnect(key);
							}
							else if(key.isWritable())
							{
								this.handleWrite(key);
							}
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
					e.printStackTrace();
				}
			}
		}
		
		//-------- handler methods --------

		/**
		 *  Establish a connection.
		 */
		protected void	handleConnect(SelectionKey key)
		{
			SocketChannel	sc	= (SocketChannel)key.channel();
			try
			{
				boolean	finished	= sc.finishConnect();
				assert finished;
				// Keep channel on hold until we are ready to write.
			    key.interestOps(0);
			}
			catch(Exception e)
			{ 
				e.printStackTrace();
				key.cancel();
			}
		}
		
		/**
		 *  Write data to a connection.
		 */
		protected void handleWrite(SelectionKey key)
		{
//			SocketChannel	sc	= (SocketChannel)key.channel();
//			NIOTCPOutputConnection	con	= (NIOTCPOutputConnection)key.attachment();
//			List	queue	= (List)this.writetasks.get(sc);
//
//			try
//			{
//				boolean	more	= true;
//				while(more)
//				{
//					if(queue.isEmpty())
//					{
//						more	= false;
//						// We wrote away all data, so we're no longer interested in
//						// writing on this socket.
//						key.interestOps(0);
//					}
//					else
//					{
//						Tuple	task	= (Tuple)queue.get(0);
//						List	buffers	= (List)task.get(0);	
//						Future	fut	= (Future)task.get(1);	
//						ByteBuffer buf = (ByteBuffer)buffers.get(0);
//						sc.write(buf);
//						if(buf.remaining()>0)
//						{
//							// Output buffer is full: stop sending for now.
//							more	= false;
//						}
//						else
//						{
//							// Buffer written: remove task and inform future, when no more buffers for this task.
//							buffers.remove(buf);
//							if(buffers.isEmpty())
//							{
//								queue.remove(task);
//								fut.setResult(null);
//							}
//						}
//					}
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				key.cancel();
//			}
		}
		
		/**
		 *  Read data from a connection.
		 */
		protected void	handleRead(SelectionKey key)
		{
//			try
//			{
//				ReadableByteChannel	rbc	= (ReadableByteChannel)key.channel();
//			}
//			catch(Exception e)
//			{ 
//				e.printStackTrace();
//				key.cancel();
//			}
		}
	}
}
