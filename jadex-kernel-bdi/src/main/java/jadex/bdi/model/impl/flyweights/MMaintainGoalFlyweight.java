package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMMaintainGoal;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEMaintainGoal;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for maintain goal model element.
 */
public class MMaintainGoalFlyweight extends MGoalFlyweight implements IMMaintainGoal, IMEMaintainGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MMaintainGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the maintain condition.
	 *  @return The maintain condition.
	 */
	public IMCondition getMaintainCondition()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.maintaingoal_has_maintaincondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.maintaingoal_has_maintaincondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
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
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.maintaingoal_has_targetcondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.maintaingoal_has_targetcondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
}
