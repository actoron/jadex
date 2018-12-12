package jadex.bpmn.model;

/**
 *  An association is a connection between an 
 *  artifact and some other thing.
 */
public class MAssociation extends MAnnotationElement
{
	//-------- attributes --------
	
	/** The source. */
	protected MArtifact source;
	
	/** The target. */
	protected MAssociationTarget target;

	/** The type. */
	protected String type;
	
	//-------- methods --------
	
	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public MArtifact getSource()
	{
		return this.source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(MArtifact source)
	{
		this.source = source;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public MAssociationTarget getTarget()
	{
		return this.target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(MAssociationTarget target)
	{
		this.target = target;
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
