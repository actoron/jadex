package jadex.base.service.message;

import jadex.commons.future.IFuture;

import java.util.Map;

/**
 * 
 */
public class OutputConnectionHandler
{
	/** The output data. */
	protected Map<Integer, StreamSendTask> data;

	/** The connection. */
	protected AbstractConnection con;
	
	/**
	 * 
	 */
	public OutputConnectionHandler(AbstractConnection con)
	{
		this.con = con;
	}
	
	/**
	 *  Called from connection.
	 */
	public IFuture<Void> send(StreamSendTask task)
	{
		Integer sq = task.getSequenceNumber();
		if(sq==null)
			throw new RuntimeException("Data send task has no sequence number.");
		data.put(sq, task);
		return con.sendTask(task);
	}
	
	/**
	 *  Called from message service.
	 */
	public IFuture<Void> resend(int seqnumber)
	{
		StreamSendTask task = data.get(new Integer(seqnumber));
		if(task==null)
			throw new RuntimeException("Resend not possible, data not found.");
		return con.sendTask(task);
	}
	
	/**
	 *  Called from message service.
	 */
	public void ack(int seqnumber)
	{
		StreamSendTask task = data.remove(new Integer(seqnumber));
		if(task==null)
			throw new RuntimeException("Ack not possible, data not found.");
	}

	/**
	 *  Get the connection.
	 *  @return the connection.
	 */
	public AbstractConnection getConnection()
	{
		return con;
	}
	
}
