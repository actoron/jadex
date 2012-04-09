package jadex.micro.testcases.stream;

import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.commons.future.IFuture;

/**
 *  Service that provides a method with a stream return value.
 */
public interface IStreamService
{
	/**
	 *  Pass an input stream to the user.
	 *  @return The input stream.
	 */
	public IFuture<IInputConnection> getInputStream();

	/**
	 *  Pass an output stream to the user.
	 *  @return The input stream.
	 */
	public IFuture<IOutputConnection> getOutputStream();

	
//	/**
//	 *  Pass an output stream from the user.
//	 *  @param con The output stream.
//	 */
//	public IFuture<Void> passOutputStream(IOutputConnection con);

}
