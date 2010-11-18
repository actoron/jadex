package jadex.bdi.testcases.service;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
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
	public IFuture getFact(final String belname)
	{
		final Future ret = new Future();
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				ret.setResult(bia.getBeliefbase().getBelief(belname).getFact());
				return null;
			}
		});
//		agent.getBeliefbase().getBeliefFact(belname).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
}
