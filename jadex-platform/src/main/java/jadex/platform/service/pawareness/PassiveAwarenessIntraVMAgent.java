package jadex.platform.service.pawareness;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.service.types.address.TransportAddress;
import jadex.micro.annotation.Agent;

/**
 *  Implements passive awareness via shared memory.
 */
@Agent
public class PassiveAwarenessIntraVMAgent	extends PassiveAwarenessBaseAgent
{
	//-------- constants --------
	
	/** The started discovery agents. */
	protected static final Set<PassiveAwarenessIntraVMAgent>	discoveries	= Collections.synchronizedSet(new HashSet<PassiveAwarenessIntraVMAgent>());

	
	//-------- agent lifecycle --------

	/**
	 *  At startup create a multicast socket for listening.
	 */
	@Override
	public void	start() throws Exception
	{
		discoveries.add(this);
		super.start();
	}
	
	@Override
	public void shutdown()	throws Exception
	{
		discoveries.remove(this);
		super.shutdown();
	}
	
	//-------- methods --------
	
	/**
	 *  Send the info to other platforms.
	 *  @param source	If set, send only to source as provided in discovered().
	 */
	@Override
	protected void	doSendInfo(List<TransportAddress> addresses, Object source) throws Exception
	{
		if(source!=null)
		{
			((PassiveAwarenessBaseAgent)source).discovered(addresses, null);	
		}
		else
		{
			PassiveAwarenessIntraVMAgent[]	agents	= discoveries.toArray(new PassiveAwarenessIntraVMAgent[0]);
			for(PassiveAwarenessIntraVMAgent agent: agents)
			{
				agent.discovered(addresses, this);
			}
		}
	}	
}
