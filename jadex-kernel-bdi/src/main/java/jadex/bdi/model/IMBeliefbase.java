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
	 *  Returns all beliefs.
	 *  @return All beliefs.
	 */
	public IMBelief[] getBeliefs();
	
	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IMBeliefSet getBeliefSet(String name);
	
	/**
	 *  Return all belief sets.
	 *  @return All belief sets.
	 */
	public IMBeliefSet[] getBeliefSets();
	
	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMBeliefReference getBeliefReference(String name);

	/**
	 *  Returns all belief references.
	 *  @return All belief references.
	 */
	public IMBeliefReference[] getBeliefReferences();
	
	/**
	 *  Get a beliefset reference for a name.
	 *  @param name	The beliefset reference name.
	 */
	public IMBeliefSetReference getBeliefSetReference(String name);

	/**
	 *  Returns all beliefset references.
	 *  @return All beliefset references.
	 */
	public IMBeliefSetReference[] getBeliefSetReferences();
	
}
