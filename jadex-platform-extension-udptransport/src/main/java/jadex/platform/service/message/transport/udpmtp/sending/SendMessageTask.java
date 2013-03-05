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
import java.util.Iterator;
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
				private boolean resend = false;
				
				/** The maximum re-send counter */
				protected int maxresends = 0;
				
				public void run()
				{
					TxMessage msg = inflightmessages.get(msgid);
					
					if (msg != null)
					{
						if (resend)
						{
//							System.out.println("Running resend for: " + msgid);
							resend = false;
							
							
//								System.out.println("Performing resend for: " + msgid + " to " + msg.getLastSentPacket());
								List<TxPacket> packets = new ArrayList<TxPacket>();
								for (int i = 0; i <= msg.getLastSentPacket(); ++i)
								{
									if (!msg.getPackets()[i].isConfirmed())
									{
										if (msg.getPackets()[i].getResendCounter() < STunables.MAX_RESENDS)
										{
//											sendquota.addAndGet(msg.getPackets()[i].getRawPacket().length);
											packets.add(msg.getPackets()[i]);
										}
										else
										{
											inflightmessages.remove(msgid);
											synchronized (msg)
											{
												int quota = 0;
												for (int j = 0; j < msg.getPackets().length; ++j)
												{
													if (!msg.getPackets()[j].isConfirmed())
													{
														msg.getPackets()[j].setConfirmed(true);
														quota += msg.getPackets()[j].getRawPacket().length;
//														sendquota.addAndGet(msg.getPackets()[i].getRawPacket().length);
//														System.out.println("IncA: " + msgid + " " + sendquota.get());
													}
												}
												flowcontrol.addAndGetQuota(quota);
											}
											msg.transmissionFailed("Message exceeded resend count.");
											return;
										}
									}
								}
								
								if (packets.isEmpty())
								{
									packets.add(msg.getPackets()[0]);
								}
								else
								{
									msg.ls = System.currentTimeMillis();
								}
								
								packets.get(packets.size() - 1).setSentCallback(this);
								
								for (Iterator<TxPacket> it = packets.iterator(); it.hasNext(); )
								{
									TxPacket packet = it.next();
									if (!packet.isConfirmed() || !it.hasNext())
									SendingThreadTask.queuePacket(packetqueue, packet);
									flowcontrol.resend(packet.getRawPacket().length);
									packet.setResendCounter(packet.getResendCounter() + 1);
									maxresends = Math.max(maxresends, packet.getResendCounter());
								}
								
//								System.out.println("Done resend for: " + msgid + " " + packets.size());
							
						}
						else
						{
							resend = true;
//							System.out.println("Resend Callback: " + msgid + " curr " + System.currentTimeMillis() + " next " + (System.currentTimeMillis() + (long) (peerinfo.getPing() * STunables.RESEND_DELAY_FACTOR)));
							executiontime = System.currentTimeMillis() + (long) (peerinfo.getPing() * STunables.RESEND_DELAY_FACTOR * (maxresends + 1));
							timedtaskdispatcher.scheduleTask(this);
						}
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
