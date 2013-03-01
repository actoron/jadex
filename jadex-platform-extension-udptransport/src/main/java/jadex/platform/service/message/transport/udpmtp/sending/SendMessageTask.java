package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.commons.IResultCommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.message.ISendTask;
import jadex.platform.service.message.transport.udpmtp.PeerInfo;
import jadex.platform.service.message.transport.udpmtp.STunables;
import jadex.platform.service.message.transport.udpmtp.TimedTask;
import jadex.platform.service.message.transport.udpmtp.TimedTaskDispatcher;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *  Task used to send a message, including resends.
 *
 */
public class SendMessageTask implements Runnable
{
	/** The receiver of the message. */
	protected InetSocketAddress resolvedreceiver;
	
	/** The message ID. */
	protected int msgid;
	
	/** The send task. */
	protected ISendTask task;
	
	/** The confirmation future. */
	protected Future<Void> conffuture;
	
	/** The peer information */
	protected PeerInfo peerinfo;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** The timed task dispatcher. */
	protected TimedTaskDispatcher timedtaskdispatcher;
	
	/** The flow control. */
	protected FlowControl flowcontrol;
	
	/**
	 *  Creates the task.
	 *  
	 *  @param resolvedreceiver The receiver of the message.
	 *  @param msgid The message ID.
	 *  @param task The send task.
	 *  @param conffuture The confirmation future.
	 *  @param peerinfo The peer information.
	 *  @param inflightmessages Messages in-flight.
	 *  @param packetqueue The transmission queue.
	 *  @param timedtaskdispatcher The timed task dispatcher.
	 *  @param flowcontrol The flow control.
	 */
	protected SendMessageTask(InetSocketAddress resolvedreceiver,
						   int msgid,
						   ISendTask task,
						   Future<Void> conffuture,
						   PeerInfo peerinfo,
						   Map<Integer, TxMessage> inflightmessages,
						   PriorityBlockingQueue<TxPacket> packetqueue,
						   TimedTaskDispatcher timedtaskdispatcher,
						   FlowControl flowcontrol)
	{
		this.resolvedreceiver = resolvedreceiver;
		this.msgid = msgid;
		this.task = task;
		this.conffuture = conffuture;
		this.peerinfo = peerinfo;
		this.inflightmessages = inflightmessages;
		this.packetqueue = packetqueue;
		this.timedtaskdispatcher = timedtaskdispatcher;
		this.flowcontrol = flowcontrol;
	}
	
	/**
	 *  Sends the message.
	 */
	public void run()
	{
		final TxMessage msg = TxMessage.createTxMessage(resolvedreceiver, msgid, task, conffuture, new TimedTask(resolvedreceiver, Long.MIN_VALUE)
			{
				/** Flag for re-send mode. */
				protected boolean resend = false;
				
				public void run()
				{
					if (resend)
					{
//						System.out.println("Running resend for: " + msgid);
						resend = false;
						TxMessage msg = inflightmessages.get(msgid);
						if (msg != null)
						{
							if (msg.getResendCounter() < STunables.MAX_RESENDS)
							{
//								System.out.println("Performing resend for: " + msgid + " to " + msg.getLastSentPacket());
								List<TxPacket> packets = new ArrayList<TxPacket>();
								for (int i = 0; i <= msg.getLastSentPacket(); ++i)
								{
									if (!msg.getPackets()[i].isConfirmed())
									{
//										sendquota.addAndGet(msg.getPackets()[i].getRawPacket().length);
										packets.add(msg.getPackets()[i]);
										flowcontrol.resend();
									}
								}
								msg.setResendCounter(msg.getResendCounter() + 1);
								
								if (packets.isEmpty())
								{
									packets.add(msg.getPackets()[msg.getPackets().length - 1]);
								}
								
								packets.get(packets.size() - 1).setSentCallback(this);
								
								for (TxPacket packet : packets)
								{
									SendingThreadTask.queuePacket(packetqueue, packet);
								}
								
//								System.out.println("Done resend for: " + msgid + " " + packets.size());
							}
							else
							{
								inflightmessages.remove(msgid);
								synchronized (msg)
								{
									int quota = 0;
									for (int i = 0; i < msg.getPackets().length; ++i)
									{
										if (!msg.getPackets()[i].isConfirmed())
										{
											msg.getPackets()[i].setConfirmed(true);
											quota += msg.getPackets()[i].getRawPacket().length;
//											sendquota.addAndGet(msg.getPackets()[i].getRawPacket().length);
//											System.out.println("IncA: " + msgid + " " + sendquota.get());
										}
									}
									flowcontrol.getSendQuota().addAndGet(quota);
								}
								msg.transmissionFailed("Message exceeded resend count.");
							}
						}
						else
						{
							// Message confirmed.
							return;
						}
					}
					else
					{
						resend = true;
//						System.out.println("Resend Callback: " + msgid + " curr " + System.currentTimeMillis() + " next " + (System.currentTimeMillis() + (long) (peerinfo.getPing() * STunables.RESEND_DELAY_FACTOR)));
						executiontime = System.currentTimeMillis() + (long) (peerinfo.getPing() * STunables.RESEND_DELAY_FACTOR);
						timedtaskdispatcher.scheduleTask(this);
					}
				}
			}, STunables.ENABLE_TINY_MODE, STunables.ENABLE_SMALL_MODE);
		
		inflightmessages.put(msgid, msg);
		msg.ls = System.currentTimeMillis();
		SendingThreadTask.queueMessage(packetqueue, peerinfo, msg);
	}
	
	/**
	 *  Sends a message.
	 *  
	 *  @param resolvedreceiver The receiver of the message.
	 *  @param msgid The message ID.
	 *  @param task The send task.
	 *  @param peerinfo The peer information.
	 *  @param inflightmessages Messages in-flight.
	 *  @param packetqueue The transmission queue.
	 *  @param timedtaskdispatcher The timed task dispatcher.
	 *  @param flowcontrol The flow control.
	 */
	public static final void executeTask(final InetSocketAddress resolvedreceiver,
										 final int msgid,
										 final ISendTask task,
										 final PeerInfo peerinfo,
										 final Map<Integer, TxMessage> inflightmessages,
										 final PriorityBlockingQueue<TxPacket> packetqueue,
										 final TimedTaskDispatcher timedtaskdispatcher,
										 final FlowControl flowcontrol)
	{
		task.ready(new IResultCommand<IFuture<Void>, Void>()
		{
			
			public IFuture<Void> execute(Void args)
			{
				final Future<Void> ret = new Future<Void>();
				
				timedtaskdispatcher.executeNow(new SendMessageTask(resolvedreceiver,
													   			   msgid,
													   			   task,
													   			   ret,
													   			   peerinfo,
													   			   inflightmessages,
													   			   packetqueue,
													   			   timedtaskdispatcher,
													   			   flowcontrol));
				
				return ret;
			}
		});
	}
}
