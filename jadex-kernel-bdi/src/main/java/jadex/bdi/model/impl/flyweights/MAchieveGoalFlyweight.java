package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMAchieveGoal;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for achieve goal model element.
 */
public class MAchieveGoalFlyweight extends MGoalFlyweight implements IMAchieveGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MAchieveGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the target condition.
	 *  @return The target condition.
	 */
	public IMCondition getTargetCondition()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.achievegoal_has_targetcondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.achievegoal_has_targetcondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
}
