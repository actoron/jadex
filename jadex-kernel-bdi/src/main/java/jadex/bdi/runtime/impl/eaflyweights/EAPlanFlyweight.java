package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MPlanFlyweight;
import jadex.bdi.runtime.IEAPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
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
					ret.setResult(SFlyweightFunctionality.getReason(getState(), getScope(), getHandle(), true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getReason(getState(), getScope(), getHandle(), true));
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
	public IFuture abortPlan()
	{
		final Future ret = new Future();
		
		// what about when the plan is handling a goal.
		// is the goal properly executed?
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					PlanRules.abortPlan(getState(), getScope(), getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			PlanRules.abortPlan(getState(), getScope(), getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param listener The plan listener.
	 */
	public IFuture addPlanListener(final IPlanListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					addEventListener(listener, getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			addEventListener(listener, getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a plan listener.
	 *  @param listener The plan listener.
	 */
	public IFuture removePlanListener(final IPlanListener listener)
	{
		final Future ret = new Future();
		// Todo: safe removal when plan is already finished.
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					removeEventListener(listener, getHandle(), false);
					ret.setResult(null);
				}
			});
		}
		else
		{
			removeEventListener(listener, getHandle(), false);
			ret.setResult(null);
		}
		
		return ret;
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
