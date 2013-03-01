package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Task of the thread handling sending packets.
 *
 */
public class SendingThreadTask implements Runnable
{
	/** The running state. */
	protected String RUNNING_STATE = "Running";
	
	/** The idle state. */
	protected String IDLE_STATE = "Idle";
	
	/** The wait state. */
	protected String WAIT_STATE = "Waiting";
	
	/** The exited state. */
	protected String EXITED_STATE = "Exited";
	
	
	/** The socket. */
	protected DatagramSocket socket;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	/** The remaining send quota. */
	protected AtomicInteger sendquota;
	
	/** The state of the thread. */
	protected volatile String state;
	
//	public static Set<Integer> schedmsg = Collections.synchronizedSet(new HashSet<Integer>());
	
	/**
	 *  Creates the task.
	 *  
	 *  @param socket The socket.
	 *  @param txqueue The transmission queue.
	 *  @param inflightmessages Messages in-flight.
	 *  @param peerinfos Information about known peers.
	 *  @param timedtaskdispatcher The timed task dispatcher.
	 *  @param threadpool The thread pool.
	 */
	public SendingThreadTask(DatagramSocket socket, PriorityBlockingQueue<TxPacket> packetqueue, AtomicInteger sendquota, Map<Integer, TxMessage> inflightmessages, Map<InetSocketAddress, PeerInfo> peerinfos, TimedTaskDispatcher timedtaskdispatcher)
	{
		this.socket = socket;
		this.packetqueue = packetqueue;
		this.sendquota = sendquota;
		this.inflightmessages = inflightmessages;
		this.peerinfos = peerinfos;
		this.timedtaskdispatcher = timedtaskdispatcher;
		this.state = RUNNING_STATE;
	}
	
	public void run()
	{
		boolean running = true;
		PeerInfo[] peers = null;
		int currentpeer = 0;
		boolean scheduledmessage = false;
		TxMessage currentmessage = null;
//		List<Integer> scheduledmessages = new LinkedList<Integer>();
		
		while (running)
		{
			if (!packetqueue.isEmpty())
			{
				TxPacket packet = null;
				synchronized(packetqueue)
				{
					packet = packetqueue.peek();
//					System.out.println("packet: "+packet.isCost()+" " + packet.getPriority() + " " + packet.getRawPacket()[0]);
					if (packet instanceof TxShutdownTask)
					{
						running = false;
					}
					
					if (packet.isCost())
					{
						int remains = sendquota.get();
						if (remains >= packet.getRawPacket().length)
						{
							packet = packetqueue.poll();
							sendquota.addAndGet(-packet.getRawPacket().length);
							packet.setPriority(STunables.RESEND_MESSAGES_PRIORITY);
						}
						else
						{
							packet = null;
						}
//						System.out.println("Red: " + sendquota.get());
					}
					else
					{
						packet = packetqueue.poll();
					}
				}
				
				if (packet != null)
				{
					if (!packet.isConfirmed())
					{
//						System.out.println("Packet to wire: " + packet.getPacketNumber());
						running = sendPacket(packet);
					}
					if (packet.isCost())
					{
						int packetnum = packet.getPacketNumber();
						currentmessage.setLastSentPacket(packetnum);
						if (((packetnum + 1) == currentmessage.getPackets().length) && currentmessage.getSentCallback() != null)
						{
							Runnable cb = currentmessage.getSentCallback();
							currentmessage.setSentCallback(null);
							timedtaskdispatcher.executeNow(cb);
							currentmessage = null;
						}
						packet.enableCost(false);
					}
					Runnable cb = packet.getSentCallback();
					if (cb != null)
					{
						timedtaskdispatcher.executeNow(cb);
					}
				}
				else
				{
					synchronized(packetqueue)
					{
						packet = packetqueue.peek();
						int remains = sendquota.get();
						if (packet != null && packet.isCost() && packet.getRawPacket().length > remains)
						{
//							System.out.println("Entering quota wait: " + packet.getRawPacket().length);
							state = WAIT_STATE;
							Runnable cb = currentmessage.getSentCallback();
							if (cb != null)
							{
								currentmessage.setSentCallback(null);
								timedtaskdispatcher.executeNow(cb);
							}
							try
							{
								packetqueue.wait();
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
							state = RUNNING_STATE;
//							System.out.println("Exiting quota wait.");
						}
						packet = null;
					}
				}
			}
			else
			{
				if (peers == null)
				{
					currentpeer = 0;
					synchronized(peerinfos)
					{
						peers = peerinfos.values().toArray(new PeerInfo[peerinfos.size()]);
					}
				}
				
//				for (Iterator<Integer> it = scheduledmessages.iterator(); it.hasNext(); )
//				{
//					int msgid = it.next();
//					if (!inflightmessages.containsKey(msgid))
//					{
//						it.remove();
//					}
//				}
				
				if (peers.length > 0)
				{
					TxMessage msg = peers[currentpeer].getMessageQueue().poll();
					if (msg != null && inflightmessages.containsKey(msg.getMsgId()))
					{
						currentmessage = msg;
//						if (msg.getResendCounter() == 0)
//						{
//							scheduledmessage = true;
//							SendingThreadTask.schedmsg.add(msg.getMsgId());
//							synchronized (schedmsg)
//							{
//								System.out.println("msgaccounting+: " + Arrays.toString(SendingThreadTask.schedmsg.toArray()));
//							}
//						}
						for (int i = 0; i < currentmessage.getPackets().length; ++i)
						{
							if (!currentmessage.getPackets()[i].isConfirmed())
							{
//								System.out.println("Adding packet: " + msg.getMsgId() + " " + currentmessage.getPackets()[i]);
								packetqueue.put(currentmessage.getPackets()[i]);
							}
						}
					}
				}
				
				if (peers.length == 0 || ++currentpeer >= peers.length)
				{
					if (!scheduledmessage)
					{
						synchronized(packetqueue)
						{
							int msgcount = 0;
							synchronized(peerinfos)
							{
								peers = peerinfos.values().toArray(new PeerInfo[peerinfos.size()]);
								for (PeerInfo info : peerinfos.values())
								{
									msgcount += info.getMessageQueue().size();
								}
							}
							
							if (msgcount == 0 && packetqueue.isEmpty())
							{
								state = IDLE_STATE;
								try
								{
//									System.out.println("Entering packet wait.");
									packetqueue.wait();
								}
								catch (InterruptedException e)
								{
								}
								state = RUNNING_STATE;
//								System.out.println("Exiting packet wait.");
							}
						}
						
					}
					else
					{
						scheduledmessage = false;
					}
					peers = null;
				}
			}
		}
		state = EXITED_STATE;
	}
	
	/**
	 *  Gets the state.
	 *  
	 *  @return The state.
	 */
	public String getState()
	{
		return state;
	}
	
	/**
	 *  Sends a packet to a receiver.
	 *  
	 *  @param resreceiver The receiver.
	 *  @param task The transmission task.
	 *  @param packet The packet.
	 *  @return True, if the packet has been sent.
	 */
	protected boolean sendPacket(TxPacket packet)
	{
		boolean ret = true;
		InetSocketAddress resreceiver = packet.getResolvedReceiver();
		DatagramPacket dgp = new DatagramPacket(packet.getRawPacket(), packet.getRawPacket().length, resreceiver.getAddress(), resreceiver.getPort());
		try
		{
			//System.out.println(packet + " " + "Sending to: " + resreceiver.getAddress() + ":" + resreceiver.getPort() + " Length: " + packet.getRawPacket().length);
			socket.send(dgp);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	/**
	 *  Queues a packet for sending.
	 *  
	 *  @param packetqueue The packet queue.
	 *  @param packet The packet.
	 */
	public static final void queuePacket(PriorityBlockingQueue<TxPacket> packetqueue, TxPacket packet)
	{
		synchronized (packetqueue)
		{
			packetqueue.offer(packet);
			packetqueue.notifyAll();
		}
	}
	
	/**
	 *  Queues a message for sending.
	 *  
	 *  @param packetqueue The packet queue.
	 *  @param peerinfo The peer.
	 *  @param packet The packet.
	 */
	public static final void queueMessage(PriorityBlockingQueue<TxPacket> packetqueue, PeerInfo peerinfo, TxMessage msg)
	{
		synchronized (packetqueue)
		{
			try
			{
				peerinfo.getMessageQueue().put(msg);
			}
			catch (InterruptedException e)
			{
			}
			packetqueue.notifyAll();
		}
	}
}
