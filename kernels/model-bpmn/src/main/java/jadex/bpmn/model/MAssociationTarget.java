package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  The target element of an association.
 */
public class MAssociationTarget extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The associations description. */
	protected String associationsdescription;
	
	/** The associations. */
	protected List associations;

	//-------- methods ---------
	
	/**
	 *  Get the xml associations description.
	 *  @return The associations description.
	 */
	public String getAssociationsDescription()
	{
		return this.associationsdescription;
	}

	/**
	 *  Set the xml associations description.
	 *  @param associationsdescription The associations description to set.
	 */
	public void setAssociationsDescription(String associationsdescription)
	{
		this.associationsdescription = associationsdescription;
	}

	/**
	 *  Get the associations.
	 *  return The associations.
	 */
	public List getAssociations()
	{
		return associations;
	}

	/**
	 *  Add an association.
	 *  @param association The association.
	 */
	public void addAssociation(MAssociation association)
	{
		if(associations==null)
			associations = new ArrayList();
		associations.add(association);
	}
	
	/**
	 *  Remove an association.
	 *  @param association The association.
	 */
	public void removeAssociation(MAssociation association)
	{
		if(associations!=null)
			associations.remove(association);
	}
}
