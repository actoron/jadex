package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MBeliefbaseFlyweight;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the belief base.
 */
public class BeliefbaseFlyweight extends ElementFlyweight implements IBeliefbase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private BeliefbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static BeliefbaseFlyweight getBeliefbaseFlyweight(IOAVState state, Object scope)
	{		
		IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		BeliefbaseFlyweight ret = (BeliefbaseFlyweight)ip.getFlyweightCache(IBeliefbase.class, new Tuple(IBeliefbase.class, scope));
		if(ret==null)
		{
			ret = new BeliefbaseFlyweight(state, scope);
			ip.putFlyweightCache(IBeliefbase.class, new Tuple(IBeliefbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods concerning beliefs --------

    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IBelief getBelief(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					object = SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name);
				}
			};
			return (IBelief)invoc.object;
		}
		else
		{
			return (IBelief)SFlyweightFunctionality.getBelief(getState(), getHandle(), getScope(), name);
		}
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IBeliefSet getBeliefSet(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					object = SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name);
				}
			};
			return (IBeliefSet)invoc.object;
		}
		else
		{
			return (IBeliefSet)SFlyweightFunctionality.getBeliefSet(getState(), getHandle(), getScope(), name);
		}
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief with the
	 *  specified name.
	 *  @param name the name of a belief.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief set.
	 *  @see #containsBeliefSet(java.lang.String)
	 */
	public boolean containsBelief(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					bool = SFlyweightFunctionality.containsBelief(getState(), getHandle(), getScope(), name);
				}
			};
			return invoc.bool;
		}
		else
		{
			return SFlyweightFunctionality.containsBelief(getState(), getHandle(), getScope(), name);
		}
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief set with the
	 *  specified name.
	 *  @param name the name of a belief set.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief.
	 *  @see #containsBelief(java.lang.String)
	 */
	public boolean containsBeliefSet(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					bool = SFlyweightFunctionality.containsBeliefSet(getState(), getHandle(), getScope(), name);
				}
			};
			return invoc.bool;
		}
		else
		{
			return SFlyweightFunctionality.containsBeliefSet(getState(), getHandle(), getScope(), name);
		}
	}

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public String[] getBeliefNames()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope());
				}
			};
			return invoc.sarray;
		}
		else
		{
			return SFlyweightFunctionality.getBeliefNames(getState(), getHandle(), getScope());
		}
	}

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public String[] getBeliefSetNames()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					sarray = SFlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope());
				}
			};
			return invoc.sarray;
		}
		else
		{
			return SFlyweightFunctionality.getBeliefSetNames(getState(), getHandle(), getScope());
		}
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
	 */
	public IMElement getModelElement()
	{
		if(isExternalThread())
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
	}

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