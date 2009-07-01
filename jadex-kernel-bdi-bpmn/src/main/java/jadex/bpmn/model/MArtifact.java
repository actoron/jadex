package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MArtifact extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The associations. */
	protected List associations;
	
	/** The type. */
	protected String type;
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public List getAssociations()
	{
		return associations;
	}
	
	/**
	 * 
	 */
	public void addAssociation(MAssociation association)
	{
		if(associations==null)
			associations = new ArrayList();
		associations.add(association);
	}
	
	/**
	 * 
	 */
	public void removeAssociation(MAssociation association)
	{
		if(associations!=null)
			associations.remove(association);
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
}
