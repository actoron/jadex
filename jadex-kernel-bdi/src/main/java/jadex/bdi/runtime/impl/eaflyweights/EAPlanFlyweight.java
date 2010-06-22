package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEAPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for plans.
 */
public class EAPlanFlyweight extends EAParameterElementFlyweight implements IEAPlan
{
	//-------- constructors --------
	
	/**
	 *  Create a new plan flyweight.
	 */
	private EAPlanFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAPlanFlyweight getPlanFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAPlanFlyweight ret = (EAPlanFlyweight)ip.getFlyweightCache(IEAPlan.class).get(handle);
		if(ret==null)
		{
			ret = new EAPlanFlyweight(state, scope, handle);
			ip.getFlyweightCache(IEAPlan.class).put(handle, ret);
		}
		return ret;
	}
	
	//-------- plan interface --------
	
	/**
	 *  Get the lifecycle state of the plan (e.g. body or aborted).
	 *  @return The lifecycle state.
	 */
	public IFuture getLifecycleState()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_lifecyclestate));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_lifecyclestate));
		}
		
		return ret;
	}

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IFuture getWaitqueue()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(EAWaitqueueFlyweight.getWaitqueueFlyweight(getState(), getScope(), getHandle()));
				}
			});
		}
		else
		{
			ret.setResult(EAWaitqueueFlyweight.getWaitqueueFlyweight(getState(), getScope(), getHandle()));
		}
		
		return ret;
	}

	/**
	 *  Get the reason (i.e. initial event).
	 *  @return The reason.
	 */
	public IFuture getReason()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getReason(getState(), getScope(), getHandle(), true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getReason(getState(), getScope(), getHandle(), true));
		}
		
		return ret;
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public IFuture getBody()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_body));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_body));
		}
		
		return ret;
	}
	
	/**
	 *  Abort a running plan. 
	 */
	public void abortPlan()
	{
		// what about when the plan is handling a goal.
		// is the goal properly executed?
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					PlanRules.abortPlan(getState(), getScope(), getHandle());
				}
			});
		}
		else
		{
			PlanRules.abortPlan(getState(), getScope(), getHandle());
		}
	}
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(final IPlanListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					addEventListener(listener, getHandle());
				}
			});
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
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					removeEventListener(listener, getHandle(), false);
				}
			});
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
	 * /
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
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
	}*/
}
