package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MPlanFlyweight;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IWaitqueue;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for plans.
 */
public class PlanFlyweight extends ParameterElementFlyweight implements IPlan
{
	//-------- constructors --------
	
	/**
	 *  Create a new plan flyweight.
	 */
	private PlanFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static PlanFlyweight getPlanFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		PlanFlyweight ret = (PlanFlyweight)ip.getFlyweightCache(IPlan.class, new Tuple(IPlan.class, handle));
		if(ret==null)
		{
			ret = new PlanFlyweight(state, scope, handle);
			ip.putFlyweightCache(IPlan.class, new Tuple(IPlan.class, handle), ret);
		}
		return ret;
	}
	
	//-------- plan interface --------
	
	/**
	 *  Get the lifecycle state of the plan (e.g. body or aborted).
	 *  @return The lifecycle state.
	 */
	public String	getLifecycleState()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_lifecyclestate);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_lifecyclestate);
		}
	}

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IWaitqueue getWaitqueue()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = WaitqueueFlyweight.getWaitqueueFlyweight(getState(), getScope(), getHandle());
				}
			};
			return (IWaitqueue)invoc.object;
		}
		else
		{
			return WaitqueueFlyweight.getWaitqueueFlyweight(getState(), getScope(), getHandle());
		}
	}

	/**
	 *  Get the reason (i.e. initial event).
	 *  @return The reason.
	 */
	public IElement getReason()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getReason(getState(), getScope(), getHandle());
				}
			};
			return (IElement) invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.getReason(getState(), getScope(), getHandle());
		}
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public Object getBody()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_body);
				}
			};
			return invoc.object;
		}
		else
		{
			return getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_body);
		}
	}
	
	/**
	 *  Abort a running plan. 
	 */
	public void abortPlan()
	{
		// what about when the plan is handling a goal.
		// is the goal properly executed?
		
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					PlanRules.abortPlan(getState(), getScope(), getHandle());
				}
			};
		}
		else
		{
			PlanRules.abortPlan(getState(), getScope(), getHandle());
		}
	}
	
	/**
	 *  Start plan processing.
	 */
	public void startPlan()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					PlanRules.adoptPlan(getState(), getScope(), getHandle());
				}
			};
		}
		else
		{
			PlanRules.adoptPlan(getState(), getScope(), getHandle());
		}
	}
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(final IPlanListener listener)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					addEventListener(listener, getHandle());
				}
			};
		}
		else
		{
			addEventListener(listener, getHandle());
		}
	}
	
	/**
	 *  Remove a plan listener.
	 *  @param listener The plan listener.
	 */
	public void removePlanListener(final IPlanListener listener)
	{
		// Todo: safe removal when plan is already finished.
		
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					removeEventListener(listener, getHandle(), false);
				}
			};
		}
		else
		{
			removeEventListener(listener, getHandle(), false);
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
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MPlanFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MPlanFlyweight(getState(), mscope, me);
		}
	}
}
