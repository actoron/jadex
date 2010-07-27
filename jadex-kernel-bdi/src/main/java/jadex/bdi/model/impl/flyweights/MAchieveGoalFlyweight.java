package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMAchieveGoal;
import jadex.bdi.model.IMCondition;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEAchieveGoal;
import jadex.bdi.model.editable.IMECondition;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for achieve goal model element.
 */
public class MAchieveGoalFlyweight extends MGoalFlyweight implements IMAchieveGoal, IMEAchieveGoal
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
	
	/**
	 *  Create the target condition.
	 *  @return The target condition.
	 */
	public IMECondition createTargetCondition(final String content, final String lang)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(content, lang, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.achievegoal_has_targetcondition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(content, lang, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.achievegoal_has_targetcondition, mcond.getHandle());
			return mcond;
		}
	}
}
