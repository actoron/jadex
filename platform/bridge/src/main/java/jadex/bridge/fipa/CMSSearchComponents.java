package jadex.bridge.fipa;


import java.util.ArrayList;
import java.util.List;

import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.types.cms.IComponentDescription;


/**
 *  Java class for concept AMSSearchComponents of beanynizer_beans_fipa_default ontology.
 */
public class CMSSearchComponents implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot searchconstraints. */
	protected ISearchConstraints searchconstraints;

	/** Attribute for slot componentdescriptions. */
	protected List componentdescriptions;

	/** Attribute for slot componentdescription. */
	protected IComponentDescription componentdescription;

	/** Flag if remote search should be done. */
	protected boolean remote;
	
	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>CMSSearchComponents</code>.
	 */
	public CMSSearchComponents()
	{
		this.componentdescriptions = new ArrayList();
	}

	/**
	 * Create a new <code>DFSearch</code>.
	 */
	public CMSSearchComponents(IComponentDescription componentdescription, IComponentDescription[] results)
	{
		this.componentdescription	= componentdescription;
		setComponentDescriptions(results);
	}

	//-------- accessor methods --------

	/**
	 *  Get the searchconstraints of this CMSSearchComponents.
	 * @return searchconstraints
	 */
	public ISearchConstraints getSearchConstraints()
	{
		return this.searchconstraints;
	}

	/**
	 *  Set the searchconstraints of this CMSSearchComponents.
	 * @param searchconstraints the value to be set
	 */
	public void setSearchConstraints(ISearchConstraints searchconstraints)
	{
		this.searchconstraints = searchconstraints;
	}

	/**
	 *  Get the componentdescriptions of this CMSSearchComponents.
	 * @return componentdescriptions
	 */
	public IComponentDescription[] getComponentDescriptions()
	{
		return (IComponentDescription[])componentdescriptions.toArray(new IComponentDescription[componentdescriptions.size()]);
	}

	/**
	 *  Set the componentdescriptions of this CMSSearchComponents.
	 * @param componentdescriptions the value to be set
	 */
	public void setComponentDescriptions(IComponentDescription[] componentdescriptions)
	{
		this.componentdescriptions.clear();
		for(int i = 0; i < componentdescriptions.length; i++)
			this.componentdescriptions.add(componentdescriptions[i]);
	}

	/**
	 *  Get an componentdescriptions of this CMSSearchComponents.
	 *  @param idx The index.
	 *  @return componentdescriptions
	 */
	public IComponentDescription getComponentDescription(int idx)
	{
		return (IComponentDescription)this.componentdescriptions.get(idx);
	}

	/**
	 *  Set a componentdescription to this CMSSearchComponents.
	 *  @param idx The index.
	 *  @param componentdescription a value to be added
	 */
	public void setComponentDescription(int idx, IComponentDescription componentdescription)
	{
		this.componentdescriptions.set(idx, componentdescription);
	}

	/**
	 *  Add a componentdescription to this CMSSearchComponents.
	 *  @param componentdescription a value to be removed
	 */
	public void addComponentDescription(IComponentDescription componentdescription)
	{
		this.componentdescriptions.add(componentdescription);
	}

	/**
	 *  Remove a componentdescription from this CMSSearchComponents.
	 *  @param componentdescription a value to be removed
	 *  @return  True when the componentdescriptions have changed.
	 */
	public boolean removeComponentDescription(IComponentDescription componentdescription)
	{
		return this.componentdescriptions.remove(componentdescription);
	}


	/**
	 *  Get the componentdescription of this CMSSearchComponents.
	 * @return componentdescription
	 */
	public IComponentDescription getComponentDescription()
	{
		return this.componentdescription;
	}

	/**
	 *  Set the componentdescription of this CMSSearchComponents.
	 * @param componentdescription the value to be set
	 */
	public void setComponentDescription(IComponentDescription componentdescription)
	{
		this.componentdescription = componentdescription;
	}
	
	

	//-------- additional methods --------

	/**
	 *  Test if remote.
	 *  @return True, if is remote.
	 */
	public boolean isRemote() 
	{
		return remote;
	}

	/**
	 *  Set remote flag.
	 *  @param remote The remote flag to set.
	 */
	public void setRemote(boolean remote) 
	{
		this.remote = remote;
	}

	/**
	 *  Get a string representation of this CMSSearchComponents.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CMSSearchComponents(" + ")";
	}

}
