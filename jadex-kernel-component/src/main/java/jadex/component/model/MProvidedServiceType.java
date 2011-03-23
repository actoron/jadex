package jadex.component.model;

import jadex.bridge.service.RequiredServiceBinding;


/**
 *  Provided service type.
 */
public class MProvidedServiceType extends MExpressionType
{
	//-------- attributes --------

	/** The direct attribute. */
	protected boolean direct;
	
	/** The binding. */
	protected RequiredServiceBinding binding;
	
	//-------- constructors --------

	/**
	 *  Create a new expression.
	 */
	public MProvidedServiceType()
	{
	}

	//-------- methods --------
	
	/**
	 *  Get the direct flag.
	 *  @return the direct.
	 */
	public boolean isDirect()
	{
		return direct;
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

	/**
	 *  Set the direct flag.
	 *  @param direct The direct to set.
	 */
	public void setDirect(boolean direct)
	{
		this.direct = direct;
	}
}
