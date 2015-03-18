package jadex.bdi.runtime.impl.flyweights;

import java.util.Collection;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.IMPlan;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MPlanFlyweight;
import jadex.bdi.model.impl.flyweights.MPlanbaseFlyweight;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight.AgentInvocation;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
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
		BDIInterpreter ip = BDIAgentFeature.getInterpreter(state);
		PlanbaseFlyweight ret = (PlanbaseFlyweight)ip.getFlyweightCache(IPlanbase.class, new Tuple(IPlanbase.class, scope));
		if(ret==null)
		{
			ret = new PlanbaseFlyweight(state, scope);
			ip.putFlyweightCache(IPlanbase.class, new Tuple(IPlanbase.class, scope), ret);
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getPlans(getState(), getHandle());
				}
			};
			return (IPlan[])invoc.object;
		}
		else
		{
			return (IPlan[])SFlyweightFunctionality.getPlans(getState(), getHandle());
		}
	}

	/**
	 *  Get all plans of a specified type (=model element name).
	 *  @param type The plan type.
	 *  @return All plans of the specified type.
	 */
	public IPlan[] getPlans(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getPlans(getState(), getHandle(), type);
				}
			};
			return (IPlan[])invoc.object;
		}
		else
		{
			return (IPlan[])SFlyweightFunctionality.getPlans(getState(), getHandle(), type);
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
	
	/**
	 *  Create a plan instance.
	 */
	public IPlan createPlan(final IMPlan mplan)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
//					Collection	bindings = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_bindings);
					Object plan = PlanRules.instantiatePlan(getState(), getScope(), ((MPlanFlyweight)mplan).getHandle(), null, null, null, null, null);
//					getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_plan, plan);
					object = PlanFlyweight.getPlanFlyweight(getState(), getScope(), plan);
				}
			};
			return (IPlan)invoc.object;
		}
		else
		{
//			Collection	bindings = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_bindings);
			Object plan = PlanRules.instantiatePlan(getState(), getScope(), ((MPlanFlyweight)mplan).getHandle(), null, null, null, null, null);
//			getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_plan, plan);
			return PlanFlyweight.getPlanFlyweight(getState(), getScope(), plan);
		}
	}
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param type	The goal type.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(final String type, final IPlanListener listener)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
