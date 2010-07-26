package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMMetaGoal;
import jadex.bdi.model.IMMetaGoalTrigger;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MElementFlyweight.AgentInvocation;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for query goal. 
 */
public class MMetaGoalFlyweight extends MGoalFlyweight implements IMMetaGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MMetaGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public IMMetaGoalTrigger getTrigger()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.metagoal_has_trigger);
					if(handle!=null)
						object = new MMetaGoalTriggerFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMMetaGoalTrigger)invoc.object;
		}
		else
		{
			IMMetaGoalTrigger ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.metagoal_has_trigger);
			if(handle!=null)
				ret = new MMetaGoalTriggerFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
}
