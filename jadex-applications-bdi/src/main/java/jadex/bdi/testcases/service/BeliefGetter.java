package jadex.bdi.testcases.service;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;

/**
 *  Simple service that fetches a belief value.
 */
public class BeliefGetter extends BasicService implements IBeliefGetter
{
	//-------- attributes --------
	
	/** The agent's external access. */
	protected IBDIExternalAccess agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a service.
	 */
	public BeliefGetter(IExternalAccess agent)
	{
		super(agent.getServiceProvider().getId(), IBeliefGetter.class, null);
		this.agent = (IBDIExternalAccess)agent;
	}
	
	//-------- methods --------

	/**
	 *  Get the fact of a belief.
	 *  @param belname The belief name.
	 *  @return The fact.
	 */
	public IFuture getFact(String belname)
	{
		final Future ret = new Future();
		agent.getBeliefbase().getBeliefFact(belname).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
}
