package jadex.commons.transformation.traverser;

/**
 *  Interface for objects that should be transformed.
 */
public interface ITransformableObject
{
	/**
	 *  Return a transformed object.
	 *  @return A transformed version of the object.
	 */
	public Object transform();
}
