package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEABeliefbase;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
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
		EABeliefbaseFlyweight ret = (EABeliefbaseFlyweight)ip.getFlyweightCache(IEABeliefbase.class).get(new Tuple(IEABeliefbase.class, scope));
		if(ret==null)
		{
			ret = new EABeliefbaseFlyweight(state, scope);
			ip.getFlyweightCache(IEABeliefbase.class).put(new Tuple(IEABeliefbase.class, scope), ret);
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
					ret.setResult(FlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name, true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name, true));
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
					ret.setResult(FlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name, true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name, true));
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
					ret.setResult(FlyweightFunctionality.containsBelief(getState(), getHandle(), getScope(), name));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.containsBelief(getState(), getHandle(), getScope(), name));
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
					ret.setResult(FlyweightFunctionality.containsBeliefSet(getState(), getHandle(), getScope(), name));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.containsBeliefSet(getState(), getHandle(), getScope(), name));
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
					ret.setResult(FlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope()));
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
					ret.setResult(FlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope()));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope()));
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
}
