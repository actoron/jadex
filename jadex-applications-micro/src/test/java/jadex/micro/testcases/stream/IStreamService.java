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

	/**
	 *  Pass an Input stream to the user.
	 *  @return The Input stream.
	 */
	public IFuture<Long> passInputStream(IInputConnection con);
	
	/**
	 *  Pass an output stream from the user.
	 *  @param con The output stream.
	 */
	public IFuture<Long> passOutputStream(IOutputConnection con);
	
	
//	/**
//	 *  Pass an input stream to the user.
//	 *  @return The input stream.
//	 */
//	@SecureTransmission
//	public IFuture<IInputConnection> getSecureInputStream();
//
//	/**
//	 *  Pass an output stream to the user.
//	 *  @return The input stream.
//	 */
//	@SecureTransmission
//	public IFuture<IOutputConnection> getSecureOutputStream();
//
//	/**
//	 *  Pass an Input stream to the user.
//	 *  @return The Input stream.
//	 */
//	@SecureTransmission
//	public IFuture<Long> passSecureInputStream(IInputConnection con);
//	
//	/**
//	 *  Pass an output stream from the user.
//	 *  @param con The output stream.
//	 */
//	@SecureTransmission
//	public IFuture<Long> passSecureOutputStream(IOutputConnection con);
	
}
