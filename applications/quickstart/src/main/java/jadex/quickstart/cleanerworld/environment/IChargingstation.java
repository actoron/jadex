package jadex.quickstart.cleanerworld.environment;

/**
 *  (Knowledge about) a charging station.
 */
public interface IChargingstation	extends ILocationObject
{
	/**
	 *  Get the id (or name) of this object.
	 *  @return The id.
	 */
	public String getId();

	/**
	 *  Get the location of this object.
	 * @return The location of the object.
	 */
	public ILocation getLocation();
}
