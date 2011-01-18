package jadex.component.model;

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

//	/** Flag if search is forced. */
//	protected boolean forced;
//	
//	/** Flag if search is forced. */
//	protected boolean remote;
//	
//	/** Flag if search is declared. */
//	protected boolean declared;
	
	/** The scope. */
	protected String scope;
	
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

	/**
	 *  Get the scope.
	 *  @return the scope.
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set.
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}
	
//	/**
//	 *  Get the forced.
//	 *  @return the forced.
//	 */
//	public boolean isForced()
//	{
//		return forced;
//	}
//
//	/**
//	 *  Set the forced.
//	 *  @param forced The forced to set.
//	 */
//	public void setForced(boolean forced)
//	{
//		this.forced = forced;
//	}
//
//	/**
//	 *  Get the remote.
//	 *  @return the remote.
//	 */
//	public boolean isRemote()
//	{
//		return remote;
//	}
//
//	/**
//	 *  Set the remote.
//	 *  @param remote The remote to set.
//	 */
//	public void setRemote(boolean remote)
//	{
//		this.remote = remote;
//	}
//
//	/**
//	 *  Get the declared.
//	 *  @return the declared.
//	 */
//	public boolean isDeclared()
//	{
//		return declared;
//	}
//
//	/**
//	 *  Set the declared.
//	 *  @param declared The declared to set.
//	 */
//	public void setDeclared(boolean declared)
//	{
//		this.declared = declared;
//	}
}

