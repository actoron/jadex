package jadex.bridge.service.types.transport;

import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ITerminableFuture;

/**
 *  Interface for a transport service.
 *
 */
@Service(system=true)
public interface ITransportService
{
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param bheader Message header already encoded and encrypted for sending.
	 *  @param body Message body.
	 *  @return Transport priority, when sent. Failure does not need to be returned as message feature uses its own timeouts.
	 *  	Future is terminated by message feature, when another transport has sent the message.
	 */
	public ITerminableFuture<Integer> sendMessage(@Reference IMsgHeader header, @Reference byte[] bheader, @Reference byte[] body);
}
