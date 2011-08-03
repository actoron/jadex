package jadex.base.service.awareness.discovery;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.micro.IPojoMicroAgent;

/**
 * 
 */
public class DiscoveryService	implements IDiscoveryService
{
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The send handler. */
	protected SendHandler sender;
	
	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
//		System.out.println("setDelay: "+delay+" "+getComponentIdentifier());
//		if(this.delay>=0 && delay>0)
//			scheduleStep(send);
		if(getDiscoveryAgent().getDelay()!=delay)
		{
			getDiscoveryAgent().setDelay(delay);
			sender.startSendBehavior();
		}
	}
	
	/**
	 *  Set the fast awareness flag.
	 *  @param fast The fast flag.
	 */
	public void setFast(boolean fast)
	{
		getDiscoveryAgent().setFast(fast);
	}
	
	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
		getDiscoveryAgent().setIncludes(includes);
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		getDiscoveryAgent().setExcludes(excludes);
	}
	
	/**
	 *  Get the discovery agent.
	 */
	protected DiscoveryAgent getDiscoveryAgent()
	{
		return (DiscoveryAgent)((IPojoMicroAgent)agent).getPojoAgent();
	}
}
