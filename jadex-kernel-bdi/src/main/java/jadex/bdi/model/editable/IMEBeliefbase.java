package jadex.bdi.model.editable;

import jadex.bdi.model.IMBeliefbase;

/**
 *  Interface for editable version of beliefbase.
 */
public interface IMEBeliefbase extends IMBeliefbase, IMEElement
{
	/**
	 *  Create a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMEBelief createBelief(String name);

	/**
	 *  Create a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IMEBeliefSet createBeliefSet(String name);
	
	/**
	 *  Create a belief reference for a name.
	 *  @param name	The belief reference name.
	 *  @param ref The belief reference name.
	 */
	public IMEBeliefReference createBeliefReference(String name, String ref);

	/**
	 *  Create a beliefset reference for a name.
	 *  @param name	The beliefset reference name.
	 *  @param ref The beliefset reference name.
	 */
	public IMEBeliefSetReference createBeliefSetReference(String name, String ref);
}
