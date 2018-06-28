package jadex.bridge.component.streams;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

//import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.Timer;

/**
 *  The output connection handler. 
 */
public class OutputConnectionHandler extends AbstractConnectionHandler implements IOutputConnectionHandler
{
	//-------- attributes --------
	
	/** The data sent (not acknowledged). */
	protected Map<Integer, DataSendInfo> sent;
	
	/** The data to send. */
	protected List<Tuple2<StreamPacket, Future<Void>>> tosend;

	/** The current sequence number. */
	protected int seqnumber;
	
	/** The max number of packets that can be sent without an ack is received. */
	protected int maxsend;
	
	/** The max number of messages that can be sending concurrently (i.e. passed to message service but sending not yet completed). */
	protected int maxqueued;

	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The number of sending messages (i.e. passed to message service but sending not yet completed). */
	protected int queuecnt;
	
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
	
	/** The current multipacket future (shared by all write requests that put data in the same multi packet). */
	protected Future<Void>	mpfut;
	
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
	protected Future<Integer> readyfuture;
	
	//-------- constructors --------
	
	/**
	 *  Create a new handler.
	 */
	public OutputConnectionHandler(IInternalAccess component, Map<String, Object> nonfunc)
	{
		super(component, nonfunc);//, maxresends, acktimeout, leasetime);
		this.tosend = new ArrayList<Tuple2<StreamPacket, Future<Void>>>();
		this.sent = new LinkedHashMap<Integer, DataSendInfo>();
		this.seqnumber = 0;
		
		this.maxsend = 200;
		this.maxqueued = 4;
		this.ackcnt = 10;
		
		this.multipackets = true;
		this.mpmaxsize = 4500;
		this.multipacket = new ArrayList<byte[]>();
		this.mpsize = 0;
		this.mpsendtimeout = 3000;
		this.stopflag = new Tuple2<Boolean, Integer>(Boolean.FALSE, -1);
		
//		createDataTimer();
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
				sendTask(createTask(ACKCLOSEREQ, null, null, nonfunc));
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
					stopflag = new Tuple2<Boolean, Integer>(ackinfo.isStop()? Boolean.TRUE: Boolean.FALSE, Integer.valueOf(ackinfo.getEndSequenceNumber()));
				
				// remove all acked packets
				for(int i=ackinfo.getStartSequenceNumber(); i<=ackinfo.getEndSequenceNumber(); i++)
				{
					DataSendInfo tup = sent.remove(Integer.valueOf(i));
					if(tup!=null)
					{
						tup.getFuture().setResult(null);
					}
				}
					
//				System.out.println("ack "+System.currentTimeMillis()+": seq="+seqnumber+" stop="+ackinfo.isStop()+" startack="+ackinfo.getStartSequenceNumber()+" endack="+ackinfo.getEndSequenceNumber()+" sent="+sent.size());
//				System.out.println(sent);
				
				// Trigger resend of unacknowledged messages, if necessary.
				checkResend();
				
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
				checkWaitForReady();
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
		// Todo: need to copy dat in case user uses array otherwise...
		
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
					StreamPacket task = createTask(DATA, dat, getNextSequenceNumber(), nonfunc);
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
				{
					sendAcknowledgedMultiPacket();
				}
				sendStored();
				checkWaitForReady();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written. Provides a value of how much data should be given to the connection for best performance.
	 */
	public IFuture<Integer> waitForReady()
	{
		final Future<Integer> ret = new Future<Integer>();
		
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
//					System.out.println("readyfuture inited");
					readyfuture = ret;
					checkWaitForReady();
				}
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	//-------- internal methods (single threaded) --------
	
	/**
	 * 
	 */
	protected void checkWaitForReady()
	{
		if(readyfuture!=null)
		{
//			System.out.println("waitforready: "+con.isInited()+" "+(maxsend-sent.size())+" "+isStop()+" "+isClosed());
			if(isSendAllowed() && !isClosed())
			{
//				System.out.println("readyfuture fired");
				Future<Integer> ret = readyfuture;
				readyfuture = null;
//				ret.setResult(Integer.valueOf(mpmaxsize));	// todo: packet size*allowed messages?
				int pa = sent.size()-maxsend;
				ret.setResult(Integer.valueOf(pa>0? pa*mpmaxsize: mpmaxsize));	
			}
			else if(isClosed())
			{
				Future<Integer> ret = readyfuture;
				readyfuture = null;
				ret.setException(new RuntimeException("Connection closed."));
			}
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> doSendData(StreamPacket task)
	{
		IFuture<Void> ret;
		
		if(isSendAllowed())
		{
//			System.out.println("send "+System.currentTimeMillis()+": "+task.getSequenceNumber());
			ret = sendData(task);
		}
		else
		{
//			System.out.println("store "+System.currentTimeMillis()+": "+task.getSequenceNumber());
			ret = new Future<Void>();
			tosend.add(new Tuple2<StreamPacket, Future<Void>>(task, (Future<Void>)ret));
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
//		System.out.println("sendStored: sent="+sent.size()+", allowed="+allowed+", tosend="+tosend.size());
		
		// Cannot use just isSendAllowed() as at least one message
		// should be sent in case of stop to provoke acks with continue
		boolean	test = con.isInited() && sent.size()<maxsend && queuecnt<maxqueued;
		while(!tosend.isEmpty() && (isSendAllowed() || test))
		{
			Tuple2<StreamPacket, Future<Void>> tup = tosend.remove(0);
//			System.out.println("send Stored: "+tup.getFirstEntity().getSequenceNumber());
			sendData(tup.getFirstEntity()).addResultListener(new DelegationResultListener<Void>(tup.getSecondEntity()));
		
			// Send only one test message if in stop mode.
			test = false;
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
		
		Set<IFuture<Void>> futs = new HashSet<IFuture<Void>>();
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
			for(IFuture<Void> fut: futs)
			{
				fut.addResultListener(lis);
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
		if(mpfut==null)
			mpfut	= new Future<Void>();
		IFuture<Void>	ret	= mpfut;
		
		// Install send timer on first packet
		if(mpsize==0)
			createMultipacketSendTimer(getSequenceNumber());
		
		multipacket.add(data);
		mpsize += data.length;
		
		if(mpsize==mpmaxsize)
		{
			sendAcknowledgedMultiPacket().addResultListener(new DelegationResultListener<Void>(mpfut));
			mpfut	= null;
		}
		
		return ret;
	}
	
	/**
	 *  Called internally.
	 * 
	 *  Send a multi packet.
	 */
	protected IFuture<Void> sendAcknowledgedMultiPacket()
	{
		IFuture<Void> ret = IFuture.DONE;
		
		if(multipacket.size()>0)
		{
			byte[] target;
			if(multipacket.size()==1)
			{
				target	= multipacket.get(0);
			}
			else
			{
				target = new byte[mpsize];
				int start = 0;
				for(int i=0; i<multipacket.size(); i++)
				{
					byte[] tmp = multipacket.get(i);
					System.arraycopy(tmp, 0, target, start, tmp.length);
					start += tmp.length;
				}
			}
			
//			System.out.println("sending stream bytes: "+target.length);
			StreamPacket task = createTask(DATA, target, getNextSequenceNumber(), nonfunc);
			ret	= doSendData(task);
			
			multipacket.clear();
			mpsize = 0;
		}
		
		return ret;
	}
	
	/**
	 *  Send or resend a data message.
	 */
	public IFuture<Void> sendData(StreamPacket task)
	{
		DataSendInfo tup = sent.get(task.getSequenceNumber());
		if(tup==null)
		{
			// First try.
			tup	= new DataSendInfo(task);
			
			// add task to unacknowledged sent list 
			sent.put(task.getSequenceNumber(), tup);
		}
		else
		{
			// Retry -> clone task for resend
			task	= tup.retry();
		}
		
//		System.out.println("send "+System.currentTimeMillis()+": "+task.getSequenceNumber());
		queuecnt++;
//		System.out.println("queue: "+queuecnt);
//		final int	seqno	= task.getSequenceNumber();
		sendTask(task).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("Sent "+System.currentTimeMillis()+": seq="+seqno);
				sendDone();
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("Not sent "+System.currentTimeMillis()+": seq="+seqno+", "+exception);
//				exception.printStackTrace();
				sendDone();
			}
			
			protected void sendDone()
			{
				scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						queuecnt--;
						sendStored();
						checkWaitForReady();
						return IFuture.DONE;
					}
				});				
			}
		});
		
		return tup.getFuture();
	}
	
	/**
	 *  Triggers resends of packets if no ack has been received in acktimeout.
	 *  @param id The message id.
	 *  @return The timer.
	 */
	protected TimerTask	createBulkAckTimer(final Object id)
	{
		TimerTask	ret;
		if(acktimeout!=Timeout.NONE)
		{
			// Test if packets have been sent till last timer was inited
			ret	= waitForRealDelay(acktimeout, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					DataSendInfo tup = sent.get(id);
					if(tup!=null)
					{
						tup.doResend();
					}
					return IFuture.DONE;
				}
			});
		}
		else
		{
			ret	= null;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected boolean isSendAllowed()
	{
		return con.isInited() && sent.size()<maxsend && queuecnt<maxqueued && !isStop();
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
		mpsendtimer = waitForRealDelay(mpsendtimeout, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Send the packet if it is still the correct one
				if(seqno==getSequenceNumber())
				{
					assert mpfut!=null;
					sendAcknowledgedMultiPacket().addResultListener(new DelegationResultListener<Void>(mpfut));
					mpfut	= null;
				}
				return IFuture.DONE;
			}
		});
		
		
	}
	
	/**
	 * 
	 */
	protected void checkClose()
	{
//		System.out.println("checkclose0: "+isCloseRequested()+", "+isDataSendFinished()+", "+con.isInited()+", "+!con.isClosed()+", "+con.isClosing()+", "+isDataAckFinished()+", "+closesent);
		
		// Try to close if close is requested.
		if(isCloseRequested() && isDataSendFinished() && con.isInited() && !con.isClosed())
		{
			// If close() was already called on connection directly perform close
			if(con.isClosing())
			{
//				System.out.println("sending close output side");
				// Send close message and wait until it was acked
				sendAcknowledgedMessage(createTask(CLOSE, SUtil.intToBytes(seqnumber), null, nonfunc), CLOSE)
					.addResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
//						System.out.println("ack from close output side");
						closesent = true;
						checkClose();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("no ack from close output side: "+exception);
						// Set connection as closed.
						con.setClosed();
//						closesent = true;
//						checkClose();
					}
				});
			}
			else
			{
//				System.out.println("start closing output side");
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
	
	/**
	 *  Check resending of unacknowledged messages.
	 */
	public void	checkResend()
	{
		// Iterate in insertion order -> oldest first
		for(DataSendInfo tup: sent.values().toArray(new DataSendInfo[0]))
		{
			if(tup.getSequenceNumber()<getSequenceNumber()-(2*maxsend+ackcnt))
			{
				tup.checkResend();
			}
			else
			{
				// No need to check newer messages.
				break;
			}
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  Info about a sent but not yet acknowledged data message.
	 */
	public class DataSendInfo
	{
		//-------- attributes --------
		
		/** The task. */
		protected StreamPacket task;
		
		/** The future. */
		protected Future<Void> fut;
		
		/** The try count. */
		protected int tries;
		
		/** The timer. */
		protected TimerTask	timer;
		
		/** The sequence number during the last sending. */
		protected long	lastsend;
		
		//-------- constructors --------
		
		/**
		 *  Create a send info.
		 */
		public DataSendInfo(StreamPacket task)
		{
			this.task = task;
			this.fut = new Future<Void>();
			this.tries = 1;
			timer = createBulkAckTimer(task.getSequenceNumber());
			lastsend = OutputConnectionHandler.this.getSequenceNumber();
		}
		
		//-------- methods --------
		
		/**
		 *  Get the sequence number.
		 */
		public int	getSequenceNumber()
		{
			return task.getSequenceNumber();
		}
		
		/**
		 *  Get the future.
		 */
		public Future<Void>	getFuture()
		{
			return fut;
		}
		
		/**
		 *  Retry sending the message.
		 *  @return task	The task for resend.
		 */
		public StreamPacket retry()
		{
			if(timer!=null)
				timer.cancel();
			timer = createBulkAckTimer(task.getSequenceNumber());
			lastsend = OutputConnectionHandler.this.getSequenceNumber();
			tries++;
			task = new StreamPacket(task);
//			System.out.println("Retry: #"+tries+", seq="+task.getSequenceNumber());
			return task;
		}
		
		/**
		 *  Called when the message should be resent.
		 */
		public void	doResend()
		{
			if(tries>=maxresends)
			{
//				System.out.println("Message could not be sent.");
				fut.setException(new RuntimeException("Message could not be sent."));
				sent.remove(task.getSequenceNumber());
				con.close();
			}
			else
			{
				sendData(task);
			}			
		}
		
		/**
		 *  Check, if the message should be resent.
		 */
		public void	checkResend()
		{
			// Resend earlier as time permits when many packets are sent
			if(lastsend<OutputConnectionHandler.this.getSequenceNumber()-(2*maxsend+ackcnt))
			{
				doResend();
			}
		}
	}
	
//	/**
//	 * 
//	 */
//	protected JPanel createPanel()
//	{
//		JPanel ret = new JPanel(new BorderLayout());
//		JPanel p1 = super.createPanel();
//		JPanel p2 = new OutputConnectionPanel();
//		ret.add(p1, BorderLayout.NORTH);
//		ret.add(p2, BorderLayout.CENTER);
//		return ret;
//	}
	
//	/**
//	 * 
//	 */
//	public class OutputConnectionPanel extends JPanel
//	{
//		/**
//		 * 
//		 */
//		public OutputConnectionPanel()
//		{
//			PropertiesPanel pp = new PropertiesPanel("Output properties");
//			final JTextField tfsent = pp.createTextField("sent");
//			final JTextField tftosend = pp.createTextField("tosend");
//			final JTextField tfseq = pp.createTextField("seqnumber");
//			final JTextField tfqueuecnt = pp.createTextField("queuecnt");
//			final JTextField tfmpmaxsize = pp.createTextField("mpmaxsize");
//			final JTextField tfmpsize = pp.createTextField("mpsize");
//			final JTextField tfstop = pp.createTextField("stop");
//			
//			final int[] cnt = new int[3];
//			final JTextField tfwaiting = pp.createTextField("waiting");
//			
//			Timer t = new Timer(100, new ActionListener()
//			{
//				public void actionPerformed(ActionEvent e)
//				{
//					tfsent.setText(""+sent.size());
//					tftosend.setText(""+tosend.size());
//					tfseq.setText(""+seqnumber);
//					tfqueuecnt.setText(""+queuecnt);
//					tfmpmaxsize.setText(""+mpmaxsize);
//					tfmpsize.setText(""+mpsize);
//					tfstop.setText(""+stopflag);
//					if(!isSendAllowed())
//						cnt[0]++;
//					if(sent.size()>=maxsend)
//						cnt[1]++;
//					if(queuecnt>=maxqueued)
//						cnt[2]++;
//					tfwaiting.setText(""+cnt[0]+" "+cnt[1]+" "+cnt[2]);
//				}
//			});
//			t.start();
//			
//			setLayout(new BorderLayout());
//			add(pp, BorderLayout.CENTER);
//		}
//	}
	
}
