package jadex.platform.service.transport.tcp;

import java.util.Map;

import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class TcpTransportService implements ITransportService
{
	/**
	 *  Priority of the transport.
	 *  
	 *  @return Transport priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(1000);
	}
	
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
