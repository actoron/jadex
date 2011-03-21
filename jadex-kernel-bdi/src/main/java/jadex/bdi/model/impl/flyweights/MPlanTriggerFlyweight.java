package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMPlanTrigger;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMECondition;
import jadex.bdi.model.editable.IMEPlanTrigger;
import jadex.bdi.model.editable.IMETriggerReference;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class MPlanTriggerFlyweight extends MTriggerFlyweight implements IMPlanTrigger, IMEPlanTrigger
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MPlanTriggerFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the goal events.
	 */
	public IMTriggerReference[]	getGoals()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.plantrigger_has_goals);
					IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMTriggerReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.plantrigger_has_goals);
			IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the trigger condition.
	 */
	public IMCondition	getCondition()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plantrigger_has_condition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.plantrigger_has_condition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}

	/**
	 *  Create a goal event.
	 *  @param reference	The referenced goal.
	 */
	public IMETriggerReference	createGoal(final String reference)
	{
		if(isExternalThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
					getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.plantrigger_has_goals, mtr);
					object	= new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
				}
			};
			return (IMETriggerReference)invoc.object;
		}
		else
		{
			Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
			getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.plantrigger_has_goals, mtr);
			return new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
		}
		
	}
	
	/**
	 *  Create the trigger condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The trigger condition.
	 */
	public IMECondition	createCondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plantrigger_has_condition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.plantrigger_has_condition, mcond.getHandle());
			return mcond;
		}
	}
}
