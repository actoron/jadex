package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameter;

/**
 * 
 */
public interface IMEParameter extends IMParameter, IMETypedElement
{
	/**
	 *  Get the parameter value.
	 *  @return The value.
	 */
	public IMExpression createValue();
	
	/**
	 *  Get the binding options.
	 *  @return The binding options.
	 */
	public IMExpression createBindingOptions();
	
	/**
	 *  Get the parameter direction.
	 *  @return The direction.
	 */
	public void setDirection(String dir);
	
	/**
	 *  Flag if parameter is optional.
	 *  @return True if optional.
	 */
	public void setOptional(boolean optional);
}
