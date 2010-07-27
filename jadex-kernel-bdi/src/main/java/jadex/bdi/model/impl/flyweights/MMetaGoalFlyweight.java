package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMMetaGoal;
import jadex.bdi.model.IMMetaGoalTrigger;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEMetaGoal;
import jadex.bdi.model.editable.IMEMetaGoalTrigger;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for query goal. 
 */
public class MMetaGoalFlyweight extends MGoalFlyweight implements IMMetaGoal, IMEMetaGoal
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
	
	/**
	 *  Create the trigger.
	 *  @return The trigger.
	 */
	public IMEMetaGoalTrigger createTrigger()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	mtrig = getState().createObject(OAVBDIMetaModel.metagoaltrigger_type);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.metagoal_has_trigger, mtrig);
					object	= new MMetaGoalTriggerFlyweight(getState(), getScope(), mtrig);
				}
			};
			return (IMEMetaGoalTrigger)invoc.object;
		}
		else
		{
			Object	mtrig = getState().createObject(OAVBDIMetaModel.metagoaltrigger_type);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.metagoal_has_trigger, mtrig);
			return new MMetaGoalTriggerFlyweight(getState(), getScope(), mtrig);
		}
	}
}
