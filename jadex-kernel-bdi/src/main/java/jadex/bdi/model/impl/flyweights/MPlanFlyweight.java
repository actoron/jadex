package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanTrigger;
import jadex.bdi.model.IMTrigger;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for plan model element.
 */
public class MPlanFlyweight extends MParameterElementFlyweight implements IMPlan
{
	//-------- constructors --------
	
	/**
	 *  Create a new plan flyweight.
	 */
	public MPlanFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the priority.
	 *  @return The priority.
	 */
	public int getPriority()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					integer = ((Integer)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_priority)).intValue();
				}
			};
			return invoc.integer;
		}
		else
		{
			return ((Integer)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_priority)).intValue();
		}
	}
	
	/**
	 *  Get the precondition.
	 *  @return The precondition.
	 */
	public IMExpression getPrecondition()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_precondition);
					if(handle!=null)
						object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			IMExpression ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_precondition);
			if(handle!=null)
				ret = new MExpressionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the context condition.
	 *  @return The context condition.
	 */
	public IMCondition getContextCondition()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_precondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_precondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the body.
	 *  @return The body.
	 */
	 // todo
//	public IMPlanBody getBody();
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IMTrigger getWaitqueue()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_waitqueue);
					if(handle!=null)
						object = new MTriggerFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMTrigger)invoc.object;
		}
		else
		{
			IMTrigger ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_waitqueue);
			if(handle!=null)
				ret = new MTriggerFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public IMPlanTrigger getTrigger()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_trigger);
					if(handle!=null)
						object = new MPlanTriggerFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMPlanTrigger)invoc.object;
		}
		else
		{
			IMPlanTrigger ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_trigger);
			if(handle!=null)
				ret = new MPlanTriggerFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
}
