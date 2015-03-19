package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MBeliefSetFlyweight;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for a belief set.
 */
public class BeliefSetFlyweight extends ElementFlyweight implements IBeliefSet
{
	//-------- constructors --------
	
	/**
	 *  Create a new belief flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private BeliefSetFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static BeliefSetFlyweight getBeliefSetFlyweight(IOAVState state, Object scope, Object handle)
	{
		IBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		BeliefSetFlyweight ret = (BeliefSetFlyweight)ip.getFlyweightCache(IBeliefSet.class, new Tuple(IBeliefSet.class, handle));
		if(ret==null)
		{
			ret = new BeliefSetFlyweight(state, scope, handle);
			ip.putFlyweightCache(IBeliefSet.class, new Tuple(IBeliefSet.class, handle), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Add a fact to a belief.
	 *  @param fact The new fact.
	 */
	public void addFact(final Object fact)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					BeliefRules.addBeliefSetValue(getState(), getHandle(), fact, getScope());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			BeliefRules.addBeliefSetValue(getState(), getHandle(), fact, getScope());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Remove a fact to a belief.
	 *  @param fact The new fact.
	 */
	public void removeFact(final Object fact)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.removeFact(getState(), getHandle(), fact, getScope());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			SFlyweightFunctionality.removeFact(getState(), getHandle(), fact, getScope());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Add facts to a parameter set.
	 *  @param facts The facts.
	 */
	public void addFacts(final Object[] facts)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.addFacts(getState(), getHandle(), facts, getScope());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			SFlyweightFunctionality.addFacts(getState(), getHandle(), facts, getScope());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Remove all facts from a belief.
	 */
	public void removeFacts()
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.removeFacts(getState(), getHandle(), getScope());
				}
			};
		}
		else
		{
			SFlyweightFunctionality.removeFacts(getState(), getHandle(), getScope());
		}
	}

	/**
	 *  Get a value equal to the given object.
	 *  @param oldval The old value.
	 */
	public Object getFact(final Object oldval)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getFact(getState(), getHandle(), getScope(), oldval);
				}
			};
			return invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.getFact(getState(), getHandle(), getScope(), oldval);
		}
	}

	/**
	 *  Test if a fact is contained in a belief.
	 *  @param fact The fact to test.
	 *  @return True, if fact is contained.
	 */
	public boolean containsFact(final Object fact)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = SFlyweightFunctionality.containsFact(getState(), getHandle(), fact);
				}
			};
			return invoc.bool;
		}
		else
		{
			return SFlyweightFunctionality.containsFact(getState(), getHandle(), fact);
		}
	}

	/**
	 *  Get the facts of a beliefset.
	 *  @return The facts.
	 */
	public Object[]	getFacts()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					oarray = SFlyweightFunctionality.getFacts(getState(), getHandle());
				}
			};
			return invoc.oarray;
		}
		else
		{
			return SFlyweightFunctionality.getFacts(getState(), getHandle());
		}
	}

	/**
	 *  Update a fact to a new fact. Searches the old
	 *  value with equals, removes it and stores the new fact.
	 *  @param newfact The new fact.
	 */
	public void updateFact(final Object newfact)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.updateFact(getState(), getHandle(), newfact, getScope());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			SFlyweightFunctionality.updateFact(getState(), getHandle(), newfact, getScope());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public int size()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection	coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
					integer = coll!=null ? Integer.valueOf(coll.size()): Integer.valueOf(0);
				}
			};
			return invoc.integer;
		}
		else
		{
			Collection	coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
			return coll!=null ? Integer.valueOf(coll.size()): Integer.valueOf(0);
		}
	}
	
	/**
	 *  Indicate that a fact of this belief set was modified.
	 *  Calling this method causes an internal facts changed
	 *  event that might cause dependent actions.
	 */
	public void modified(final Object fact)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getBDIFeature().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			getBDIFeature().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Update or add a fact. When the fact is already
	 *  contained it will be updated to the new fact.
	 *  Otherwise the value will be added.
	 *  @param fact The new or changed fact.
	 * /
	public void updateOrAddFact(Object fact);*/

	/**
	 *  Replace a fact with another one.
	 *  @param oldfact The old fact.
	 *  @param newfact The new fact.
	 * /
	public void replaceFact(Object oldfact, Object newfact);*/

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

	/**
	 *  Is this belief accessable.
	 *  @return False, if the belief cannot be accessed.
	 * /
	public boolean isAccessible()
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					// todo: how to do?
					throw new UnsupportedOperationException();
				}
			};
			return false;
		}
		else
		{
			// todo: how to do?
			throw new UnsupportedOperationException();
		}
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefSetListener(final IBeliefSetListener listener)
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
	public void removeBeliefSetListener(final IBeliefSetListener listener)
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
					object = new MBeliefSetFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MBeliefSetFlyweight(getState(), mscope, me);
		}
	}
}
