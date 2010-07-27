package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameter;

/**
 * 
 */
public interface IMEParameter extends IMParameter, IMETypedElement
{
	/**
	 *  Create the parameter value.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The value.
	 */
	public IMExpression createValue(String expression, String language);
	
	/**
	 *  Create the binding options.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The binding options.
	 */
	public IMExpression createBindingOptions(String expression, String language);
	
	/**
	 *  Set the parameter direction.
	 *  @param dir The direction.
	 */
	public void setDirection(String dir);
	
	/**
	 *  Flag if parameter is optional.
	 *  @param optional True if optional.
	 */
	public void setOptional(boolean optional);
}
