package jadex.base.service.message;

import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.SUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class InputConnectionHandler extends AbstractConnectionHandler
{
	/** The last received sequence number. */
	protected int seqnumber;
	
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
	protected ITimer timer;

	
	/**
	 * 
	 */
	public InputConnectionHandler(MessageService ms)
	{
		super(ms);
		this.seqnumber = -1;
		this.maxbuf = 1000;
		this.data = new HashMap<Integer, byte[]>();
	
		this.ackcnt = 10;
		this.acktimeout = 10000;
		this.lastack = -1;
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
		if(seqnumber==this.seqnumber+1)
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
//			else if(this.data.size()%misscnt==0)
//			{
//				requestResend(seqnumber);
//			}
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	protected void forwardData(byte[] data)
	{
		System.out.println("forward data: "+SUtil.arrayToString(data));
		
		int seqno = ++seqnumber;
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
		if(seqnumber>lastack)
		{
			System.out.println("send ack: "+seqnumber);
			sendTask(createTask(StreamSendTask.ACK, seqnumber, true, null));
			lastack = seqnumber;
		}
	}
	
	/**
	 *  Get the sequence number.
	 *  @return the sequence number.
	 */
	public int getSequenceNumber()
	{
		return seqnumber;
	}

	/**
	 * 
	 */
	// Synchronized with timer on this
	public synchronized void createTimer()
	{
		// Test if packets have been received till creation
		if(timer==null && seqnumber>lastack)
			timer = ms.getClockService().createTimer(acktimeout, new TimedObject());
	}
	
	/**
	 * 
	 */
	public InputConnection getInputConnection()
	{
		return (InputConnection)getConnection();
	}
	
	/**
	 * 
	 */
	public class TimedObject implements ITimedObject
	{
		/**
		 * 
		 */
		public void timeEventOccurred(long currenttime)
		{
			System.out.println("timer ack");
			sendAck();
			timer = null;
			createTimer();
		}
	}
}
