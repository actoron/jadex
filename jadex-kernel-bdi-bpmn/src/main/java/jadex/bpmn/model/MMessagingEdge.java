package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MMessagingEdge extends MNamedIdElement implements IAssociationTarget
{
	//-------- attributes --------
	
	/** The association description. */
	protected String associationsdescription;

	/** The source description. */
	protected String sourcedescription;

	/** The target description. */
	protected String targetdescription;
	
	
	/** The outgoing edges. */
	protected MActivity source;
	
	/** The incoming edges. */
	protected MActivity target;
	
	/** The type. */
	protected String type;
	
	/** The associations. */
	protected List associations;
	
	//-------- methods --------

	/**
	 * @return the associationsdescription
	 */
	public String getAssociationsDescription()
	{
		return this.associationsdescription;
	}

	/**
	 * @param associationsdescription the associationsdescription to set
	 */
	public void setAssociationsDescription(String associationsdescription)
	{
		this.associationsdescription = associationsdescription;
	}
	
	/**
	 * @return the sourcedescription
	 */
	public String getSourceDescription()
	{
		return this.sourcedescription;
	}

	/**
	 * @param sourcedescription the sourcedescription to set
	 */
	public void setSourceDescription(String sourcedescription)
	{
		this.sourcedescription = sourcedescription;
	}

	/**
	 * @return the targetdescription
	 */
	public String getTargetDescription()
	{
		return this.targetdescription;
	}

	/**
	 * @param targetdescription the targetdescription to set
	 */
	public void setTargetDescription(String targetdescription)
	{
		this.targetdescription = targetdescription;
	}

	/**
	 * 
	 */
	public MActivity getSource()
	{
		return source;
	}
	
	/**
	 * 
	 */
	public MActivity getTarget()
	{
		return target;
	}
	
	/**
	 * 
	 */
	public void setSource(MActivity source)
	{
		this.source = source;
	}
	
	/**
	 * 
	 */
	public void setTarget(MActivity target)
	{
		this.target = target;
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

}
