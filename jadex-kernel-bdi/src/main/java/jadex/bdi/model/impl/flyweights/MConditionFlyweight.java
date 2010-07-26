package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMPlanTrigger;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MElementFlyweight.AgentInvocation;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class MConditionFlyweight extends MExpressionFlyweight implements IMCondition
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MConditionFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
