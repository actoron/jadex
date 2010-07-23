package jadex.bdi.model;

/**
 *  Interface for parameter model.
 */
public interface IMParameter extends IMTypedElement
{
	/**
	 *  Get the parameter value.
	 *  @return The value.
	 */
	public IMExpression getValue();
	
	/**
	 *  Get the binding options.
	 *  @return The binding options.
	 */
	public IMExpression getBindingOptions();
	
	/**
	 *  Get the parameter direction.
	 *  @return The direction.
	 */
	public String getDirection();
	
	/**
	 *  Flag if parameter is optional.
	 *  @return True if optional.
	 */
	public boolean isOptional();
	
}
