package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanBody;
import jadex.bdi.model.IMPlanTrigger;
import jadex.bdi.model.IMTrigger;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMECondition;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMEPlan;
import jadex.bdi.model.editable.IMEPlanBody;
import jadex.bdi.model.editable.IMEPlanTrigger;
import jadex.bdi.model.editable.IMETrigger;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for plan model element.
 */
public class MPlanFlyweight extends MParameterElementFlyweight implements IMPlan, IMEPlan
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		
	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public IMPlanBody getBody()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_body);
					if(handle!=null)
						object = new MPlanBodyFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMPlanBody)invoc.object;
		}
		else
		{
			IMPlanBody ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_body);
			if(handle!=null)
				ret = new MPlanBodyFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Set the priority.
	 *  param priority	The priority.
	 */
	public void	setPriority(final int priority)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_priority, priority);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_priority, priority);
		}
	}
	
	/**
	 *  Create a precondition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The precondition.
	 */
	public IMEExpression	createPrecondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_dropcondition, mexp.getHandle());
					object	= mexp;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_dropcondition, mexp.getHandle());
			return mexp;
		}
	}
	
	/**
	 *  Create a context condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The context condition.
	 */
	public IMECondition createContextCondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_contextcondition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_contextcondition, mcond.getHandle());
			return mcond;
		}
	}
	
	/**
	 *  Create the body.
	 *  @param impl	The implementation (e.g. class or file name).
	 *  @param type	The plan body type (null for standard java plans).
	 *  @return The body.
	 */
	public IMEPlanBody createBody(final String impl, final String type)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	body	= getState().createObject(OAVBDIMetaModel.body_type);
					if(type!=null)
						getState().setAttributeValue(body, OAVBDIMetaModel.body_has_type, type);
					getState().setAttributeValue(body, OAVBDIMetaModel.body_has_impl, impl);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_body, body);
					
					object	= new MPlanBodyFlyweight(getState(), getScope(), body);
				}
			};
			return (IMEPlanBody)invoc.object;
		}
		else
		{
			Object	body	= getState().createObject(OAVBDIMetaModel.body_type);
			if(type!=null)
				getState().setAttributeValue(body, OAVBDIMetaModel.body_has_type, type);
			getState().setAttributeValue(body, OAVBDIMetaModel.body_has_impl, impl);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_body, body);
			
			return new MPlanBodyFlyweight(getState(), getScope(), body);
		}
	}
	
	/**
	 *  Create the waitqueue.
	 *  @return The waitqueue.
	 */
	public IMETrigger createWaitqueue()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	mtrig = getState().createObject(OAVBDIMetaModel.trigger_type);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_waitqueue, mtrig);
					object	= new MTriggerFlyweight(getState(), getScope(), mtrig);
				}
			};
			return (IMETrigger)invoc.object;
		}
		else
		{
			Object	mtrig = getState().createObject(OAVBDIMetaModel.trigger_type);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_waitqueue, mtrig);
			return new MTriggerFlyweight(getState(), getScope(), mtrig);
		}
	}
	
	/**
	 *  Create the trigger.
	 *  @return The trigger.
	 */
	public IMEPlanTrigger createTrigger()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	mtrig = getState().createObject(OAVBDIMetaModel.plantrigger_type);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_trigger, mtrig);
					object	= new MPlanTriggerFlyweight(getState(), getScope(), mtrig);
				}
			};
			return (IMEPlanTrigger)invoc.object;
		}
		else
		{
			Object	mtrig = getState().createObject(OAVBDIMetaModel.plantrigger_type);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plan_has_trigger, mtrig);
			return new MPlanTriggerFlyweight(getState(), getScope(), mtrig);
		}
	}
}
