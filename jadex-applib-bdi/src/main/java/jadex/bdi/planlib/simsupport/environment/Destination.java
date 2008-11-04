package jadex.bdi.planlib.simsupport.environment;

import javax.swing.text.Position;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

/** A SimObject destination.
 */
public class Destination
{
	/** Position of the destination.
	 */
	private IVector2 position_;
	
	/** Tolerance used when considering whether the destination has been reached.
	 */
	private IVector1 tolerance_;
	
	public Destination(IVector2 position,
					   IVector1 tolerance)
	{
		position_ = position.copy();
		tolerance_ = tolerance.copy();
	}
	
	/** Returns the destination position.
	 * 
	 * @return destination position
	 */
	public IVector2 getPosition()
	{
		return position_;
	}
	
	/** Returns the tolerance.
	 * 
	 * @return the tolerance
	 */
	public IVector1 getTolerance()
	{
		return tolerance_;
	}
}
