package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MBeliefFlyweight;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyeight for a belief.
 */
public class BeliefFlyweight extends ElementFlyweight implements IBelief
{
	//-------- constructors --------
	
	/**
	 *  Create a new belief flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private BeliefFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static BeliefFlyweight getBeliefFlyweight(IOAVState state, Object scope, Object handle)
	{
		IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		BeliefFlyweight ret = (BeliefFlyweight)ip.getFlyweightCache(IBelief.class, new Tuple(IBelief.class, handle));
		if(ret==null)
		{
			ret = new BeliefFlyweight(state, scope, handle);
			ip.putFlyweightCache(IBelief.class, new Tuple(IBelief.class, handle), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Set a fact of a belief.
	 *  @param fact The new fact.
	 */
	public void setFact(final Object fact)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.setFact(getState(), getHandle(), fact, getScope());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			SFlyweightFunctionality.setFact(getState(), getHandle(), fact, getScope());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public Object getFact()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = BeliefRules.getBeliefValue(getState(), getHandle(), getScope());
				}
			};
			return invoc.object;
		}
		else
		{
			return BeliefRules.getBeliefValue(getState(), getHandle(), getScope());
		}
	}

	/**
	 *  Indicate that the fact of this belief was modified.
	 *  Calling this method causes an internal fact changed
	 *  event that might cause dependent actions.
	 */
	public void modified()
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.modified(getState(), getHandle(), getInterpreter());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			SFlyweightFunctionality.modified(getState(), getHandle(), getInterpreter());
			getBDIFeature().endMonitorConsequences();
		}
	}
	
	/**
	 *  Get the value class.
	 *  @return The valuec class.
	 */
	public Class getClazz()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					clazz = SFlyweightFunctionality.getClazz(getState(), getHandle());
				}
			};
			return invoc.clazz;
		}
		else
		{
			return SFlyweightFunctionality.getClazz(getState(), getHandle());
		}
	}

	//-------- listeners --------
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(final IBeliefListener listener)
	{
		if(isExternalThread())
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
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(final IBeliefListener listener)
	{
		if(isExternalThread())
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
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MBeliefFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MBeliefFlyweight(getState(), mscope, me);
		}
	}
}
