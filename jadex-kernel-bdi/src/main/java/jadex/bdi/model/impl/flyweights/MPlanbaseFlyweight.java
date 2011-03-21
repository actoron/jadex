package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEPlan;
import jadex.bdi.model.editable.IMEPlanbase;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for planbase model.
 */
public class MPlanbaseFlyweight extends MElementFlyweight implements IMPlanbase, IMEPlanbase
{
 	//-------- constructors --------
 	
 	/**
 	 *  Create a new planbase flyweight.
 	 */
 	public MPlanbaseFlyweight(IOAVState state, Object scope)
 	{
 		super(state, scope, scope);
 	}
 	
 	//-------- methods --------

 	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMPlan getPlan(final String name)
	{
		if(isExternalThread())
 		{
 			AgentInvocation invoc = new AgentInvocation(name)
 			{
 				public void run()
 				{
 					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_plans, name);
 					if(handle==null)
 						throw new RuntimeException("Plan not found: "+name);
 					object = new MPlanFlyweight(getState(), getScope(), handle);
 				}
 			};
 			return (IMPlan)invoc.object;
 		}
 		else
 		{
 			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_plans, name);
 			if(handle==null)
 				throw new RuntimeException("Plan not found: "+name);
 			return new MPlanFlyweight(getState(), getScope(), handle);
 		}
	}

	/**
	 *  Returns all beliefs.
	 *  @return All beliefs.
	 */
	public IMPlan[] getPlans()
	{
		if(isExternalThread())
 		{
 			AgentInvocation invoc = new AgentInvocation()
 			{
 				public void run()
 				{
 					Collection plans = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_plans);
 					IMPlan[] ret = new IMPlan[plans==null? 0: plans.size()];
 					if(plans!=null)
 					{
 						int i=0;
 						for(Iterator it=plans.iterator(); it.hasNext(); )
 						{
 							ret[i++] = new MPlanFlyweight(getState(), getScope(), it.next());
 						}
 					}
 					object = ret;
 				}
 			};
 			return (IMPlan[])invoc.object;
 		}
 		else
 		{
 			Collection plans = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_plans);
 			IMPlan[] ret = new IMPlan[plans==null? 0: plans.size()];
 			if(plans!=null)
 			{
 				int i=0;
 				for(Iterator it=plans.iterator(); it.hasNext(); )
 				{
 					ret[i++] = new MPlanFlyweight(getState(), getScope(), it.next());
 				}
 			}
 			return ret;
 		}
	}
	
	/**
	 *  Create a plan with a name.
	 *  @param name	The plan name.
	 */
	public IMEPlan createPlan(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.plan_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_plans, elem);
					object = new MPlanFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEPlan)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.plan_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_plans, elem);
			return new MPlanFlyweight(getState(), getScope(), elem);
		}
	}
}
