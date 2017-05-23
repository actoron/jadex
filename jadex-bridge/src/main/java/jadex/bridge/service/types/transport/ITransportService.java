package jadex.bridge.service.types.transport;

import java.util.Map;
import jadex.commons.future.IFuture;

/**
 *  Interface for a transport service.
 *
 */
public interface ITransportService
{
	/**
	 *  Priority of the transport.
	 *  
	 *  @return Transport priority.
	 */
	public IFuture<Integer> getPriority();
	
	/**
	 *  Checks if the transport is ready.
	 * 
	 *  @param header <essage header.
	 *  @return Null, when ready.
	 */
	public IFuture<Void> isReady(Map<String, Object> header);
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Null, when sent.
	 */
	public IFuture<Void> sendMessage(Map<String, Object> header, byte[] body);
}
