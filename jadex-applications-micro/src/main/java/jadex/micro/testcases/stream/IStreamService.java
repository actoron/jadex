package jadex.micro.testcases.stream;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.annotation.Reference;
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
	public IFuture<IInputConnection> getInputStream();
}
