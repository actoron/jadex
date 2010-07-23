package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for planbase model.
 */
public class MPlanbaseFlyweight extends MElementFlyweight implements IMPlanbase 
{
 	//-------- constructors --------
 	
 	/**
 	 *  Create a new planbase flyweight.
 	 */
 	private MPlanbaseFlyweight(IOAVState state, Object scope)
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
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
 		{
 			AgentInvocation invoc = new AgentInvocation()
 			{
 				public void run()
 				{
 					Collection bels = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_plans);
 					IMPlan[] ret = new IMPlan[bels==null? 0: bels.size()];
 					if(bels!=null)
 					{
 						int i=0;
 						for(Iterator it=bels.iterator(); it.hasNext(); )
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
 			Collection bels = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_plans);
 			IMPlan[] ret = new IMPlan[bels==null? 0: bels.size()];
 			if(bels!=null)
 			{
 				int i=0;
 				for(Iterator it=bels.iterator(); it.hasNext(); )
 				{
 					ret[i++] = new MPlanFlyweight(getState(), getScope(), it.next());
 				}
 			}
 			return ret;
 		}
	}
}
