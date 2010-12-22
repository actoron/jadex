package jadex.application.model;

/**
 *  Required service type.
 */
public class MRequiredServiceType extends MExpressionType
{
	//-------- attributes --------

	/** Flag if binding is dynamic. */
	protected boolean dynamic;

	/** Flag if multiple services should be returned. */
	protected boolean multiple;

	/** Flag if search is forced. */
	protected boolean forced;
	
	/** Flag if search is forced. */
	protected boolean remote;
	
	/** Flag if search is declared. */
	protected boolean declared;
	
	//-------- constructors --------

	/**
	 *  Create a new service.
	 */
	public MRequiredServiceType()
	{
	}

	//-------- methods --------

	/**
	 *  Get the dynamic.
	 *  @return the dynamic.
	 */
	public boolean isDynamic()
	{
		return dynamic;
	}


	/**
	 *  Set the dynamic.
	 *  @param dynamic The dynamic to set.
	 */
	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}


	/**
	 *  Get the multiple.
	 *  @return the multiple.
	 */
	public boolean isMultiple()
	{
		return multiple;
	}


	/**
	 *  Set the multiple.
	 *  @param multiple The multiple to set.
	 */
	public void setMultiple(boolean multiple)
	{
		this.multiple = multiple;
	}
	
}

