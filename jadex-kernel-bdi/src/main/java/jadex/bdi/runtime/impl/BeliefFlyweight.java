package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.BeliefRules;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefListener;
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
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		BeliefFlyweight ret = (BeliefFlyweight)ip.getFlyweightCache(IBelief.class).get(handle);
		if(ret==null)
		{
			ret = new BeliefFlyweight(state, scope, handle);
			ip.getFlyweightCache(IBelief.class).put(handle, ret);
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
			new AgentInvocation()
			{
				public void run()
				{
					Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object	evamode = getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
					if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
					{
						throw new RuntimeException("Setting value not supported for dynamic belief: "
							+ getState().getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name));
					}
					BeliefRules.setBeliefValue(getState(), getHandle(), fact);
				}
			};
		}
		else
		{
			Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object	evamode = getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_evaluationmode);
			if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
			{
				throw new RuntimeException("Setting value not supported for dynamic belief: "
					+ getState().getAttributeValue(mbel, OAVBDIMetaModel.modelelement_has_name));
			}
			getInterpreter().startMonitorConsequences();
			BeliefRules.setBeliefValue(getState(), getHandle(), fact);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public Object	getFact()
	{
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object	fact = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.belief_has_fact);
					getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.belief_has_fact, fact, fact);
				}
			};
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
	public Class	getClazz()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					clazz = (Class)getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_class);
				}
			};
			return invoc.clazz;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return (Class)getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_class);
		}
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
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					removeEventListener(listener, getHandle());
				}
			};
		}
		else
		{
			removeEventListener(listener, getHandle());
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
