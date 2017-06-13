package jadex.bridge.service.types.transport;

import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Interface for a transport service.
 *
 */
@Service
public interface ITransportService
{	
	/**
	 *  Checks if the transport is ready.
	 * 
	 *  @param header Message header.
	 *  @return Transport priority, when ready
	 */
	public IFuture<Integer> isReady(IMsgHeader header);
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(IMsgHeader header, byte[] body);
}
