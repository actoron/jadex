package jadex.quickstart.cleanerworld.environment;


/**
 *  A location on the virtual map.
 */
public interface ILocation
{
	/**
	 *  Get the x of this Location.
	 *  The x-coordinate.
	 * @return x
	 */
	public double getX();

	/**
	 *  Get the y of this Location.
	 *  The y-coordinate.
	 * @return y
	 */
	public double getY();

	/**
	 *  Caculate the distance to another location.
	 *  @return The distance.
	 */
	public double getDistance(ILocation other);

	/**
	 *  Check, if the other location is in range.
	 *  E.g. when the chargin station is near to the cleaner it can recharge, etc.
	 *  @return True, if the given locations is near to this location.
	 */
	public boolean isNear(ILocation other);
}
