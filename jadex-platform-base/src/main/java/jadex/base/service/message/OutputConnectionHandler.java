package jadex.base.service.message;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The output connection handler. 
 */
public class OutputConnectionHandler extends AbstractConnectionHandler
{
	//-------- attributes --------
	
	/** The data sent (not acknowledged). */
	protected Map<Integer, Tuple2<StreamSendTask, Integer>> sent;
	
	/** The data to send. */
	protected List<Tuple2<StreamSendTask, Future<Void>>> tosend;

	/** The current sequence number. */
	protected int seqnumber;
	
	/** The last acknowledged packet number. */
	protected int lastack;
	
	/** The max number of packets that can be sent without an ack is received. */
	protected int maxsend;


	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The acknowledgement timer. */
	protected ITimer acktimer;
	
	
	/** Flag if multipackets should be used. */
	protected boolean multipackets;
	
	/** The packet size to collect (in bytes). */
	protected int mpmaxsize;
	
	/** The collected data for a packet. */
	protected List<byte[]> multipacket;
	
	/** The current multipacket size. */
	protected int mpsize;
	
	/** The max delay before a multipacket is sent (even if not full). */
	protected long mpsendtimeout;

	/** The multipacket send timer. */
	protected ITimer mpsendtimer;
	
	
	/** Close request flag (when a closereq message was received). */
	protected boolean closereqflag;

	/** Stop flag (is sent in ack from input side) to signal that the rceiver is flooded with data). */
	protected Tuple2<Boolean, Integer> stopflag;
	
	//-------- constructors --------
	
//	/**
//	 *  Creat a new handler.
//	 */
//	public OutputConnectionHandler(MessageService ms)
//	{
//		this();
//	}
	
//	/**
//	 *  Creat a new handler.
//	 */
//	public OutputConnectionHandler(MessageService ms, int maxresends, long acktimeout, 
//		long leasetime)
	/**
	 *  Creat a new handler.
	 */
	public OutputConnectionHandler(MessageService ms)
	{
		super(ms);//, maxresends, acktimeout, leasetime);
		this.tosend = new ArrayList<Tuple2<StreamSendTask, Future<Void>>>();
		this.sent = new HashMap<Integer, Tuple2<StreamSendTask, Integer>>();
		this.seqnumber = 0;
		this.lastack = 0;
		
		this.maxsend = 20;
		this.ackcnt = 10;
		
		this.multipackets = true;
		this.mpmaxsize = 50000;
		this.multipacket = new ArrayList<byte[]>();
		this.mpsize = 0;
		this.mpsendtimeout = 3000;
		this.stopflag = new Tuple2<Boolean, Integer>(Boolean.FALSE, -1);
		
		createDataTimer();
	}
	
	//-------- methods called from message service --------
	
	/**
	 *  Received a request to close the connection.
	 */
	public void closeRequestReceived()
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				closereqflag = true;
				checkClose();
				sendTask(createTask(StreamSendTask.ACKCLOSEREQ, null, null));
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Called from message service.
	 *  
	 *  Uses: sent, lastack
	 */
	public void ackData(final int seqnumber, final boolean stop)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Update stop if newer
				if(stopflag.getSecondEntity().intValue()<seqnumber)
					stopflag = new Tuple2<Boolean, Integer>(stop? Boolean.TRUE: Boolean.FALSE, new Integer(seqnumber));
				
				int s = sent.size();
				
//				for(int i=seqnumber; i>lastack && cnt<ackcnt; i--)
				for(int i=0; i<ackcnt && seqnumber-i>lastack; i++)
				{
					Tuple2<StreamSendTask, Integer> tup = sent.remove(new Integer(seqnumber-i));
					if(tup==null)
						System.out.println("Ack error: fix me!!!");
//						throw new RuntimeException("Ack not possible, data not found.");
				}
				
				lastack = seqnumber;

				System.out.println("ack: "+seqnumber+" "+stop+" "+s+" "+sent.size());
				System.out.println(sent);
				
				// Try to send stored messages after some others have been acknowledged
				sendStored();
				
				// Try to close if close is requested.
				checkClose();
			
				return IFuture.DONE;
			}
		});
	}
	
	//-------- methods called from connection ---------
	
	/**
	 *  Called from connection.
	 *  Initiates closing procedure (is different for initiator and participant).
	 */
	public IFuture<Void> doClose()
	{
		final Future<Void> ret = new Future<Void>();

		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("do close output side");
				
				if(isDataFinished())
				{
					sendAcknowledgedMessage(createTask(StreamSendTask.CLOSE, null, null), StreamSendTask.CLOSE)
						.addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							// Set connection as closed.
							con.setClosed();
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							con.setClosed();
							ret.setException(exception);
						}
					});
				}
				else
				{
					closereqflag = true;
				}
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called from connection.
	 *  
	 *  Uses: sent, tosend
	 */
	public IFuture<Void> send(final byte[] dat)
	{
		final Future<Void> ret = new Future<Void>();
//		ret.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				System.out.println("send end");
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("send end ex");
//			}
//		});

		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("called send: "+sent.size());
				
				sendStored();

				if(multipackets)
				{
					addMultipacket(dat).addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, dat, getNextSequenceNumber());
					doSendData(task).addResultListener(new DelegationResultListener<Void>(ret));
				}
				
				return IFuture.DONE;
			}
		});
	
		return ret;
	}
	
	/**
	 *  Flush the data.
	 */
	public void flush()
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(multipackets)
					sendMultiPacket();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> doSendData(StreamSendTask task)
	{
		IFuture<Void> ret;
		
		if(isSendAllowed())
		{
			System.out.println("send: "+task.getSequenceNumber());
			ret = sendTask(task, null);
		}
		else
		{
//			System.out.println("store: "+task.getSequenceNumber());
			ret = new Future<Void>();
			tosend.add(new Tuple2<StreamSendTask, Future<Void>>(task, (Future<Void>)ret));
		}
		
		return ret;
	}
	
	/**
	 *  Called internally. 
	 * 
	 *  Uses: sent, tosend
	 */
	protected void sendStored()
	{
//		System.out.println("sendStored: "+sent.size());
		int allowed = maxsend-sent.size();
		for(int i=0; i<allowed && tosend.size()>0; i++)
		{
			Tuple2<StreamSendTask, Future<Void>> tup = tosend.remove(0);
//			System.out.println("send Stored: "+tup.getFirstEntity().getSequenceNumber());
			sendTask(tup.getFirstEntity(), tup.getSecondEntity());
		
			// Send only one test message if in stop mode.
			if(isStop())
				break;
		}
	}
	
	/**
	 *  Called internally.
	 * 
	 *  Add data to a multi packet.
	 *  @parm data The data.
	 */
	protected IFuture<Void> addMultipacket(byte[] data)
	{
		IFuture<Void> ret = new Future<Void>();
		
		int start = 0;
		int len = Math.min(mpmaxsize-mpsize, data.length);
		
		List<IFuture<Void>> futs = new ArrayList<IFuture<Void>>();
		while(len>0)
		{
			byte[] part = new byte[len];
			System.arraycopy(data, start, part, 0, len);
			futs.add(addMultiPacketChunk(part));
			start += len;
			len = Math.min(mpmaxsize-mpsize, data.length-start);
		}
		
		if(futs.size()>0)
		{
			CounterResultListener<Void> lis = new CounterResultListener<Void>(futs.size(), 
				new DelegationResultListener<Void>((Future<Void>)ret));
			for(int i=0; i<futs.size(); i++)
			{
				futs.get(i).addResultListener(lis);
			}
		}
		else
		{
			ret = IFuture.DONE;
		}
		
		return ret;
	}
	
	/**
	 *  Called internally.
	 * 
	 *  Add data chunk.
	 *  @param data The data.
	 */
	protected IFuture<Void> addMultiPacketChunk(byte[] data)
	{
		IFuture<Void> ret = IFuture.DONE;
		
		// Install send timer on first packet
		if(mpsize==0)
			createMultipacketSendTimer(getSequenceNumber());
		
		multipacket.add(data);
		mpsize += data.length;
		
		if(mpsize==mpmaxsize)
			ret = sendMultiPacket();
		
		return ret;
	}
	
	/**
	 *  Called internally.
	 * 
	 *  Send a multi packet.
	 */
	protected IFuture<Void> sendMultiPacket()
	{
		IFuture<Void> ret = IFuture.DONE;
		
		if(multipacket.size()>0)
		{
			byte[] target = new byte[mpsize];
			int start = 0;
			for(int i=0; i<multipacket.size(); i++)
			{
				byte[] tmp = multipacket.get(i);
				System.arraycopy(tmp, 0, target, start, tmp.length);
				start += tmp.length;
			}
			
			StreamSendTask task = (StreamSendTask)createTask(StreamSendTask.DATA, target, getNextSequenceNumber());
			doSendData(task);
			
			multipacket.clear();
			mpsize = 0;
		}
		
		return ret;
	}
	
	/**
	 *  Called internally.
	 */
	public IFuture<Void> sendTask(StreamSendTask task, Future<Void> fut)
	{
		IFuture<Void> ret = fut;
		
		if(ret==null)
		{
			ret = sendTask(task);
		}
		else
		{
			sendTask(task).addResultListener(new DelegationResultListener<Void>(fut));
		}
		
		// add task to unacknowledged sent list 
		sent.put(task.getSequenceNumber(), new Tuple2<StreamSendTask, Integer>(task, new Integer(1)));
		
		// create timer if none is active
		createAckTimer(task.getSequenceNumber());
		
		return ret;
	}
	
	/**
	 *  Called from timer.
	 *  
	 *  Uses: sent
	 */
	public IFuture<Void> resend(int seqnumber)
	{
		IFuture<Void> ret = IFuture.DONE;
		
		Tuple2<StreamSendTask, Integer> tup = sent.get(new Integer(seqnumber));
		if(tup!=null)
		{
			if(tup.getSecondEntity().intValue()==maxresends)
			{
				con.close();
			}
			else
			{
//				System.out.println("resend: "+seqnumber);
				StreamSendTask task = tup.getFirstEntity();
				ret = sendTask(tup.getFirstEntity());
				sent.put(task.getSequenceNumber(), new Tuple2<StreamSendTask, Integer>(task, new Integer(tup.getSecondEntity().intValue()+1)));
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected boolean isSendAllowed()
	{
		return maxsend-sent.size()>0 && !isStop();
	}
	
	/**
	 * 
	 */
	protected boolean isStop()
	{
		return stopflag.getFirstEntity().booleanValue();
	}
	
	/**
	 *  Get the closereq.
	 *  @return The closereq.
	 */
	public boolean isCloseRequested()
	{
		return closereqflag;
	}

	/**
	 *  Tests if the data processing of the connection is finished. 
	 */
	public boolean isDataFinished()
	{
		// All acks received and no unfinished multi packet
		System.out.println("isDataFinished: "+sent.size()+" "+mpsize);
		return sent.isEmpty() && (!multipackets || mpsize==0);
	}
	
	/**
	 *  Set the connection closed.
	 */
	public void setClosed()
	{
		con.setClosed();
	}
	
	/**
	 *  Get the output connection.
	 *  @return The connection.
	 */
	public OutputConnection getOutputConnection()
	{
		return (OutputConnection)getConnection();
	}
	
	/**
	 *  Get the seqnumber.
	 *  @return the seqnumber.
	 */
	public int getSequenceNumber()
	{
		return seqnumber;
	}
	
	/**
	 *  Get The next seqnumber.
	 *  @return The next seqnumber.
	 */
	public int getNextSequenceNumber()
	{
		return ++seqnumber;
	}
	
	/**
	 *  This timer automatically sends non-full multipackets after mpsendtimeout has occurred.
	 */
	protected void createMultipacketSendTimer(final int seqno)
	{
		if(mpsendtimer!=null)
			mpsendtimer.cancel();
		mpsendtimer = ms.getClockService().createTimer(mpsendtimeout, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				try
				{
					scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							// Send the packet if it is still the correct one
							if(seqno==getSequenceNumber())
								sendMultiPacket();
							return IFuture.DONE;
						}
					});
				}
				catch(ComponentTerminatedException e)
				{
					// nop
				}
			}
		});
	}
	
	/**
	 *  Triggers resends of packets if no ack has been received in acktimeout.
	 */
	protected void createAckTimer(final int seqno)
	{
		// Test if packets have been sent till last timer was inited
		if(acktimer==null)
		{
			// If more packets have been sent 
			if(getSequenceNumber()>seqno)
			{
//				final int sq = seqno + ackcnt;
				acktimer = ms.getClockService().createTimer(acktimeout, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						try
						{
							scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											// Send all packets of the segment again.
											for(int i=0; i<ackcnt; i++)
											{
												resend(i+seqno);
											}
											acktimer = null;
											createAckTimer(seqno + ackcnt);
											return IFuture.DONE;
										}
									});
									return IFuture.DONE;
								}
							});
						}
						catch(ComponentTerminatedException e)
						{
							// nop
						}
					}
				});
			}
			else
			{
				acktimer = null;
			}
		}
	}
	
	/**
	 *  
	 */
	protected void createDataTimer()
	{
		ms.getClockService().createTimer(10000, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				try
				{
					scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
	//						System.out.println("data timer triggered");
							
							sendStored();
							
							checkClose();
							
							if(!isClosed())
								createDataTimer();
							
							return IFuture.DONE;
						}
					});
				}
				catch(ComponentTerminatedException e)
				{
					// nop
				}
			}
		});
	}
	
	/**
	 * 
	 */
	protected void checkClose()
	{
		// Try to close if close is requested.
		if(isCloseRequested() && isDataFinished())
		{
			if(con.isClosing())
			{
				doClose();
			}
			else
			{
				close();
			}
			closereqflag = false;
		}
//		else
//			System.out.println("check close: "+isCloseRequested()+" "+isDataFinished());
	}
}
