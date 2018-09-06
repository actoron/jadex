package jadex.platform.service.transport.intravm;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Autostart;
import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent that implements TCP message transport.
 */
@Agent(autostart=@Autostart(value=Boolean3.FALSE, name="intravm", predecessors="jadex.platform.service.address.TransportAddressAgent"))
public class IntravmTransportAgent extends AbstractTransportAgent<IntravmTransport>
{
	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the transport implementation
	 */
	public ITransport<IntravmTransport>	createTransportImpl()
	{
		return new IntravmTransport();
	}
}
