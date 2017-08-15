package jadex.bridge.component.streams;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Handler that sits between connection and message service.
 *  Is used by connection to forward user requests.
 *  Is used by the message service to signal arrived messages.
 */
public class InputConnectionHandler extends AbstractConnectionHandler implements IInputConnectionHandler
{
	//-------- attributes -------- 
	
	/** The last in order received sequence number. */
	protected int rseqno;
	
	/** The highest yet (may be out of order) received sequence number. */
	protected int maxseqno;
	
	/** The highest yet (may be out of order) acknowledged sequence number (only used to trigger new acks every x messages). */
	protected int maxackseqno;
	
	/** The maximum buffer size for out of order packets. */
	protected int maxbuf;
	
	/** The maximum bytes of data that can be stored in connection (without being consumed). */
	protected int maxstored;
	
	/** The data (stored here only as long as it is out of order or incomplete). 
	    Ready data will be forwarded to the connection. Also remembers if an acknowledgement has been sent.*/
	protected Map<Integer, Tuple2<byte[], Boolean>> data;
	
	
	/** The last in order sequence number acknowledged. */
	protected int lastack;
	
	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The current timer. */
	protected TimerTask datatimer;
	
	
	/** The last sequence number. */
	protected int lastseqno;
	
	
	//-------- constructors --------
	
	/**
	 *  Create a new input connection handler.
	 */
	public InputConnectionHandler(IInternalAccess component, Map<String, Object> nonfunc)
	{
		super(component, nonfunc);
		
		this.rseqno = 0;
		this.maxseqno = 0;
		this.maxbuf = 10000;
		this.maxstored = 10000; 
		this.data = new HashMap<Integer, Tuple2<byte[], Boolean>>();
	
		this.ackcnt = 10;
		this.lastack = -1;
		this.lastseqno = -1;
	}

	//-------- methods called from message service --------
	
	/**
	 *  From initiator.
	 * 
	 *  Called when a close message was received.
	 *  participant acks and closes
	 *  
	 *  @param seqno The last data packet.
	 */
	public void closeReceived(final int seqno)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Remember that close message was received, close the connection and send an ack.
//				System.out.println("close received: "+seqno+" "+rseqno+" "+getConnectionId());
				sendTask(createTask(ACKCLOSE, null, null, nonfunc));
				if(seqno==rseqno)
				{
					sendDataAck(); // send missing acks to speedup closing
					if(!con.isClosed())
						con.setClosed();
				}
				else
				{
					// closes later unilaterally
					lastseqno = seqno;
				}
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
		return scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void> ret = new Future<Void>();
				// Send a close request
//				System.out.println("do close input side");
				try
				{
					// Needs nothing to do with ack response.
					sendAcknowledgedMessage(createTask(CLOSEREQ, null, null, nonfunc), CLOSEREQ)
						.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
					{
						public void customResultAvailable(Object result)
						{
	//						System.out.println("close ack");
							ret.setResult(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{exception.printStackTrace();
							System.out.println("no close ack: "+exception);
							// Todo: what about already received data!?
							scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									if(!con.isClosed())
										con.setClosed();
									return IFuture.DONE;
								}
							});
							super.exceptionOccurred(exception);
						}
					});
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
								
				return ret;
			}
		});
	}
	
	/**
	 *  Called from message service.
	 *  
	 *  Uses: data
	 *  
	 *  @param data The new data.
	 */
	public void addData(final int seqnumber, final byte[] dat)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!con.isClosed())
				{
					maxseqno	= Math.max(maxseqno, seqnumber);
					
//					System.out.println("received: "+seqnumber+" "+System.currentTimeMillis());
					
					// If packet is the next one deliver to stream
					// else store in map till the next one arrives
					int expseqno = getSequenceNumber()+1;
					if(seqnumber==expseqno)
					{
//						System.out.println("forwarding: "+seqnumber);
						
						forwardData(dat);
						
						// Forward possibly stored data
						Tuple2<byte[], Boolean> nextdata = data.remove(Integer.valueOf(getSequenceNumber()+1));
						for(; nextdata!=null ;)
						{
//							System.out.println("forwarding stored: "+(getSequenceNumber()+1));
							forwardData(nextdata.getFirstEntity());
							nextdata = data.remove(Integer.valueOf(getSequenceNumber()+1));
						}
					}
					else
					{
//						System.out.println("storing: "+seqnumber+", size="+data.size()+", lastack="+lastack);
						// ack msg may be lost, repeat ack msg
						if(lastack>=seqnumber || data.containsKey(Integer.valueOf(seqnumber)))
						{
							// todo: ack also more than one packet?
							sendDataAck(seqnumber, seqnumber, false);
						}
						else
						{
							data.put(Integer.valueOf(seqnumber), new Tuple2<byte[], Boolean>(dat, Boolean.FALSE));
							if(data.size()>maxbuf)
							{
								System.out.println("Closing connection due to package loss: "+seqnumber+" :"+data.size());
								con.close();
//								data.clear();
							}
						}
					}
					
					// Directly acknowledge when ackcnt packets have been received
					// or start timer to acknowledge less packages in an interval.
					if(maxseqno>maxackseqno+ackcnt || maxseqno==lastseqno)
					{
						sendDataAck();
					}
					else 
					{
						createDataTimer(acktimeout);
					}
				}
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Forward the data to the input connection.
	 */
	protected void forwardData(byte[] data)
	{
//		System.out.println("forward data: "+SUtil.arrayToString(data));
		
		int seqno = getNextReceivedSequenceNumber();
		getInputConnection().addData(data);
		
		// if last packet was received close this side
		if(seqno==lastseqno)
		{
			if(!con.isClosed())
				con.setClosed();
		}
	}
	
	/**
	 *  Get the last received sequence number.
	 *  @return the sequence number.
	 */
	public int getSequenceNumber()
	{
		return rseqno;
	}
	
	/**
	 *  Get the next received sequence number.
	 *  @return the sequence number.
	 */
	public int getNextReceivedSequenceNumber()
	{
		return ++rseqno;
	}

	/**
	 *  Create a new data ack timer.
	 *  Sends an ack automatically after some timeout.
	 */
	public void createDataTimer(long acktimeout)
	{
		// Test if packets have been received till creation
		if(datatimer==null && rseqno>lastack && acktimeout!=Timeout.NONE)
		{
			datatimer = waitForRealDelay(acktimeout, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
	//						System.out.println("timer ack");
					sendDataAck();
					datatimer = null;
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Send ack data.
	 */
	protected void sendDataAck()
	{
		// Send missing acks for all available data
		int seqno	= maxseqno;
		while(seqno>lastack)
		{
			// Skip empty or acknowledged slots to find first unack slot from right.
			while(seqno>getSequenceNumber() && (!data.containsKey(Integer.valueOf(seqno)) 
				|| data.get(Integer.valueOf(seqno)).getSecondEntity().booleanValue()))
			{
				seqno--;
			}

			int end	= seqno;	// End of block.
			int start	= seqno;	// Start of block.
			// Find block of received messages (don't exclude already acknowledged to reduce number of required messages)
			while(seqno>getSequenceNumber() && seqno>lastack && data.containsKey(Integer.valueOf(seqno)))
			{
				if(!data.get(Integer.valueOf(seqno)).getSecondEntity().booleanValue())
				{
					data.put(Integer.valueOf(seqno), new Tuple2<byte[], Boolean>(data.get(Integer.valueOf(seqno)).getFirstEntity(), Boolean.TRUE));
					start	= seqno;	// Only start from unacknowledged message.
				}
				seqno--;
			}
			
			// if reached back to current seq no then acks can be sent back to lastack because
			// all messages before rseqno have been received in order
			if(seqno==getSequenceNumber())	// acknowldege forwarded data, if block expands to current rseqno.
			{
				start	= lastack+1;
				lastack	= end;
			}
			
			if(start<end)	// found at least one message?
			{
//				System.out.println("send ack: start="+start+" end="+end+" cur="+rseqno+" last="+lastack+" "+System.currentTimeMillis());
				sendDataAck(start, end, isStop());
			}
		}
		maxackseqno	= maxseqno;
	}
	
	/**
	 * 
	 */
	protected void sendDataAck(int startseqno, int endseqno, boolean stop)
	{
		sendTask(createTask(ACKDATA, new AckInfo(startseqno, endseqno, stop), true, null, nonfunc));
	}
	
	/**
	 *  Get the input connection.
	 *  @return The input connection.
	 */
	public InputConnection getInputConnection()
	{
		return (InputConnection)getConnection();
	}
	
	/**
	 *  Test if stop is activated (too much data arrived).
	 */
	protected boolean isStop()
	{
		return getInputConnection().getStoredDataSize()>=maxstored;
	}
	
	/**
	 *  Called by connection when user read some data
	 *  so that other side can continue to send.
	 */
	public void notifyDataRead()
	{
		// nop, only used for local connections
	}
	
//	/**
//	 * 
//	 */
//	protected JPanel createPanel()
//	{
//		JPanel ret = new JPanel(new BorderLayout());
//		JPanel p1 = super.createPanel();
//		JPanel p2 = new InputConnectionPanel();
//		ret.add(p1, BorderLayout.NORTH);
//		ret.add(p2, BorderLayout.CENTER);
//		return ret;
//	}
	
//	/**
//	 * 
//	 */
//	public class InputConnectionPanel extends JPanel
//	{
//		/**
//		 * 
//		 */
//		public InputConnectionPanel()
//		{
//			PropertiesPanel pp = new PropertiesPanel("Input properties");
//			final JTextField tfrseqno = pp.createTextField("rseqno");
//			final JTextField tfmaxseqno = pp.createTextField("maxseqno");
//			final JTextField tfmaxbuf = pp.createTextField("maxbuf");
//			final JTextField tfmaxstored = pp.createTextField("maxstored");
//			final JTextField tfdata= pp.createTextField("data");
//			final JTextField tflastack = pp.createTextField("lastack");
//			final JTextField tfackcnt = pp.createTextField("ackcnt");
//			final JTextField tflastseqno = pp.createTextField("lastseqno");
//			
//			Timer t = new Timer(1000, new ActionListener()
//			{
//				public void actionPerformed(ActionEvent e)
//				{
//					tfrseqno.setText(""+rseqno);
//					tfmaxseqno.setText(""+maxseqno);
//					tfmaxbuf.setText(""+maxbuf);
//					tfmaxstored.setText(""+maxstored);
//					tfdata.setText(""+data.size());
//					tflastack.setText(""+lastack);
//					tfackcnt.setText(""+ackcnt);
//					tflastseqno.setText(""+lastseqno);
//				}
//			});
//			t.start();
//			
//			setLayout(new BorderLayout());
//			add(pp, BorderLayout.CENTER);
//		}
//	}
	
}