package jadex.bridge.service;

import jadex.commons.SReflect;

/**
 * 
 */
public class ProvidedServiceImplementation
{
	// todo: use UnparsedExpression instead of implementation and expression text?
	
	/** The implementation class. */
	protected Class implementation;

	/** The creation expression. */
	protected String expression;
		
	/** The binding for forwarding service calls. */
	protected RequiredServiceBinding binding;

	/** The direct flag. */
	protected boolean direct;
	
	/**
	 * 
	 */
	public ProvidedServiceImplementation()
	{
	}
	
	/**
	 * 
	 */
	public ProvidedServiceImplementation(Class implementation,
		String expression, boolean direct, RequiredServiceBinding binding)
	{
		this.implementation = implementation;
		this.expression = expression;
		this.direct = direct;
		this.binding = binding;
	}

	/**
	 *  Get the implementation.
	 *  @return The implementation.
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
	 *  @return The direct.
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

	/**
	 *  Get the binding.
	 *  @return The binding.
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return implementation!=null? SReflect.getInnerClassName(implementation): 
			expression!=null? expression: binding!=null? 
			binding.getComponentName()!=null? binding.getComponentName(): 
				binding.getComponentType(): "";
	}
}
