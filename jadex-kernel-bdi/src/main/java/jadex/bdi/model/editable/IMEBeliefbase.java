package jadex.bdi.model.editable;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMBeliefReference;
import jadex.bdi.model.IMBeliefSet;
import jadex.bdi.model.IMBeliefSetReference;
import jadex.bdi.model.IMBeliefbase;

/**
 *  Interface for editable version of beliefbase.
 */
public interface IMEBeliefbase extends IMBeliefbase
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
	 */
	public IMEBeliefReference createBeliefReference(String name);

	/**
	 *  Create a beliefset reference for a name.
	 *  @param name	The beliefset reference name.
	 */
	public IMEBeliefSetReference createBeliefSetReference(String name);
}
