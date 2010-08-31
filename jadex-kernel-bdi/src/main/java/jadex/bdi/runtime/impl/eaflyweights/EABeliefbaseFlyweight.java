package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MBeliefbaseFlyweight;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IEABeliefbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class EABeliefbaseFlyweight extends ElementFlyweight implements IEABeliefbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EABeliefbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EABeliefbaseFlyweight getBeliefbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EABeliefbaseFlyweight ret = (EABeliefbaseFlyweight)ip.getFlyweightCache(IEABeliefbase.class, new Tuple(IEABeliefbase.class, scope));
		if(ret==null)
		{
			ret = new EABeliefbaseFlyweight(state, scope);
			ip.putFlyweightCache(IEABeliefbase.class, new Tuple(IEABeliefbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods concerning beliefs --------

    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IFuture getBelief(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name, true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name, true));
		}
		
		return ret;
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IFuture getBeliefSet(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name, true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name, true));
		}
		
		return ret;
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief with the
	 *  specified name.
	 *  @param name the name of a belief.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief set.
	 *  @see #containsBeliefSet(java.lang.String)
	 */
	public IFuture containsBelief(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.containsBelief(getState(), getHandle(), getScope(), name));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.containsBelief(getState(), getHandle(), getScope(), name));
		}
		
		return ret;
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief set with the
	 *  specified name.
	 *  @param name the name of a belief set.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief.
	 *  @see #containsBelief(java.lang.String)
	 */
	public IFuture containsBeliefSet(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.containsBeliefSet(getState(), getHandle(), getScope(), name));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.containsBeliefSet(getState(), getHandle(), getScope(), name));
		}
		
		return ret;
	}

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public IFuture getBeliefNames()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public IFuture getBeliefSetNames()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope()));
		}
		
		return ret;
	}

	/**
	 *  Create a belief with given key and class.
	 *  @param key The key identifying the belief.
	 *  @param clazz The class.
	 *  @deprecated
	 * /
	public void createBelief(String key, Class clazz, int update)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Create a belief with given key and class.
	 *  @param key The key identifying the belief.
	 *  @param clazz The class.
	 *  @deprecated
	 * /
	public void createBeliefSet(String key, Class clazz, int update)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Delete a belief with given key.
	 *  @param key The key identifying the belief.
	 *  @deprecated
	 * /
	public void deleteBelief(String key)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Delete a belief with given key.
	 *  @param key The key identifying the belief.
	 *  @deprecated
	 * /
	public void deleteBeliefSet(String key)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Register a new belief.
	 *  @param mbelief The belief model.
	 * /
	public void registerBelief(IMBelief mbelief)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Register a new beliefset model.
	 *  @param mbeliefset The beliefset model.
	 * /
	public void registerBeliefSet(IMBeliefSet mbeliefset)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Register a new belief reference.
	 *  @param mbeliefref The belief reference model.
	 * /
	public void registerBeliefReference(IMBeliefReference mbeliefref)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Register a new beliefset reference model.
	 *  @param mbeliefsetref The beliefset reference model.
	 * / 
	public void registerBeliefSetReference(IMBeliefSetReference mbeliefsetref)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister a belief model.
	 *  @param mbelief The belief model.
	 * /
	public void deregisterBelief(IMBelief mbelief)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister a beliefset model.
	 *  @param mbeliefset The beliefset model.
	 * /
	public void deregisterBeliefSet(IMBeliefSet mbeliefset)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister a belief reference model.
	 *  @param mbeliefref The belief reference model.
	 * /
	public void deregisterBeliefReference(IMBeliefReference mbeliefref)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister a beliefset reference model.
	 *  @param mbeliefsetref The beliefset reference model.
	 * /
	public void deregisterBeliefSetReference(IMBeliefSetReference mbeliefsetref)
	{
		// todo: implement me
		throw new UnsupportedOperationException();
	}*/
	
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
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MBeliefbaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MBeliefbaseFlyweight(getState(), mscope);
		}
	}*/

//	/**
//	 *  Create a belief reference flyweight.
//	 */
//	public static IBelief createBeliefReferenceFlyweight(IOAVState state, Object rcapa, Object mcapa, Object mbel)
//	{
//		// Hack!!! Is there a better way to lookup references?
//		String	refname	= (String)state.getAttributeValue(mbel, OAVBDIMetaModel.elementreference_has_concrete);
//		String	refcap	= refname.substring(0, refname.indexOf('.'));
//		refname	= refname.substring(refname.indexOf('.')+1);
//		Collection	coll	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs);
//		int i=0;
//		for(Iterator it=coll.iterator(); it.hasNext(); i++)
//		{
//			Object	caparef	= it.next();
//			if(state.getAttributeValue(caparef, OAVBDIMetaModel.modelelement_has_name).equals(refcap))
//				break;
//		}
//		coll	= state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
//		Object	refcapa	= null;
//		int j=0;
//		for(Iterator it=coll.iterator(); it.hasNext(); j++)
//		{
//			refcapa	= it.next();
//			if(i==j)
//				break;
//		}
//		
//		// Todo: nested references.
//		Object	mrefcapa	= state.getAttributeValue(refcapa, OAVBDIRuntimeModel.element_has_model);
//		Object	mbeliefref	= state.getAttributeValue(mrefcapa, OAVBDIMetaModel.capability_has_beliefs, refname);
//		Object	beliefref	= state.getAttributeValue(refcapa, OAVBDIRuntimeModel.capability_has_beliefs, mbeliefref);
//		
//		return new BeliefReferenceFlyweight(state, mbel, new BeliefFlyweight(state, refcapa, beliefref));
//	}

//	/**
//	 *  Create a beliefset reference flyweight.
//	 */
//	public static IBeliefSet createBeliefSetReferenceFlyweight(IOAVState state, Object rcapa, Object mcapa, Object mbelset)
//	{
//		// Hack!!! Is there a better way to lookup references?
//		String	refname	= (String)state.getAttributeValue(mbelset, OAVBDIMetaModel.elementreference_has_concrete);
//		String	refcap	= refname.substring(0, refname.indexOf('.'));
//		refname	= refname.substring(refname.indexOf('.')+1);
//		Collection	coll	= state.getAttributeValues(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs);
//		int i=0;
//		for(Iterator it=coll.iterator(); it.hasNext(); i++)
//		{
//			Object	caparef	= it.next();
//			if(state.getAttributeValue(caparef, OAVBDIMetaModel.modelelement_has_name).equals(refcap))
//				break;
//		}
//		coll	= state.getAttributeValues(rcapa, OAVBDIRuntimeModel.capability_has_subcapabilities);
//		Object	refcapa	= null;
//		int j=0;
//		for(Iterator it=coll.iterator(); it.hasNext(); j++)
//		{
//			refcapa	= it.next();
//			if(i==j)
//				break;
//		}
//		
//		// Todo: nested references.
//		Object	mrefcapa	= state.getAttributeValue(refcapa, OAVBDIRuntimeModel.element_has_model);
//		Object	mbeliefsetref	= state.getAttributeValue(mrefcapa, OAVBDIMetaModel.capability_has_beliefsets, refname);
//		Object	beliefsetref	= state.getAttributeValue(refcapa, OAVBDIRuntimeModel.capability_has_beliefsets, mbeliefsetref);
//		
//		return new BeliefSetReferenceFlyweight(state, mbelset, new BeliefSetFlyweight(state, refcapa, beliefsetref));
//	}
	
	//-------- convenience methods --------
	
	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public IFuture getBeliefFact(final String belief)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
					ret.setResult(bel.getFact());
				}
			});
		}
		else
		{
			IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
			ret.setResult(bel.getFact());
		}
		
		return ret;
	}
	
	/**
	 *  Set the belief fact.
	 *  @param belief The belief name.
	 *  @param fact The fact.
	 */
	public IFuture setBeliefFact(final String belief, final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
					bel.setFact(fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
			bel.setFact(fact);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get the facts of a beliefset.
	 *  @return The facts.
	 */
	public IFuture getBeliefSetFacts(final String beliefset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					ret.setResult(belset.getFacts());
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			ret.setResult(belset.getFacts());
		}
		
		return ret;
	}
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public IFuture addBeliefListener(final String belief, final IBeliefListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
					bel.addBeliefListener(listener);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
			bel.addBeliefListener(listener);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public IFuture removeBeliefListener(final String belief, final IBeliefListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
					bel.removeBeliefListener(listener);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBelief bel = (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), belief, false);
			bel.removeBeliefListener(listener);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Add a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param fact The fact.
	 */
	public IFuture addBeliefSetFact(final String beliefset, final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					belset.addFact(fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			belset.addFact(fact);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Add a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param facts The facts.
	 */
	public IFuture addBeliefSetFacts(final String beliefset, final Object[] facts)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					belset.addFacts(facts);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			belset.addFacts(facts);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param fact The fact.
	 */
	public IFuture removeBeliefSetFact(final String beliefset, final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					belset.removeFact(fact);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			belset.removeFact(fact);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove fact from a beliefset.
	 *  @param beliefset The beliefset name.
	 */
	public IFuture removeBeliefSetFacts(final String beliefset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					belset.removeFacts();
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			belset.removeFacts();
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param fact The fact.
	 */
	public IFuture containsBeliefSetFact(final String beliefset, final Object fact)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					ret.setResult(belset.containsFact(fact)? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			ret.setResult(belset.containsFact(fact)? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}
	
	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public IFuture getBeliefSetSize(final String beliefset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					ret.setResult(new Integer(belset.size()));
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			ret.setResult(new Integer(belset.size()));
		}
		
		return ret;
	}
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public IFuture addBeliefSetListener(final String beliefset, final IBeliefSetListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					belset.addBeliefSetListener(listener);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			belset.addBeliefSetListener(listener);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public IFuture removeBeliefSetListener(final String beliefset, final IBeliefSetListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
					belset.removeBeliefSetListener(listener);
					ret.setResult(null);
				}
			});
		}
		else
		{
			IBeliefSet belset = (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), beliefset, false);
			belset.removeBeliefSetListener(listener);
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
					object = new MBeliefbaseFlyweight(getState(), me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return new MBeliefbaseFlyweight(getState(), me);
		}
	}
}
