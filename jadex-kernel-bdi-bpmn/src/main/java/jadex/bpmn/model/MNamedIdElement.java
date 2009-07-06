package jadex.bpmn.model;

/**
 * 
 */
public class MNamedIdElement extends MIdElement
{
	/** The name. */
	protected String name;

	/**
	 * @return the name
	 * /
	public String getName()
	{
		return this.name;
	}*/
	
	/**
	 * 
	 */
	public String getName()
	{
		String	name	= this.name;
		if(name!=null && name.indexOf("\r")!=-1)
			name	= name.substring(0, name.indexOf("\r"));
		if(name!=null && name.indexOf("\n")!=-1)
			name	= name.substring(0, name.indexOf("\n"));
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return name;
	}
}
