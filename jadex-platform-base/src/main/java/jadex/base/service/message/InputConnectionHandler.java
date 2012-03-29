package jadex.base.service.message;

import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class InputConnectionHandler extends AbstractConnectionHandler
{
	/** The last received sequence number. */
	protected int rseqno;
	
	/** The maximum buffer size. */
	protected int maxbuf;
	
	/** The data. */
	protected Map<Integer, byte[]> data;
	
	
	/** The last sequence number acknowledged. */
	protected int lastack;
	
	/** The max delay before an acknowledgement is received. */
	protected long acktimeout;
	
	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The current timer. */
	protected ITimer datatimer;
	

	/** Close flag (when a close message was received). */
	protected boolean closeflag;
		
	/** The close request (for resend). */
	protected StreamSendTask closereqtask;

	/** The close timer. */
	protected ITimer closereqtimer;

	/**
	 * 
	 */
	public InputConnectionHandler(MessageService ms)
	{
		super(ms);
		this.rseqno = -1;
		this.maxbuf = 1000;
		this.data = new HashMap<Integer, byte[]>();
	
		this.ackcnt = 10;
		this.acktimeout = 10000;
		this.lastack = -1;
	}

	/**
	 *  From initiator.
	 * 
	 *  Called when a close message was received.
	 *  participant acks and closes
	 */
	public void closeReceived()
	{
		// Remember that close message was received, close the connection and send an ack.
//		System.out.println("close received");
		closeflag = true;
		con.setClosed();
		sendTask(createTask(StreamSendTask.ACKCLOSE, null, null));
	}
	
	/**
	 *  From initiator.
	 *  
	 *  Called to signal that the close request was received.
	 */
	public void ackCloseRequestReceived()
	{
		System.out.println("received ack close req");
		closereqtask = null;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> doClose()
	{
		// Send a close request
		if(closeflag)
			return IFuture.DONE;
		
		if(closereqtask!=null)
			throw new RuntimeException("Must be only called once");
		
		closeflag = true;
		closereqtask = (StreamSendTask)createTask(StreamSendTask.CLOSEREQ, null, null);
		
		return sendTask(closereqtask);
	}
	
	/**
	 *  Called from message service.
	 *  
	 *  Uses: data
	 *  
	 *  @param data The new data.
	 *  @return
	 */
	public boolean addData(int seqnumber, byte[] data)
	{
		if(con.isClosed())
			return false;
		
		// If packet is the next one deliver to stream
		// else store in map till the next one arrives
		if(seqnumber==getSequenceNumber()+1)
		{
			forwardData(data);
			
			// Forward possibly stored data
			byte[] nextdata = this.data.get(new Integer(getSequenceNumber()));
			for(; nextdata!=null ;)
			{
				forwardData(nextdata);
				nextdata = this.data.get(new Integer(getSequenceNumber()));
			}
		}
		else
		{
			this.data.put(new Integer(seqnumber), data);
			if(this.data.size()>maxbuf)
			{
				System.out.println("Closing connection due to package loss: "+seqnumber+" :"+this.data.size());
				con.close();
				this.data.clear();
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	protected void forwardData(byte[] data)
	{
		System.out.println("forward data: "+SUtil.arrayToString(data));
		
		int seqno = getNextReceivedSequenceNumber();
		getInputConnection().addData(data);
		
		// Directly acknowledge when ackcnt packets have been received
		// or start time to acknowledge less packages in an interval.
		if(seqno%ackcnt==0)
		{
			sendAck();
		}
		else 
		{
			createTimer();
		}
	}
	
	/**
	 *  Called by by itself.
	 */
	protected void sendAck()
	{
		// Only send acks if new packets have arrived.
		if(getSequenceNumber()>lastack)
		{
			System.out.println("send ack: "+rseqno);
			sendTask(createTask(StreamSendTask.ACKCLOSEREQ, rseqno, true, null));
			lastack = rseqno;
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
	 * 
	 */
	// Synchronized with timer on this
	public synchronized void createTimer()
	{
		// Test if packets have been received till creation
		if(datatimer==null && rseqno>lastack)
			datatimer = ms.getClockService().createTimer(acktimeout, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				System.out.println("timer ack");
				sendAck();
				datatimer = null;
				createTimer();				
			}
		});
	}
	
	/**
	 * 
	 */
	public InputConnection getInputConnection()
	{
		return (InputConnection)getConnection();
	}
	
}