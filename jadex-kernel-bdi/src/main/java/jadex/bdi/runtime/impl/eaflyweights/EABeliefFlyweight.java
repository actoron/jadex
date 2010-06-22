package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IEABelief;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class EABeliefFlyweight extends ElementFlyweight implements IEABelief
{
	//-------- constructors --------
	
	/**
	 *  Create a new belief flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EABeliefFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EABeliefFlyweight getBeliefFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EABeliefFlyweight ret = (EABeliefFlyweight)ip.getFlyweightCache(IEABelief.class).get(handle);
		if(ret==null)
		{
			ret = new EABeliefFlyweight(state, scope, handle);
			ip.getFlyweightCache(IEABelief.class).put(handle, ret);
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
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.setFact(getState(), getHandle(), fact);
				}
			});
		}
		else
		{
			FlyweightFunctionality.setFact(getState(), getHandle(), fact);
		}
	}

	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public IFuture getFact()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(BeliefRules.getBeliefValue(getState(), getHandle(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(BeliefRules.getBeliefValue(getState(), getHandle(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Indicate that the fact of this belief was modified.
	 *  Calling this method causes an internal fact changed
	 *  event that might cause dependent actions.
	 */
	public void modified()
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object	fact = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.belief_has_fact);
					getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.belief_has_fact, fact, fact);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			Object	fact = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.belief_has_fact);
			getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.belief_has_fact, fact, fact);
			getInterpreter().endMonitorConsequences();
		}
	}
	
	/**
	 *  Get the value class.
	 *  @return The valuec class.
	 */
	public IFuture getClazz()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_class));
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_class));
		}
		
		return ret;
	}

	//-------- listeners --------
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(final IBeliefListener listener)
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
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(final IBeliefListener listener)
	{
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
	}*/
}
