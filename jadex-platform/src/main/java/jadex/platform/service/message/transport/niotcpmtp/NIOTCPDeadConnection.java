package jadex.platform.service.message.transport.niotcpmtp;

import java.io.Closeable;
import java.io.IOException;

/**
 *  Dead connection identifier.
 */
public class NIOTCPDeadConnection	implements Closeable
{
	//-------- constants --------
	
	/** The time span for which this connection is dead. */
	public static long DEADSPAN = 60000;
	
	//-------- attributes --------
	
	/** The dead connection start time. */
	protected long deadtime;
	
	//-------- constructors --------
	
	/**
	 *  Create a new dead connection.
	 */
	public NIOTCPDeadConnection()
	{
		this.deadtime = System.currentTimeMillis();
	}
	
	//-------- methods --------

	/**
	 *  Test if it should be retried to reestablish the connection.
	 *  @return True, if should retry.
	 */
	public boolean shouldRetry()
	{
		return System.currentTimeMillis()>deadtime+DEADSPAN;
	}
	
	/**
	 *  Close the connection.
	 */
	public void close() throws IOException
	{
	}
}
