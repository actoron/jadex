package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IEABeliefSet;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for a belief set.
 */
public class EABeliefSetFlyweight extends ElementFlyweight implements IEABeliefSet
{
	//-------- constructors --------
	
	/**
	 *  Create a new belief flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EABeliefSetFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EABeliefSetFlyweight getBeliefSetFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EABeliefSetFlyweight ret = (EABeliefSetFlyweight)ip.getFlyweightCache(IEABeliefSet.class).get(handle);
		if(ret==null)
		{
			ret = new EABeliefSetFlyweight(state, scope, handle);
			ip.getFlyweightCache(IEABeliefSet.class).put(handle, ret);
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
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					BeliefRules.addBeliefSetValue(getState(), getHandle(), fact);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			BeliefRules.addBeliefSetValue(getState(), getHandle(), fact);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Remove a fact to a belief.
	 *  @param fact The new fact.
	 */
	public void removeFact(final Object fact)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.removeFact(getState(), getHandle(), fact);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			FlyweightFunctionality.removeFact(getState(), getHandle(), fact);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Add facts to a parameter set.
	 *  @param facts The facts.
	 */
	public void addFacts(final Object[] facts)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.addFacts(getState(), getHandle(), facts);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			FlyweightFunctionality.addFacts(getState(), getHandle(), facts);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Remove all facts from a belief.
	 */
	public void removeFacts()
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.removeFacts(getState(), getHandle());
				}
			});
		}
		else
		{
			FlyweightFunctionality.removeFacts(getState(), getHandle());
		}
	}

	/**
	 *  Get a value equal to the given object.
	 *  @param oldval The old value.
	 */
	public IFuture getFact(final Object oldval)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.getFact(getState(), getHandle(), getScope(), oldval);
				}
			});
		}
		else
		{
			FlyweightFunctionality.getFact(getState(), getHandle(), getScope(), oldval);
		}
		
		return ret;
	}

	/**
	 *  Test if a fact is contained in a belief.
	 *  @param fact The fact to test.
	 *  @return True, if fact is contained.
	 */
	public IFuture containsFact(final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(internalContainsFact(fact)? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			ret.setResult(internalContainsFact(fact)? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}
	
	/**
	 *  Internal method for checking if a fact is contained.
	 *  @param fact The fact.
	 *  @return True if contained.
	 */
	protected boolean internalContainsFact(Object fact)
	{
		// Convert wrapped basic values to desired class (e.g. Integer to Long).
		Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
		Class	clazz	= (Class)getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
		Object	newfact	= SReflect.convertWrappedValue(fact, clazz);

		Collection	coll	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
		return coll!=null && coll.contains(newfact);
	}

	/**
	 *  Get the facts of a beliefset.
	 *  @return The facts.
	 */
	public IFuture getFacts()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getFacts(getState(), getHandle()));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getFacts(getState(), getHandle()));
		}
		
		return ret;
	}

	/**
	 *  Update a fact to a new fact. Searches the old
	 *  value with equals, removes it and stores the new fact.
	 *  @param newfact The new fact.
	 */
	public void updateFact(final Object newfact)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.updateFact(getState(), getHandle(), newfact);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			FlyweightFunctionality.updateFact(getState(), getHandle(), newfact);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public IFuture size()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Collection	coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
					ret.setResult(coll!=null ? new Integer(coll.size()): new Integer(0));
				}
			});
		}
		else
		{
			Collection	coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
			ret.setResult(coll!=null ? new Integer(coll.size()): new Integer(0));
		}
		
		return ret;
	}
	
	/**
	 *  Indicate that a fact of this belief set was modified.
	 *  Calling this method causes an internal facts changed
	 *  event that might cause dependent actions.
	 */
	public void modified(final Object fact)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
			getInterpreter().endMonitorConsequences();
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
	public void removeBeliefSetListener(final IBeliefSetListener listener)
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
	}*/
}