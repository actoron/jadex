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
	public IFuture addFact(final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					BeliefRules.addBeliefSetValue(getState(), getHandle(), fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			BeliefRules.addBeliefSetValue(getState(), getHandle(), fact);
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Remove a fact to a belief.
	 *  @param fact The new fact.
	 */
	public IFuture removeFact(final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.removeFact(getState(), getHandle(), fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			FlyweightFunctionality.removeFact(getState(), getHandle(), fact);
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Add facts to a parameter set.
	 *  @param facts The facts.
	 */
	public IFuture addFacts(final Object[] facts)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.addFacts(getState(), getHandle(), facts);
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			FlyweightFunctionality.addFacts(getState(), getHandle(), facts);
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Remove all facts from a belief.
	 */
	public IFuture removeFacts()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.removeFacts(getState(), getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			FlyweightFunctionality.removeFacts(getState(), getHandle());
			ret.setResult(null);
		}
		
		return ret;
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
					ret.setResult(FlyweightFunctionality.getFact(getState(), getHandle(), getScope(), oldval));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getFact(getState(), getHandle(), getScope(), oldval));
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
	public IFuture updateFact(final Object newfact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					FlyweightFunctionality.updateFact(getState(), getHandle(), newfact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			FlyweightFunctionality.updateFact(getState(), getHandle(), newfact);
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
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
	public IFuture modified(final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
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
	public IFuture addBeliefSetListener(final IBeliefSetListener listener)
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
	public IFuture removeBeliefSetListener(final IBeliefSetListener listener)
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