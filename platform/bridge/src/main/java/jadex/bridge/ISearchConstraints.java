package jadex.bridge;

/**
 *  Interface for search constraints.
 */
public interface ISearchConstraints
{
	/**
	 *  Get the max-results of this SearchConstraints.
	 *  @return max-results
	 */
	public int  getMaxResults();

	/**
	 *  Get the max-depth of this SearchConstraints.
	 *  @return max-depth
	 */
	public int  getMaxDepth();
	
	/**
	 *  Get the search id.
	 *  @return The search id.
	 */
	public String getSearchId();
}
