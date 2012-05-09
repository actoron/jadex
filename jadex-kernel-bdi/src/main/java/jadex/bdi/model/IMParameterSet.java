package jadex.bdi.model;

/**
 *  Create a new parameter set.
 */
public interface IMParameterSet extends IMTypedElement
{	
	/**
	 *  Get the parameter value.
	 *  @return The value.
	 */
	public IMExpression[] getValues();
	
	/**
	 *  Get the values expression.
	 *  @return The values expression.
	 */
	public IMExpression getValuesExpression();
	
	/**
	 *  Get the parameter set direction.
	 *  @return The direction.
	 */
	public String getDirection();
	
	/**
	 *  Flag if parameter set is optional.
	 *  @return True if optional.
	 */
	public boolean isOptional();
}
