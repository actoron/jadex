package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.AgentRules;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

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
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		BeliefbaseFlyweight ret = (BeliefbaseFlyweight)ip.getFlyweightCache(IBeliefbase.class).get(new Tuple(IBeliefbase.class, scope));
		if(ret==null)
		{
			ret = new BeliefbaseFlyweight(state, scope);
			ip.getFlyweightCache(IBeliefbase.class).put(new Tuple(IBeliefbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods concerning beliefs --------

    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	// changed signature for javaflow, removed final
	public IBelief getBelief(String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability((String) arg, OAVBDIMetaModel.belief_type, getScope(), getState());

					Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object mbel = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
					if(mbel!=null)
					{
						// Init on demand.
						if(!getState().containsKey(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel))
						{
							AgentRules.initBelief(getState(), scope[1], mbel, null);
						}
						Object rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);	
						object = BeliefFlyweight.getBeliefFlyweight(getState(), scope[1], rbel);
					}
					else
					{
						throw new RuntimeException("No such belief: "+scope[0]+" in "+scope[1]);
					}
				}
			};
			return (IBelief)invoc.object;
		}
		else
		{
			IBelief	ret;
			
			Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.belief_type, getScope(), getState());

			Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			Object mbel = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
			if(mbel!=null)
			{
				// Init on demand.
				if(!getState().containsKey(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel))
				{
					AgentRules.initBelief(getState(), scope[1], mbel, null);
				}
				Object rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);	
				ret = BeliefFlyweight.getBeliefFlyweight(getState(), scope[1], rbel);
			}
			else
			{
				throw new RuntimeException("No such belief: "+scope[0]+" in "+scope[1]);
			}
			
			return ret;
		}
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	// changed signature for javaflow, removed final
	public IBeliefSet getBeliefSet(String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability((String) arg, OAVBDIMetaModel.beliefset_type, getScope(), getState());

					Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object mbelset = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					if(mbelset!=null)
					{
						// Init on demand.
						if(!getState().containsKey(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset))
						{
							AgentRules.initBeliefSet(getState(), scope[1], mbelset, null);
						}
						
						Object rbelset = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);	
						object = BeliefSetFlyweight.getBeliefSetFlyweight(getState(), scope[1], rbelset);						
					}
					else
					{
						throw new RuntimeException("No such belief set: "+scope[0]+" in "+scope[1]);
					}
				}
			};
			return (IBeliefSet)invoc.object;
		}
		else
		{
			IBeliefSet	ret;

			Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.beliefset_type, getScope(), getState());

			Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			Object mbelset = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			if(mbelset!=null)
			{
				// Init on demand.
				if(!getState().containsKey(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset))
				{
					AgentRules.initBeliefSet(getState(), scope[1], mbelset, null);
				}
				
				Object rbelset = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbelset);	
				ret = BeliefSetFlyweight.getBeliefSetFlyweight(getState(), scope[1], rbelset);						
			}
			else
			{
				throw new RuntimeException("No such belief set: "+scope[0]+" in "+scope[1]);
			}
			return ret;
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
	// changed signature for javaflow, removed final
	public boolean containsBelief(String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability((String) arg, OAVBDIMetaModel.belief_type, getScope(), getState());
					Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object mbel = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
					bool = mbel!=null;
				}
			};
			return invoc.bool;
		}
		else
		{
			Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.belief_type, getScope(), getState());
			Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			Object mbel = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
			return mbel!=null;
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
	// changed signature for javaflow, removed final
	public boolean containsBeliefSet(String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability((String) arg, OAVBDIMetaModel.beliefset_type, getScope(), getState());
					Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object mbelset = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					bool = mbelset!=null;
				}
			};
			return invoc.bool;
		}
		else
		{
			Object[] scope = AgentRules.resolveCapability(name, OAVBDIMetaModel.beliefset_type, getScope(), getState());
			Object mscope = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			Object mbelset = getState().getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			return mbelset!=null;
		}
	}

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public String[] getBeliefNames()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Collection bels = getState().getAttributeValues(mscope, OAVBDIMetaModel.capability_has_beliefs);
					
					if(bels!=null)
					{
						sarray = new String[bels.size()];
						int i=0;
						for(Iterator it=bels.iterator(); it.hasNext(); i++)
						{
							sarray[i] = (String)getState().getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
						}
					}
					else
					{
						sarray = SUtil.EMPTY_STRING;
					}
				}
			};
			return invoc.sarray;
		}
		else
		{
			String[] ret;	
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Collection bels = getState().getAttributeValues(mscope, OAVBDIMetaModel.capability_has_beliefs);
			
			if(bels!=null)
			{
				ret = new String[bels.size()];
				int i=0;
				for(Iterator it=bels.iterator(); it.hasNext(); i++)
				{
					ret[i] = (String)getState().getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
				}
			}
			else
			{
				ret = SUtil.EMPTY_STRING;
			}
			return ret;
		}
	}

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public String[] getBeliefSetNames()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Collection belsets = getState().getAttributeValues(mscope, OAVBDIMetaModel.capability_has_beliefsets);
					
					if(belsets!=null)
					{
						sarray = new String[belsets.size()];
						int i=0;
						for(Iterator it=belsets.iterator(); it.hasNext(); i++)
						{
							sarray[i] = (String)getState().getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
						}
					}
					else
					{
						sarray = SUtil.EMPTY_STRING;
					}
				}
			};
			return invoc.sarray;
		}
		else
		{
			String[] ret;	
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Collection belsets = getState().getAttributeValues(mscope, OAVBDIMetaModel.capability_has_beliefsets);
			
			if(belsets!=null)
			{
				ret = new String[belsets.size()];
				int i=0;
				for(Iterator it=belsets.iterator(); it.hasNext(); i++)
				{
					ret[i] = (String)getState().getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
				}
			}
			else
			{
				ret = SUtil.EMPTY_STRING;
			}
			return ret;
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