package jadex.bridge;

import java.io.InputStream;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for output connection.
 */
public interface IOutputConnection extends IConnection
{
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data);
	
	/**
	 *  Flush the data.
	 */
	public void flush();
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written. Provides a value of how much data should be given to the connection for best performance.
	 */
	public IFuture<Integer> waitForReady();
	
	/**
	 *  Write all data from input stream to the connection.
	 *  The result is an intermediate future that reports back the size that was written.
	 *  It can also be used to terminate sending.
	 *  @param is The input stream.
	 */
	public ISubscriptionIntermediateFuture<Long> writeFromInputStream(final InputStream is, final IExternalAccess component);
	
}
