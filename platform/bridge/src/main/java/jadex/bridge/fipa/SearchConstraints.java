package jadex.bridge.fipa;

import java.io.Serializable;

import jadex.bridge.ISearchConstraints;

/**
 *  Java class for concept SearchConstraints of beanynizer_beans_fipa_default ontology.
 */
public class SearchConstraints implements ISearchConstraints, Serializable
{
	//-------- attributes ----------

	/** Attribute for slot search-id. */
	protected String searchid;

	/** Attribute for slot max-results. */
	protected int maxresults = 1;

	/** Attribute for slot max-depth. */
	protected int maxdepth = 0;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>SearchConstraints</code>.
	 */
	public SearchConstraints()
	{
	}
	
	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>SearchConstraints</code>.
	 */
	public SearchConstraints(int maxresults, int maxdepth)
	{
		this.maxresults = maxresults;
		this.maxdepth = maxdepth;
	}

	//-------- accessor methods --------

	/**
	 *  Get the search-id of this SearchConstraints.
	 * @return search-id
	 */
	public String getSearchId()
	{
		return this.searchid;
	}

	/**
	 *  Set the search-id of this SearchConstraints.
	 * @param searchid the value to be set
	 */
	public void setSearchId(String searchid)
	{
		this.searchid = searchid;
	}

	/**
	 *  Get the max-results of this SearchConstraints.
	 * @return max-results
	 */
	public int getMaxResults()
	{
		return this.maxresults;
	}

	/**
	 *  Set the max-results of this SearchConstraints.
	 * @param maxresults the value to be set
	 */
	public void setMaxResults(int maxresults)
	{
		this.maxresults = maxresults;
	}

	/**
	 *  Get the max-depth of this SearchConstraints.
	 * @return max-depth
	 */
	public int getMaxDepth()
	{
		return this.maxdepth;
	}

	/**
	 *  Set the max-depth of this SearchConstraints.
	 * @param maxdepth the value to be set
	 */
	public void setMaxDepth(int maxdepth)
	{
		this.maxdepth = maxdepth;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this SearchConstraints.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "SearchConstraints(" + ")";
	}

}
