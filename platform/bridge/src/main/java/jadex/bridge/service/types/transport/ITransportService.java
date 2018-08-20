package jadex.bridge.service.types.transport;

import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

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
	 *  @param body Message body.
	 *  @return Transport priority, when sent. Failure does not need to be returned as message feature uses its own timeouts.
	 */
	public IFuture<Integer> sendMessage(@Reference IMsgHeader header, @Reference byte[] body);
}
