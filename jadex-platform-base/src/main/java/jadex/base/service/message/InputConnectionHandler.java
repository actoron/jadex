package jadex.base.service.message;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 *  Handler that sits between connection and message service.
 *  Is used by connection to forward user requests.
 *  Is used by the message service to signal arrived messages.
 */
public class InputConnectionHandler extends AbstractConnectionHandler
{
	//-------- attributes -------- 
	
	/** The last received sequence number. */
	protected int rseqno;
	
	/** The maximum buffer size for out of order packets. */
	protected int maxbuf;
	
	/** The maximum bytes of data that can be stored in connection (without being consumed). */
	protected int maxstored;
	
	/** The data (stored here only as long as it is out of order or incomplete). 
	    Ready data will be forwarded to the connection. */
	protected Map<Integer, byte[]> data;
	
	
	/** The last sequence number acknowledged. */
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
	public InputConnectionHandler(MessageService ms)
	{
		super(ms);
		this.rseqno = 0;
		this.maxbuf = 1000;
		this.maxstored = 10000; 
		this.data = new HashMap<Integer, byte[]>();
	
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
				sendTask(createTask(StreamSendTask.ACKCLOSE, null, null));
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
		final Future<Void> ret = new Future<Void>();

		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Send a close request
				System.out.println("do close input side");
				
				// Needs nothing to do with ack response.
				sendAcknowledgedMessage(createTask(StreamSendTask.CLOSEREQ, null, null), StreamSendTask.CLOSEREQ)
					.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(null);
					}	
				});
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called from message service.
	 *  
	 *  Uses: data
	 *  
	 *  @param data The new data.
	 *  @return
	 */
	public void addData(final int seqnumber, final byte[] dat)
	{
		scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(!con.isClosed())
				{
					System.out.println("received: "+seqnumber);
		
					// If packet is the next one deliver to stream
					// else store in map till the next one arrives
					int expseqno = getSequenceNumber()+1;
					if(seqnumber==expseqno)
					{
						forwardData(dat);
						
						// Forward possibly stored data
						byte[] nextdata = data.remove(new Integer(getSequenceNumber()+1));
						for(; nextdata!=null ;)
						{
							forwardData(nextdata);
							nextdata = data.remove(new Integer(getSequenceNumber()+1));
						}
					}
					else
					{
						Object old = data.put(new Integer(seqnumber), dat);
						// ack msg may be lost, repeat ack msg
						if(old!=null)
						{
							// todo: ack also more than one packet?
							sendDataAck(seqnumber, seqnumber, false);
						}
						if(data.size()>maxbuf)
						{
							System.out.println("Closing connection due to package loss: "+seqnumber+" :"+data.size());
							con.close();
							data.clear();
						}
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
		
		// Directly acknowledge when ackcnt packets have been received
		// or start time to acknowledge less packages in an interval.
		if(seqno%ackcnt==0 || seqno==lastseqno)
		{
			sendDataAck();
			
			// if last packet was received close this side
			if(seqno==lastseqno)
			{
				if(!con.isClosed())
					con.setClosed();
			}
		}
		else 
		{
			createDataTimer();
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
	public void createDataTimer()
	{
		// Test if packets have been received till creation
		if(datatimer==null && rseqno>lastack)
		{
			datatimer = ms.waitForRealDelay(acktimeout, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
	//						System.out.println("timer ack");
					sendDataAck();
					datatimer = null;
					createDataTimer();	
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
		// Only send acks if new packets have arrived.
		if(getSequenceNumber()>lastack)
		{
//			System.out.println("send ack: "+rseqno);
			// tuple contains seqno and stop flag
			sendDataAck(Math.max(0, rseqno-ackcnt), rseqno, isStop());
			lastack = rseqno;
		}
	}
	
	/**
	 * 
	 */
	protected void sendDataAck(int startseqno, int endseqno, boolean stop)
	{
		sendTask(createTask(StreamSendTask.ACKDATA, new AckInfo(startseqno, endseqno, stop), true, null));
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
	
}