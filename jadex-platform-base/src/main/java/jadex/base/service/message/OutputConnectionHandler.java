package jadex.base.service.message;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 *  The output connection handler. 
 */
public class OutputConnectionHandler extends AbstractConnectionHandler implements IOutputConnectionHandler
{
	//-------- attributes --------
	
	/** The data sent (not acknowledged). */
	protected Map<Integer, Tuple2<StreamSendTask, Integer>> sent;
	
	/** The data to send. */
	protected List<Tuple2<StreamSendTask, Future<Void>>> tosend;

	/** The current sequence number. */
	protected int seqnumber;
	
	/** The max number of packets that can be sent without an ack is received. */
	protected int maxsend;


	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The acknowledgement timer. */
	protected TimerTask acktimer;
	
	
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
	protected TimerTask mpsendtimer;
	
	
	/** Close request flag (when a closereq message was received). */
	protected boolean closereqflag;

	/** Stop flag (is sent in ack from input side) to signal that the rceiver is flooded with data). */
	protected Tuple2<Boolean, Integer> stopflag;
	
	/** Flag if close was already sent. */
	protected boolean closesent;
	
	/** Future used in waitForReady(). */
	protected Future<Void> readyfuture;
	
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
	public void ackDataReceived(final AckInfo ackinfo)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Update stop if newer sequence number
				if(stopflag.getSecondEntity().intValue()<ackinfo.getEndSequenceNumber())
					stopflag = new Tuple2<Boolean, Integer>(ackinfo.isStop()? Boolean.TRUE: Boolean.FALSE, new Integer(ackinfo.getEndSequenceNumber()));
				
				// remove all acked packets
				for(int i=ackinfo.getStartSequenceNumber(); i<=ackinfo.getEndSequenceNumber(); i++)
				{
//					Tuple2<StreamSendTask, Integer> tup =
						sent.remove(new Integer(i));
				}
					
//				System.out.println("ack: "+seqnumber+" "+stop+" "+s+" "+sent.size());
//				System.out.println(sent);
				
				// Try to send stored messages after some others have been acknowledged
				sendStored();
				
				// Check ready state.
				checkWaitForReady();
				
				// Try to close if close is requested.
				checkClose();
			
				return IFuture.DONE;
			}
		});
	}
	
	//-------- methods called from connection ---------
	
	/**
	 * 
	 */
	public void notifyInited()
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				sendStored();
				checkClose();
				return IFuture.DONE;
			}
		});
	}
	
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
//				System.out.println("do close output side");
				closereqflag = true;
				checkClose();
				
				return IFuture.DONE;
			}
		}).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
				con.setClosed();
				ret.setException(exception);
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
				
				// Check ready state.
				checkWaitForReady();
				
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
				sendStored();
				checkWaitForReady();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Void> waitForReady()
	{
		final Future<Void> ret = new Future<Void>();
		
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(readyfuture!=null)
				{
					ret.setException(new RuntimeException("Must not be called twice without waiting for result."));
				}
				else
				{
					System.out.println("readyfuture inited");
					readyfuture = ret;
					checkWaitForReady();
				}
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void checkWaitForReady()
	{
		if(readyfuture!=null)
		{
			System.out.println("waitforready: "+con.isInited()+" "+(maxsend-sent.size())+" "+isStop()+" "+isClosed());
			if(con.isInited() && maxsend-sent.size()>0 && !isStop() && !isClosed())
			{
				System.out.println("readyfuture fired");
				Future<Void> ret = readyfuture;
				readyfuture = null;
				ret.setResult(null);
			}
			else if(isClosed())
			{
				readyfuture.setException(new RuntimeException("Connection closed."));
			}
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> doSendData(StreamSendTask task)
	{
		IFuture<Void> ret;
		
		if(isSendAllowed())
		{
//			System.out.println("send: "+task.getSequenceNumber());
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
		
		// Only send data messages after init 
		// but cannot use isSendAllowed() as
		// at least one message should be sent in case of stop
		// to provoke acks with continue
		if(con.isInited())
		{
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
		return con.isInited() && maxsend-sent.size()>0 && !isStop();
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
	protected boolean isDataSendFinished()
	{
		// No packet to send any more?
//		System.out.println("isDataFinished (unacknowledged, multipacketsize): "+sent.size()+" "+mpsize);
//		return sent.isEmpty() && (!multipackets || mpsize==0);
		return tosend.isEmpty() && !multipackets || mpsize==0;
	}
	
	/**
	 *  Tests if the data processing of the connection is finished. 
	 */
	protected boolean isDataAckFinished()
	{
		// All acks received
		return sent.isEmpty();
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
		mpsendtimer = ms.waitForRealDelay(mpsendtimeout, new IComponentStep<Void>()
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
				acktimer = ms.waitForRealDelay(acktimeout, new IComponentStep<Void>()
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
								checkWaitForReady();
								return IFuture.DONE;
							}
						});
						return IFuture.DONE;
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
		ms.waitForRealDelay(10000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("data timer triggered");
				
				sendStored();
				
				checkClose();
				
				if(!isClosed())
					createDataTimer();
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 * 
	 */
	protected void checkClose()
	{
		// Try to close if close is requested.
		if(isCloseRequested() && isDataSendFinished() && con.isInited() && !con.isClosed())
		{
			// If close() was already called on connection directly perform close
			if(con.isClosing())
			{
//				System.out.println("sending close output side");
				// Send close message and wait until it was acked
				sendAcknowledgedMessage(createTask(StreamSendTask.CLOSE, SUtil.intToBytes(seqnumber), null), StreamSendTask.CLOSE)
					.addResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						closesent = true;
					}
					
					public void exceptionOccurred(Exception exception)
					{
						closesent = true;
					}
				});
			}
			else
			{
				close();
			}
			closereqflag = false; // ensure that close is executed only once
		}
		
		// If all data sent and acked and not already closed and close message was acked
		if(isDataSendFinished() && isDataAckFinished() && !con.isClosed() && closesent)
		{
//			System.out.println("close end output side");
			// Set connection as closed.
			con.setClosed();
		}
	}
	
}
