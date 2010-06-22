package jadex.bdi.runtime;

import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.commons.Future;
import jadex.commons.IFuture;

/**
 *  The beliefbase contains the beliefs and beliefsets
 *  of an agent or capability.
 */
public interface IEABeliefbase extends IEAElement 
{
	//-------- methods concerning beliefs --------

    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
//	public IFuture getBelief(String name);

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
//	public IFuture getBeliefSet(String name);

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief with the
	 *  specified name.
	 *  @param name the name of a belief.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief set.
	 *  @see #containsBeliefSet(java.lang.String)
	 */
//	public IFuture containsBelief(String name);

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief set with the
	 *  specified name.
	 *  @param name the name of a belief set.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief.
	 *  @see #containsBelief(java.lang.String)
	 */
//	public IFuture containsBeliefSet(String name);

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public IFuture getBeliefNames();

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public IFuture getBeliefSetNames();

	/**
	 *  Create a belief with given key and class.
	 *  @param key The key identifying the belief.
	 *  @param clazz The class.
	 *  @deprecated
	 */
//	public void createBelief(String key, Class clazz, int update);

	/**
	 *  Create a belief with given key and class.
	 *  @param key The key identifying the belief.
	 *  @param clazz The class.
	 *  @deprecated
	 */
//	public void createBeliefSet(String key, Class clazz, int update);

	/**
	 *  Delete a belief with given key.
	 *  @param key The key identifying the belief.
	 *  @deprecated
	 */
//	public void deleteBelief(String key);

	/**
	 *  Delete a belief with given key.
	 *  @param key The key identifying the belief.
	 *  @deprecated
	 */
//	public void deleteBeliefSet(String key);

	/**
	 *  Register a new belief.
	 *  @param mbelief The belief model.
	 */
//	public void registerBelief(IMBelief mbelief);

	/**
	 *  Register a new beliefset model.
	 *  @param mbeliefset The beliefset model.
	 */
//	public void registerBeliefSet(IMBeliefSet mbeliefset);

	/**
	 *  Register a new belief reference.
	 *  @param mbeliefref The belief reference model.
	 */
//	public void registerBeliefReference(IMBeliefReference mbeliefref);

	/**
	 *  Register a new beliefset reference model.
	 *  @param mbeliefsetref The beliefset reference model.
	 */
//	public void registerBeliefSetReference(IMBeliefSetReference mbeliefsetref);

	/**
	 *  Deregister a belief model.
	 *  @param mbelief The belief model.
	 */
//	public void deregisterBelief(IMBelief mbelief);

	/**
	 *  Deregister a beliefset model.
	 *  @param mbeliefset The beliefset model.
	 */
//	public void deregisterBeliefSet(IMBeliefSet mbeliefset);

	/**
	 *  Deregister a belief reference model.
	 *  @param mbeliefref The belief reference model.
	 */
//	public void deregisterBeliefReference(IMBeliefReference mbeliefref);

	/**
	 *  Deregister a beliefset reference model.
	 *  @param mbeliefsetref The beliefset reference model.
	 */
//	public void deregisterBeliefSetReference(IMBeliefSetReference mbeliefsetref);


	/**
	 *  Get the fact of a belief.
	 *  @return The fact.
	 */
	public IFuture getBeliefFact(final String belief);
	
	/**
	 *  Set the belief fact.
	 *  @param belief The belief name.
	 *  @param fact The fact.
	 */
	public void setBeliefFact(final String belief, final Object fact);
	
	/**
	 *  Get the facts of a beliefset.
	 *  @return The facts.
	 */
	public IFuture getBeliefSetFacts(final String beliefset);
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(final String belief, final IBeliefListener listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(final String belief, final IBeliefListener listener);
	
	/**
	 *  Add a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param fact The fact.
	 */
	public void addBeliefSetFact(final String beliefset, final Object fact);
	
	/**
	 *  Remove a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param fact The fact.
	 */
	public void removeBeliefSetFact(final String beliefset, final Object fact);
	
	/**
	 *  Remove a fact to a beliefset.
	 *  @param beliefset The beliefset name.
	 *  @param fact The fact.
	 */
	public IFuture containsBeliefSetFact(final String beliefset, final Object fact);
	
	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public IFuture getBeliefSetSize(final String beliefset);
	
	/**
	 *  Add a belief listener.
	 *  @param listener The belief listener.
	 */
	public void addBeliefSetListener(final String beliefset, final IBeliefSetListener listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefSetListener(final String beliefset, final IBeliefSetListener listener);
	
}
