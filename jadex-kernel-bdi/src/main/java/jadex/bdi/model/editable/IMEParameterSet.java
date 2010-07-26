package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameterSet;

/**
 *
 */
public interface IMEParameterSet extends IMParameterSet, IMETypedElement
{
	/**
	 *  Get the parameter value.
	 *  @return The value.
	 */
	public IMExpression createValue();
	
	/**
	 *  Get the values expression.
	 *  @return The values expression.
	 */
	public IMExpression createValuesExpression();
	
	/**
	 *  Get the parameter set direction.
	 *  @return The direction.
	 */
	public void setDirection(String dir);
	
	/**
	 *  Flag if parameter set is optional.
	 *  @return True if optional.
	 */
	public void setOptional(boolean optional);
}

