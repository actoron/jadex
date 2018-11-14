package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  An artifact is a thing such as a text associated to 
 *  some other thing such as an activity.
 */
public class MArtifact extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The associations. */
	protected List<MAssociation> associations;
	
	/** The type. */
	protected String type;
	
	//-------- methods --------
	
	/**
	 *  Get the associations.
	 *  return The associations.
	 */
	public List<MAssociation> getAssociations()
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
			associations = new ArrayList<MAssociation>();
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

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
}
