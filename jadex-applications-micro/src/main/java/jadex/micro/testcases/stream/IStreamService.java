package jadex.micro.testcases.stream;

import java.io.InputStream;

import jadex.commons.future.IFuture;

/**
 *  Service that provides a method with a stream return value.
 */
public interface IStreamService
{
	/**
	 *  Pass an input stream to the user.
	 *  @return The output stream.
	 */
	public IFuture<InputStream> getInputStream();
}
