package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *  Flyweight for the plan base.
 */
public class PlanbaseFlyweight extends ElementFlyweight implements IPlanbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new planbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private PlanbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static PlanbaseFlyweight getPlanbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		PlanbaseFlyweight ret = (PlanbaseFlyweight)ip.getFlyweightCache(IPlanbase.class).get(new Tuple(IPlanbase.class, scope));
		if(ret==null)
		{
			ret = new PlanbaseFlyweight(state, scope);
			ip.getFlyweightCache(IPlanbase.class).put(new Tuple(IPlanbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- IPlanbase interface --------
	
	/**
	 *  Get all running plans of this planbase.
	 *  @return The plans.
	 */
	public IPlan[] getPlans()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IPlan[]	ret;
					Collection	plans	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_plans);
					if(plans!=null)
					{
						ret = new IPlan[plans.size()];
						int i=0;
						for(Iterator it=plans.iterator(); it.hasNext(); i++)
						{
							ret[i] = PlanFlyweight.getPlanFlyweight(getState(), getHandle(), it.next());
						}
					}
					else
					{
						ret	= new IPlan[0];
					}
					
					object = ret;
				}
			};
			return (IPlan[])invoc.object;
		}
		else
		{
			IPlan[]	ret;
			Collection	plans	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_plans);
			if(plans!=null)
			{
				ret = new IPlan[plans.size()];
				int i=0;
				for(Iterator it=plans.iterator(); it.hasNext(); i++)
				{
					ret[i] = PlanFlyweight.getPlanFlyweight(getState(), getHandle(), it.next());
				}
			}
			else
			{
				ret	= new IPlan[0];
			}
			
			return ret;
		}
	}

	/**
	 *  Get all plans of a specified type (=model element name).
	 *  @param type The plan type.
	 *  @return All plans of the specified type.
	 */
	public IPlan[] getPlans(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					List ret = null;
					Collection	plans	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_plans);
					if(plans!=null)
					{
						ret = new ArrayList();
						for(Iterator it=plans.iterator(); it.hasNext(); )
						{
							Object rplan = it.next();
							Object mplan = getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
							String tname = (String)getState().getAttributeValue(mplan, OAVBDIMetaModel.modelelement_has_name);
							if(tname.equals(type))
								ret.add(PlanFlyweight.getPlanFlyweight(getState(), getHandle(), rplan));
						}
					}
					
					object = ret==null? new IPlan[0]: (IPlan[])ret.toArray(new IPlan[ret.size()]);
				}
			};
			return (IPlan[])invoc.object;
		}
		else
		{
			List ret = null;
			Collection	plans	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_plans);
			if(plans!=null)
			{
				ret = new ArrayList();
				for(Iterator it=plans.iterator(); it.hasNext(); )
				{
					Object rplan = it.next();
					Object mplan = getState().getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
					String tname = (String)getState().getAttributeValue(mplan, OAVBDIMetaModel.modelelement_has_name);
					if(tname.equals(type))
						ret.add(PlanFlyweight.getPlanFlyweight(getState(), getHandle(), rplan));
				}
			}
			
			return ret==null? new IPlan[0]: (IPlan[])ret.toArray(new IPlan[ret.size()]);
		}
	}

	/**
	 *  Get a plan by name.
	 *  @param name	The plan name.
	 *  @return The plan with that name (if any).
	 * /
	public IPlan	getPlan(String name)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Register a new plan.
	 *  @param mplan The new plan model.
	 * /
	public void registerPlan(IMPlan mplan)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister a plan.
	 *  @param mplan The plan model.
	 * /
	public void deregisterPlan(IMPlan mplan)
	{
		throw new UnsupportedOperationException();
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param type	The goal type.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(final String type, final IPlanListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mplan = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_plans, type);
					if(mplan==null)
						throw new RuntimeException("Plan not found: "+type);
					
					addEventListener(listener, mplan);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mplan = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_plans, type);
			if(mplan==null)
				throw new RuntimeException("Plan not found: "+type);
			
			addEventListener(listener, mplan);
		}
	}
	
	/**
	 *  Remove a goal listener.
	 *  @param type	The goal type.
	 *  @param listener The goal listener.
	 */
	public void removePlanListener(final String type, final IPlanListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mplan = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_plans, type);
					if(mplan==null)
						throw new RuntimeException("Plan not found: "+type);
					
					removeEventListener(listener, mplan, false);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mplan = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_plans, type);
			if(mplan==null)
				throw new RuntimeException("Plan not found: "+type);
			
			removeEventListener(listener, mplan, false);
		}
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 * /
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MPlanbaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MPlanbaseFlyweight(getState(), mscope);
		}
	}*/
}
