package jadex.base.service.message;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class InputConnectionHandler
{
	/** The last received sequence number. */
	protected int seqnumber;
	
	/** The allowed open number of missing packets. */
	protected int misscnt;
	
	/** The set of requested resends. */
	
	/** The data. */
	protected Map<Integer, byte[]> data;
	
	/** The connection. */
	protected InputConnection con;
	
	/**
	 * 
	 */
	public InputConnectionHandler(InputConnection con)
	{
		this.con = con;
		this.seqnumber = -1;
		this.data = new HashMap<Integer, byte[]>();
	}
	
	/**
	 *  Called by itself.
	 */
	public void requestResend(int seqnumber)
	{
//		return con.sendTask(task);
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
			this.seqnumber++;
			con.addData(data);
			
			byte[] nextdata = this.data.get(new Integer(this.seqnumber));
			for(; nextdata!=null ; this.seqnumber++)
			{
				con.addData(nextdata);
				nextdata = this.data.get(new Integer(this.seqnumber));
			}
		}
		else
		{
			this.data.put(new Integer(seqnumber), data);
			if(this.data.size()%misscnt==0)
			{
				requestResend();
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	public void requestResend()
	{
//		con.createTask(type, content, seqnumber)
	}
}
