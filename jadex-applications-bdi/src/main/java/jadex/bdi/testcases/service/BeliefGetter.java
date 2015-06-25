package jadex.bdi.testcases.service;

import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Simple service that fetches a belief value.
 */
public class BeliefGetter extends BasicService implements IBeliefGetter
{
	//-------- attributes --------
	
	/** The agent's external access. */
	protected IExternalAccess agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a service.
	 */
	public BeliefGetter(IExternalAccess agent)
	{
		super(agent.getComponentIdentifier(), IBeliefGetter.class, null);
		this.agent = agent;
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
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("getter")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				ret.setResult(capa.getBeliefbase().getBelief(belname).getFact());
				return IFuture.DONE;
			}
		});
//		agent.getBeliefbase().getBeliefFact(belname).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
}
