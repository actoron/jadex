package jadex.platform.service.transport.intravm;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.platform.service.transport.AbstractTransportAgent2;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent that implements TCP message transport.
 */
@Agent(name="intravm",
	autostart=Boolean3.FALSE,
	predecessors="jadex.platform.service.address.TransportAddressAgent",
	successors="jadex.platform.service.registryv2.SuperpeerClientAgent")
public class IntravmTransportAgent extends AbstractTransportAgent2<IntravmTransport.HandlerHolder>
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
