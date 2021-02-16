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
	
//	/**
//	 *  Heisendebug handshake issue
//	 */
//	@Override
//	public IFuture<Void> startService()
//	{
//		// Heisenbug test if not simulating (TODO: test also when simulating?)
//		if(!SSimulation.isSimulating(getAccess()) && !SSimulation.isBisimulating(getAccess()))
//		{
//	//		return agent.waitForDelay(400, ia -> super.startService());	// works
//	//		return agent.waitForDelay(450, ia -> super.startService());	// heisenbug
//			return agent.waitForDelay(550, ia -> super.startService());	// broken
//	//		return agent.waitForDelay(700, ia -> super.startService());	// broken
//	//		return agent.waitForDelay(800, ia -> super.startService());	// broken
//	//		return agent.waitForDelay(900, ia -> super.startService());	// broken
//	//		return agent.waitForDelay(1000, ia -> super.startService());	// broken
//	//		return agent.waitForDelay(5000, ia -> super.startService());	// broken
//	//		return agent.waitForDelay(3000, ia -> super.startService());	// works
//	//		return agent.waitForDelay(300, ia -> super.startService());	// works
//	//		return agent.waitForDelay(5, ia -> super.startService());	// works
//		}
//		else
//		{
//			return super.startService();	// works
//		}
//	}
}
