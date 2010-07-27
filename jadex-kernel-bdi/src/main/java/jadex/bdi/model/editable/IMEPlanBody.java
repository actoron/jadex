package jadex.bdi.model.editable;

import jadex.bdi.model.IMPlanBody;

/**
 *  Editable interface for plan body.
 */
//Hack!!! Shouldn't be expression.
public interface IMEPlanBody	extends IMPlanBody, IMEExpression
{
	/**
	 *  Set the body type (e.g. 'standard').
	 *  @param type	The type. 
	 */
	public void	setType(String type);

	/**
	 *  Set the body implementation (e.g. file name).
	 *  @param impl	The implementation.
	 */
	public void	setImplementation(String impl);
}
