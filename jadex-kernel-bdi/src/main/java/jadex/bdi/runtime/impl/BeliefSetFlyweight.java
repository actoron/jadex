package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

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
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		BeliefSetFlyweight ret = (BeliefSetFlyweight)ip.getFlyweightCache(IBeliefSet.class).get(handle);
		if(ret==null)
		{
			ret = new BeliefSetFlyweight(state, scope, handle);
			ip.getFlyweightCache(IBeliefSet.class).put(handle, ret);
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
			new AgentInvocation()
			{
				public void run()
				{
					BeliefRules.addBeliefSetValue(getState(), getHandle(), fact);
				}
			};
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
			new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object	evamode	= getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
					if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
						throw new RuntimeException("Removing value on dynamic beliefset not allowed: "+this);
					
					BeliefRules.removeBeliefSetValue(getState(), getHandle(), fact);
				}
			};
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object	evamode	= getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
			if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
				throw new RuntimeException("Removing value on dynamic beliefset not allowed: "+this);

			getInterpreter().startMonitorConsequences();
			BeliefRules.removeBeliefSetValue(getState(), getHandle(), fact);
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
			new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object	evamode	= getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
					if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
						throw new RuntimeException("Adding values on dynamic beliefset not allowed: "+this);
		
					for(int i=0; facts!=null && i<facts.length; i++)
						addFact(facts[i]);
				}
			};
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object	evamode	= getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
			if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
				throw new RuntimeException("Adding values on dynamic beliefset not allowed: "+this);

			getInterpreter().startMonitorConsequences();
			for(int i=0; facts!=null && i<facts.length; i++)
				addFact(facts[i]);
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
			new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object	evamode	= getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
					if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
						throw new RuntimeException("Removing values on dynamic beliefset not allowed: "+this);
		
					Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
					if(coll!=null)
					{
						Object[] facts = coll.toArray();
						
						for(int i=0; i<facts.length; i++)
							BeliefRules.removeBeliefSetValue(getState(), getHandle(), facts[i]);
					}
				}
			};
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object	evamode	= getState().getAttributeValue(me, OAVBDIMetaModel.typedelement_has_evaluationmode);
			if(!OAVBDIMetaModel.EVALUATIONMODE_STATIC.equals(evamode))
				throw new RuntimeException("Removing values on dynamic beliefset not allowed: "+this);
			
			Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
			if(coll!=null)
			{
				Object[] facts = coll.toArray();
			
				getInterpreter().startMonitorConsequences();
				for(int i=0; i<facts.length; i++)
					BeliefRules.removeBeliefSetValue(getState(), getHandle(), facts[i]);
				getInterpreter().endMonitorConsequences();
			}
		}
	}

	/**
	 *  Get a value equal to the given object.
	 *  @param oldval The old value.
	 */
	public Object getFact(final Object oldval)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					// Convert wrapped basic values to desired class (e.g. Integer to Long).
					Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Class	clazz	= (Class)getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
					Object	coldval	= SReflect.convertWrappedValue(oldval, clazz);

					Object newval = null;
					boolean found = false;
					Collection coll = getState().getAttributeValues(getHandle(), 
						OAVBDIRuntimeModel.beliefset_has_facts);
					if(coll!=null)
					{
						for(Iterator it = coll.iterator(); it.hasNext() && !found; )
						{
							Object	val	= it.next();
							found = SUtil.equals(coldval, val);
							if(found)
								newval = val;
						}
						object =  newval;
					}
				}
			};
			return invoc.object;
		}
		else
		{
			// Convert wrapped basic values to desired class (e.g. Integer to Long).
			Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Class	clazz	= (Class)getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
			Object	coldval	= SReflect.convertWrappedValue(oldval, clazz);

			Object newval = null;
			boolean found = false;
			Collection coll = getState().getAttributeValues(getHandle(), 
				OAVBDIRuntimeModel.beliefset_has_facts);
			if(coll!=null)
			{
				for(Iterator it = coll.iterator(); it.hasNext() && !found; )
				{
					Object	val	= it.next();
					found = SUtil.equals(coldval, val);
					if(found)
						newval = val;
				}
			}
			return newval;
		}
	}

	/**
	 *  Test if a fact is contained in a belief.
	 *  @param fact The fact to test.
	 *  @return True, if fact is contained.
	 */
	public boolean containsFact(final Object fact)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					// Convert wrapped basic values to desired class (e.g. Integer to Long).
					Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Class	clazz	= (Class)getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
					Object	newfact	= SReflect.convertWrappedValue(fact, clazz);

					Collection	coll	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
					bool = coll!=null && coll.contains(newfact);
				}
			};
			return invoc.bool;
		}
		else
		{
			// Convert wrapped basic values to desired class (e.g. Integer to Long).
			Object	mbel	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Class	clazz	= (Class)getState().getAttributeValue(mbel, OAVBDIMetaModel.typedelement_has_class);
			Object	newfact	= SReflect.convertWrappedValue(fact, clazz);

			Collection	coll	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
			return coll!=null && coll.contains(newfact);
		}
	}

	/**
	 *  Get the facts of a beliefset.
	 *  @return The facts.
	 */
	public Object[]	getFacts()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	mbelset	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Collection facts	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
					Class	clazz	= (Class)getState().getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
					oarray	= (Object[])Array.newInstance(SReflect.getWrappedType(clazz), facts!=null ? facts.size() : 0);
					if(facts!=null)
						oarray = facts.toArray(oarray);
				}
			};
			return invoc.oarray;
		}
		else
		{
			Object[] ret;
			
			Object	mbelset	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Collection facts	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
			Class	clazz	= (Class)getState().getAttributeValue(mbelset, OAVBDIMetaModel.typedelement_has_class);
			ret	= (Object[])Array.newInstance(SReflect.getWrappedType(clazz), facts!=null ? facts.size() : 0);
			if(facts!=null)
			{
				ret = facts.toArray(ret);
			}
			return ret;
		}
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
			new AgentInvocation()
			{
				public void run()
				{
					if(containsFact(newfact))
						BeliefRules.removeBeliefSetValue(getState(), getHandle(), newfact);
					BeliefRules.addBeliefSetValue(getState(), getHandle(), newfact);
				}
			};
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			if(containsFact(newfact))
				BeliefRules.removeBeliefSetValue(getState(), getHandle(), newfact);
			BeliefRules.addBeliefSetValue(getState(), getHandle(), newfact);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public int size()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection	coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
					integer = coll!=null ? coll.size() : 0;
				}
			};
			return invoc.integer;
		}
		else
		{
			Collection	coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.beliefset_has_facts);
			return coll!=null ? coll.size() : 0;
		}
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
			new AgentInvocation()
			{
				public void run()
				{
					getInterpreter().getEventReificator().objectModified(getHandle(), getState().getType(getHandle()), OAVBDIRuntimeModel.beliefset_has_facts, fact, fact);
				}
			};
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

	/**
	 *  Is this belief accessable.
	 *  @return False, if the belief cannot be accessed.
	 */
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
	}
	
	//-------- listeners --------
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefSetListener(final IBeliefSetListener listener)
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
	public void removeBeliefSetListener(final IBeliefSetListener listener)
	{
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
