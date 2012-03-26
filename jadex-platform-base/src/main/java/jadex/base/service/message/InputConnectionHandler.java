package jadex.base.service.message;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.codecs.ICodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class InputConnectionHandler extends AbstractConnectionHandler
{
	/** The last received sequence number. */
	protected int seqnumber;
	
	/** The buffer size after which new resends are sent. */
	protected int misscnt;

	/** The number of received elements after which an ack is sent. */
	protected int ackcnt;
	
	/** The maximum buffer size. */
	protected int maxbuf;
	
	/** The data. */
	protected Map<Integer, byte[]> data;
	
	/**
	 * 
	 */
	public InputConnectionHandler(MessageService ms)
	{
		super(ms);
		this.seqnumber = -1;
		this.misscnt = 10;
		this.ackcnt = 10;
		this.maxbuf = 1000;
		this.data = new HashMap<Integer, byte[]>();
	}

	/**
	 *  Called from message service.
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
			
			byte[] nextdata = this.data.get(new Integer(this.seqnumber));
			for(; nextdata!=null ; this.seqnumber++)
			{
				forwardData(nextdata);
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
			else if(this.data.size()%misscnt==0)
			{
				requestResend(seqnumber);
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	protected void forwardData(byte[] data)
	{
		seqnumber++;
		getInputConnection().addData(data);
		if(seqnumber%ackcnt==0)
			sendAck();
	}
	
	/**
	 *  Called by by itself.
	 */
	protected void sendAck()
	{
		System.out.println("send ack: "+seqnumber);
		sendTask(createTask(StreamSendTask.ACK, seqnumber, true, null));
	}
	
	/**
	 *  Called by itself.
	 */
	protected void requestResend(int newest)
	{
		sendTask(createTask(StreamSendTask.RESEND, getMissingPackets(newest), true, null));
	}
	
	/**
	 * 
	 */
	protected int[] getMissingPackets(int newest)
	{
		List<Integer> tmp = new ArrayList<Integer>();
		
		for(int i=seqnumber+1; i<=newest; i++)
		{
			if(!data.containsKey(new Integer(i)))
			{
				tmp.add(new Integer(i));
			}
		}
		
		int[] ret = new int[tmp.size()];
		for(int i=0; i<ret.length; i++)
			ret[i] = tmp.get(i).intValue();
		
		return ret;
	}
	
	/**
	 * 
	 */
	public InputConnection getInputConnection()
	{
		return (InputConnection)getConnection();
	}
}
