package jadex.bdiv3.model;

/**
 * 
 */
public class MElement
{
	/** The element name. */
	protected String name;

	/**
	 *  Create a new element.
	 */
	public MElement(String name)
	{
		if(name.equals("Object"))
			System.out.println("gggsdg");
		this.name = name;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
