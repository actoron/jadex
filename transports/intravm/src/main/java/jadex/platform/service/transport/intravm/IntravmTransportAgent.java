package jadex.platform.service.transport.intravm;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent that implements TCP message transport.
 */
@Agent(name="intravm", autostart=Boolean3.FALSE)
public class IntravmTransportAgent extends AbstractTransportAgent<IntravmTransport.HandlerHolder>
{
	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the transport implementation
	 */
	public ITransport<IntravmTransport.HandlerHolder> createTransportImpl()
	{
		return new IntravmTransport();
	}
}
