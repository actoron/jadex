package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IWaitqueue;
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
		PlanFlyweight ret = (PlanFlyweight)ip.getFlyweightCache(IPlan.class).get(handle);
		if(ret==null)
		{
			ret = new PlanFlyweight(state, scope, handle);
			ip.getFlyweightCache(IPlan.class).put(handle, ret);
		}
		return ret;
	}
	
	//-------- plan interface --------
	
	// Todo: add some methods (e.g. isAlive)?

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IWaitqueue getWaitqueue()
	{
		if(getInterpreter().isExternalThread())
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
	public Object getReason()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	elem	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_reason);
					if(elem!=null)
					{
						// todo: wrong scope
						object	= WaitqueueFlyweight.getFlyweight(getState(), getScope(), elem);
					}
				}
			};
			return invoc.object;
		}
		else
		{
			Object	elem	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_reason);
			IElement ret = null;
			if(elem!=null)
			{
				// todo: wrong scope
				ret = WaitqueueFlyweight.getFlyweight(getState(), getScope(), elem);
			}
			return ret;
		}
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public Object getBody()
	{
		if(getInterpreter().isExternalThread())
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
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(final IPlanListener listener)
	{
		if(getInterpreter().isExternalThread())
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
		
		if(getInterpreter().isExternalThread())
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
