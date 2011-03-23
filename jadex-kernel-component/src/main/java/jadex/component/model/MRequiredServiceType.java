package jadex.component.model;

import jadex.bridge.service.RequiredServiceBinding;

/**
 *  Required service type.
 */
public class MRequiredServiceType extends MExpressionType
{
	//-------- attributes --------

	/** Flag if multiple services should be returned. */
	protected boolean multiple;

	/** The binding. */
	protected RequiredServiceBinding binding;
	
	//-------- constructors --------

	/**
	 *  Create a new service.
	 */
	public MRequiredServiceType()
	{
	}

	//-------- methods --------

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
	 *  Get the binding.
	 *  @return the binding.
	 */
	public RequiredServiceBinding getBinding()
	{
		return binding;
	}

	/**
	 *  Set the binding.
	 *  @param binding The binding to set.
	 */
	public void setBinding(RequiredServiceBinding binding)
	{
		this.binding = binding;
	}
}

