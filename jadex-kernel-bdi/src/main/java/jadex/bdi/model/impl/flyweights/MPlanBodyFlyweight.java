package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMPlanBody;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEPlanBody;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for a plan body.
 */
//Hack!!! Shouldn't be expression.
public class MPlanBodyFlyweight extends MExpressionFlyweight implements IMPlanBody, IMEPlanBody
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MPlanBodyFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the body type (e.g. 'standard').
	 */
	public String	getType()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string	= (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_type);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_type);
		}
	}

	/**
	 *  Get the body implementation (e.g. file name).
	 */
	public String	getImplementation()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string	= (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_impl);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_impl);
		}
	}

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
	 *  Set the body type (e.g. 'standard').
	 *  @param type	The type. 
	 */
	public void	setType(final String type)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_type, type);
				}
			};
		}
		else
		{
			getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_type, type);
		}
	}

	/**
	 *  Set the body implementation (e.g. file name).
	 *  @param impl	The implementation.
	 */
	public void	setImplementation(final String impl)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_impl, impl);
				}
			};
		}
		else
		{
			getState().getAttributeValue(getHandle(), OAVBDIMetaModel.body_has_impl, impl);
		}
	}
}
