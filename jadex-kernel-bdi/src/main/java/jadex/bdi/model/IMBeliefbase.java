package jadex.bdi.model;

/**
 *  Interface for beliefbase model.
 */
public interface IMBeliefbase
{
    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMBelief getBelief(String name);

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IMBeliefSet getBeliefSet(String name);
	
	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
//	public IMBelief getBeliefReference(String name);

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
//	public IMBeliefSet getBeliefSetReference(String name);

	/**
	 *  Returns all beliefs.
	 *  @return All beliefs.
	 */
	public IMBelief[] getBeliefs();

	/**
	 *  Return all belief sets.
	 *  @return All belief sets.
	 */
	public IMBeliefSet[] getBeliefSets();
}
