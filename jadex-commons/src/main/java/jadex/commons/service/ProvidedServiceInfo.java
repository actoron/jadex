package jadex.commons.service;

/**
 *  Info for provided services.
 */
public class ProvidedServiceInfo
{
	//-------- attributes --------
	
	/** The service interface type. */
	protected Class type;
	
	/** The creation expression. */
	protected String expression;
	
	/** The direct flag. */
	protected boolean direct;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(Class type)
	{
		this(type, null);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(Class type, String expression)
	{
		this(type, expression, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(Class type, String expression, boolean direct)
	{
		this.type = type;
		this.expression = expression;
		this.direct = direct;
	}

	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}

	/**
	 *  Get the expression.
	 *  @return The expression.
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 *  Set the expression.
	 *  @param expression The expression to set.
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 *  Get the direct.
	 *  @return the direct.
	 */
	public boolean isDirect()
	{
		return direct;
	}

	/**
	 *  Set the direct.
	 *  @param direct The direct to set.
	 */
	public void setDirect(boolean direct)
	{
		this.direct = direct;
	}
}
