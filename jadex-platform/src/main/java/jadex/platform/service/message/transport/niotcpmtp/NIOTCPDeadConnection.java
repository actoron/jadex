package jadex.platform.service.message.transport.niotcpmtp;

import java.io.Closeable;
import java.io.IOException;

import jadex.platform.service.message.transport.niotcpmtp.SelectorThread.Cleaner;

/**
 *  Dead connection identifier.
 */
public class NIOTCPDeadConnection	implements Closeable
{
	//-------- attributes --------
	
	/** The dead connection start time. */
	protected long deadtime;
	
	/** The cleaner. */
	protected Cleaner	cleaner;

	//-------- constructors --------
	
	/**
	 *  Create a new dead connection.
	 */
	public NIOTCPDeadConnection(Cleaner cleaner)
	{
		this.deadtime = System.currentTimeMillis();
		this.cleaner	= cleaner;
	}
	
	//-------- methods --------

	/**
	 *  Test if it should be retried to reestablish the connection.
	 *  @return True, if should retry.
	 */
	public boolean shouldRetry()
	{
		return System.currentTimeMillis()>deadtime+NIOTCPTransport.DEADSPAN;
	}
	
	/**
	 *  Close the connection.
	 */
	public void close() throws IOException
	{
		cleaner.remove();
	}
}
