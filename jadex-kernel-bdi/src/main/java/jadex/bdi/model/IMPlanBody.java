package jadex.bdi.model;


/**
 *  The plan body specifies how a plan can be instantiated.
 */
// Hack!!! Shouldn't be expression.
public interface IMPlanBody	extends IMExpression
{
	/**
	 *  Get the body type (e.g. 'standard').
	 */
	public String	getType();

	/**
	 *  Get the body implementation (e.g. file name).
	 */
	public String	getImplementation();
}
