package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MBeliefFlyweight;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IEABelief;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
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
	public IFuture setFact(final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					SFlyweightFunctionality.setFact(getState(), getHandle(), fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			SFlyweightFunctionality.setFact(getState(), getHandle(), fact);
			ret.setResult(null);
		}
		
		return ret;
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
	public IFuture modified()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object	fact = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.belief_has_fact);
					getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.belief_has_fact, fact, fact);
					ret.setResult(true);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			Object	fact = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.belief_has_fact);
			getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.belief_has_fact, fact, fact);
			getInterpreter().endMonitorConsequences();
			ret.setResult(true);
		}
		
		return ret;
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
	public IFuture addBeliefListener(final IBeliefListener listener)
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
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public IFuture removeBeliefListener(final IBeliefListener listener)
	{
		final Future ret = new Future();
		
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
