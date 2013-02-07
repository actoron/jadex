package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  Task of the thread handling sending packets.
 *
 */
public class SendingThreadTask implements Runnable
{
	/** The socket. */
	protected DatagramSocket socket;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<ITxTask> txqueue;
	
	/** The thread pool. */
	protected IDaemonThreadPoolService threadpool;
	
	/**
	 *  Creates the task.
	 *  
	 *  @param socket The socket.
	 *  @param txqueue The transmission queue.
	 *  @param threadpool The thread pool.
	 */
	public SendingThreadTask(DatagramSocket socket, PriorityBlockingQueue<ITxTask> txqueue, IDaemonThreadPoolService threadpool)
	{
		this.socket = socket;
		this.txqueue = txqueue;
		this.threadpool = threadpool;
	}
	
	public void run()
	{
		boolean running = true;
		while (running)
		{
			ITxTask task = null;
			try
			{
				 task = txqueue.take();
			}
			catch (InterruptedException e)
			{
			}
			
			if (task instanceof TxShutdownTask)
			{
				running = false;
			}
			
			if (task != null && running)
			{
				byte[][] packets = task.getPackets();
				
				InetSocketAddress resreceiver = task.getResolvedReceiver();
												
				short[] txids = task.getTxPacketIds();
				boolean keepsending = true;
				if (txids == null)
				{
					for (int i = 0; keepsending && i < packets.length; ++i)
					{
//						System.out.println("Sending packet: " + i);
						byte[] packet = packets[i];
						keepsending = sendPacket(resreceiver, task, packet);
					}
				}
				else
				{
					for (int i = 0; keepsending && i < txids.length; ++i)
					{
						byte[] packet = packets[txids[i]];
						keepsending = sendPacket(resreceiver, task, packet);
					}
				}
			}
		}
	}
	
	protected boolean sendPacket(InetSocketAddress resreceiver, final ITxTask task, byte[] packet)
	{
		boolean ret = true;
		DatagramPacket dgp = new DatagramPacket(packet, packet.length, resreceiver.getAddress(), resreceiver.getPort());
		try
		{
//			System.out.println("Sending to: " + resreceiver.getAddress() + ":" + resreceiver.getPort() + " Length: " + packet.length);
			socket.send(dgp);
		}
		catch (final IOException e)
		{
			threadpool.execute(new Runnable()
			{
				public void run()
				{
					task.transmissionFailed("IO Error sending message: " + e.getMessage());
				}
			});
			ret = false;
		}
		return ret;
	}
}
