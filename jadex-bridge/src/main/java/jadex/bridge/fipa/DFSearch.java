package jadex.bridge.fipa;


import java.util.ArrayList;
import java.util.List;

import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.types.df.IDFComponentDescription;


/**
 *  Java class for concept DFSearch of beanynizer_beans_fipa_default ontology.
 */
public class DFSearch implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot searchconstraints. */
	protected ISearchConstraints searchconstraints;

	/** Attribute for slot dfcomponentdescription. */
	protected IDFComponentDescription dfcomponentdescription;

	/** Remote flag. */
	protected boolean remote;
	
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
	public DFSearch(IDFComponentDescription dfcomponentdescription, IDFComponentDescription[] results)
	{
		this();
		this.dfcomponentdescription	= dfcomponentdescription;
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
	 *  Get the dfcomponentdescription of this DFSearch.
	 * @return dfcomponentdescription
	 */
	public IDFComponentDescription getComponentDescription()
	{
		return this.dfcomponentdescription;
	}

	/**
	 *  Set the dfcomponentdescription of this DFSearch.
	 * @param dfcomponentdescription the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfcomponentdescription)
	{
		this.dfcomponentdescription = dfcomponentdescription;
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
	 *  Get the remote.
	 *  @return The remote.
	 */
	public boolean isRemote()
	{
		return remote;
	}

	/**
	 *  Set the remote.
	 *  @param remote The remote to set.
	 */
	public void setRemote(boolean remote)
	{
		this.remote = remote;
	}

	/**
	 *  Get a string representation of this DFSearch.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "DFSearch(" + ")";
	}

}
