package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MPlanbaseFlyweight;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

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
					object = SFlyweightFunctionality.getPlans(getState(), getHandle(), false);
				}
			};
			return (IPlan[])invoc.object;
		}
		else
		{
			return (IPlan[])SFlyweightFunctionality.getPlans(getState(), getHandle(), false);
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
					object = SFlyweightFunctionality.getPlans(getState(), getHandle(), false, type);
				}
			};
			return (IPlan[])invoc.object;
		}
		else
		{
			return (IPlan[])SFlyweightFunctionality.getPlans(getState(), getHandle(), false, type);
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
					Object mplan = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
					addEventListener(listener, mplan);
				}
			};
		}
		else
		{
			Object mplan = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
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
					Object mplan = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
					removeEventListener(listener, mplan, false);
				}
			};
		}
		else
		{
			Object mplan = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
			removeEventListener(listener, mplan, false);
		}
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
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
	}
}
