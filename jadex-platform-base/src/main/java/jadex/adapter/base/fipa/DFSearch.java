package jadex.adapter.base.fipa;


import jadex.bridge.ISearchConstraints;

import java.util.ArrayList;
import java.util.List;


/**
 *  Java class for concept DFSearch of beanynizer_beans_fipa_default ontology.
 */
public class DFSearch implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot searchconstraints. */
	protected ISearchConstraints searchconstraints;

	/** Attribute for slot dfagentdescription. */
	protected IDFComponentDescription dfagentdescription;

	/** Attribute for slot results. */
	protected List results;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>DFSearch</code>.
	 */
	public DFSearch()
	{
		this.results = new ArrayList();
	}

	/**
	 * Create a new <code>DFSearch</code>.
	 */
	public DFSearch(IDFComponentDescription dfagentdescription, IDFComponentDescription[] results)
	{
		this();
		this.dfagentdescription	= dfagentdescription;
		setResults(results);
	}

	//-------- accessor methods --------

	/**
	 *  Get the searchconstraints of this DFSearch.
	 * @return searchconstraints
	 */
	public ISearchConstraints getSearchConstraints()
	{
		return this.searchconstraints;
	}

	/**
	 *  Set the searchconstraints of this DFSearch.
	 * @param searchconstraints the value to be set
	 */
	public void setSearchConstraints(ISearchConstraints searchconstraints)
	{
		this.searchconstraints = searchconstraints;
	}

	/**
	 *  Get the dfagentdescription of this DFSearch.
	 * @return dfagentdescription
	 */
	public IDFComponentDescription getComponentDescription()
	{
		return this.dfagentdescription;
	}

	/**
	 *  Set the dfagentdescription of this DFSearch.
	 * @param dfagentdescription the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfagentdescription)
	{
		this.dfagentdescription = dfagentdescription;
	}

	/**
	 *  Get the results of this DFSearch.
	 * @return results
	 */
	public IDFComponentDescription[] getResults()
	{
		return (IDFComponentDescription[])results.toArray(new IDFComponentDescription[results.size()]);
	}

	/**
	 *  Set the results of this DFSearch.
	 * @param results the value to be set
	 */
	public void setResults(IDFComponentDescription[] results)
	{
		this.results.clear();
		for(int i = 0; i < results.length; i++)
			this.results.add(results[i]);
	}

	/**
	 *  Get an results of this DFSearch.
	 *  @param idx The index.
	 *  @return results
	 */
	public IDFComponentDescription getResult(int idx)
	{
		return (IDFComponentDescription)this.results.get(idx);
	}

	/**
	 *  Set a result to this DFSearch.
	 *  @param idx The index.
	 *  @param result a value to be added
	 */
	public void setResult(int idx, IDFComponentDescription result)
	{
		this.results.set(idx, result);
	}

	/**
	 *  Add a result to this DFSearch.
	 *  @param result a value to be removed
	 */
	public void addResult(IDFComponentDescription result)
	{
		this.results.add(result);
	}

	/**
	 *  Remove a result from this DFSearch.
	 *  @param result a value to be removed
	 *  @return  True when the results have changed.
	 */
	public boolean removeResult(IDFComponentDescription result)
	{
		return this.results.remove(result);
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this DFSearch.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "DFSearch(" + ")";
	}

}
