package jadex.application.model;

import jadex.bridge.service.RequiredServiceBinding;


/**
 *  Provided service type.
 */
public class MProvidedServiceType extends MExpressionType
{
	//-------- attributes --------

	/** The proxtype attribute. */
	protected String proxytype;
	
	/** The binding. */
	protected RequiredServiceBinding binding;
	
	/** The implementation class. */
	protected Class implementation;
	
	//-------- constructors --------

	/**
	 *  Create a new expression.
	 */
	public MProvidedServiceType()
	{
	}

	//-------- methods --------

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
	 *  Get the implementation.
	 *  @return the implementation.
	 */
	public Class getImplementation()
	{
		return implementation;
	}

	/**
	 *  Set the implementation.
	 *  @param implementation The implementation to set.
	 */
	public void setImplementation(Class implementation)
	{
		this.implementation = implementation;
	}

	public String getProxytype()
	{
		return proxytype;
	}
	

	public void setProxytype(String proxytype)
	{
		this.proxytype	= proxytype;
	}
	
}
