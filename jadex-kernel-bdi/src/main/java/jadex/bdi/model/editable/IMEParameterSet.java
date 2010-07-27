package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameterSet;

/**
 *
 */
public interface IMEParameterSet extends IMParameterSet, IMETypedElement
{
	/**
	 *  Add a parameter value.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The value.
	 */
	public IMExpression addValue(String expression, String language);
	
	/**
	 *  @return The values expression.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  Create the values expression.
	 */
	public IMExpression createValuesExpression(String expression, String language);
	
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

