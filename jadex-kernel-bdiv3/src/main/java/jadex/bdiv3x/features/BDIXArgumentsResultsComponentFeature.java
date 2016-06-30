package jadex.bdiv3x.features;

import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3x.BDIXModel;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.ArgumentsResultsComponentFeature;
import jadex.commons.future.IFuture;

/**
 *  Extension to write back result beliefs on agent shutdown.
 */
public class BDIXArgumentsResultsComponentFeature extends ArgumentsResultsComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public BDIXArgumentsResultsComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	@Override
	protected void initDefaultArguments()
	{
		// NOP -> default arguments handled by beliefs.
	}
		
	@Override
	protected void initDefaultResults()
	{
		// NOP -> default results handled by beliefs.
	}
		
	/**
	 *  Set result values from beliefs.
	 */
	public IFuture<Void> shutdown()
	{
		IBDIXAgentFeature	bdif	= getComponent().getComponentFeature(IBDIXAgentFeature.class);
		BDIXModel	model	= (BDIXModel)bdif.getModel();
		for(MBelief mbel: model.getCapability().getBeliefs())
		{
			if(model.getCapability().getResultMappings().containsKey(mbel.getName()) && mbel.getEvaluationMode()==EvaluationMode.PULL)
			{
				Object	val;
				if(mbel.isMulti(null))
				{
					val	= bdif.getBeliefbase().getBeliefSet(mbel.getName()).getFacts();
				}
				else
				{
					val	= bdif.getBeliefbase().getBelief(mbel.getName()).getFact();
				}
				getResults().put(model.getCapability().getResultMappings().get(mbel.getName()), val);
			}
		}
		return super.shutdown();
	}
}
