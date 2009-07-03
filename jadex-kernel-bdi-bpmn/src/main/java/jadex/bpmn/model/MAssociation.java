package jadex.bpmn.model;

/**
 * 
 */
public class MAssociation extends MIdElement
{
	//-------- attributes --------
	
	/** The source. */
	protected MArtifact source;
	
	/** The target. */
	protected IAssociationTarget target;

	/** The type. */
	protected String type;
	
	//-------- methods --------
	
	/**
	 * @return the source
	 */
	public MArtifact getSource()
	{
		return this.source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(MArtifact source)
	{
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public IAssociationTarget getTarget()
	{
		return this.target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(IAssociationTarget target)
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
	
}
