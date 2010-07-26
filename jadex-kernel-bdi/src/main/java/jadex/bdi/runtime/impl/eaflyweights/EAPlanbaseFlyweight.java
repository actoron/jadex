package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MPlanbaseFlyweight;
import jadex.bdi.runtime.IEAPlanbase;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the plan base.
 */
public class EAPlanbaseFlyweight extends ElementFlyweight implements IEAPlanbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new planbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAPlanbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAPlanbaseFlyweight getPlanbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAPlanbaseFlyweight ret = (EAPlanbaseFlyweight)ip.getFlyweightCache(IEAPlanbase.class).get(new Tuple(IEAPlanbase.class, scope));
		if(ret==null)
		{
			ret = new EAPlanbaseFlyweight(state, scope);
			ip.getFlyweightCache(IEAPlanbase.class).put(new Tuple(IEAPlanbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- IPlanbase interface --------
	
	/**
	 *  Get all running plans of this planbase.
	 *  @return The plans.
	 */
	public IFuture getPlans()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getPlans(getState(), getHandle(), true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getPlans(getState(), getHandle(), true));
		}
		
		return ret;
	}

	/**
	 *  Get all plans of a specified type (=model element name).
	 *  @param type The plan type.
	 *  @return All plans of the specified type.
	 */
	public IFuture getPlans(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getPlans(getState(), getHandle(), true, type));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getPlans(getState(), getHandle(), true, type));
		}
		
		return ret;
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
	public IFuture addPlanListener(final String type, final IPlanListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mplan = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
					addEventListener(listener, mplan);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mplan = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
			addEventListener(listener, mplan);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a goal listener.
	 *  @param type	The goal type.
	 *  @param listener The goal listener.
	 */
	public IFuture removePlanListener(final String type, final IPlanListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mplan = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
					removeEventListener(listener, mplan, false);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mplan = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_plans);
			removeEventListener(listener, mplan, false);
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
