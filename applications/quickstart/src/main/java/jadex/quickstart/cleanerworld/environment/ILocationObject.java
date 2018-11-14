package jadex.quickstart.cleanerworld.environment;

/**
 *  Base interface for all environment opbjects.
 */
public interface ILocationObject
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
