package jadex.application.model;


/**
 *  Provided service type.
 */
public class MProvidedServiceType extends MExpressionType
{
	//-------- attributes --------

	/** The component name. */
	protected String componentname;

	/** The component type. */
	protected String componenttype;
	
	/** The direct attribute. */
	protected boolean direct;
	
	//-------- constructors --------

	/**
	 *  Create a new expression.
	 */
	public MProvidedServiceType()
	{
	}

	//-------- methods --------
	
	/**
	 *  Get the componentname.
	 *  @return the componentname.
	 */
	public String getComponentName()
	{
		return componentname;
	}

	/**
	 *  Set the componentname.
	 *  @param componentname The componentname to set.
	 */
	public void setComponentName(String componentname)
	{
		this.componentname = componentname;
	}
	
	/**
	 *  Get the component type.
	 *  @return the component type.
	 */
	public String getComponentType()
	{
		return componenttype;
	}

	/**
	 *  Set the component type.
	 *  @param componenttype The component type to set.
	 */
	public void setComponentType(String componenttype)
	{
		this.componenttype = componenttype;
	}

	/**
	 *  Get the direct flag.
	 *  @return the direct.
	 */
	public boolean isDirect()
	{
		return direct;
	}

	/**
	 *  Set the direct flag.
	 *  @param direct The direct to set.
	 */
	public void setDirect(boolean direct)
	{
		this.direct = direct;
	}
}
