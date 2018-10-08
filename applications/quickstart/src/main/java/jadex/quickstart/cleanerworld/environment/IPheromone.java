package jadex.quickstart.cleanerworld.environment;

/**
 *  Environment representation of a pheromone.
 */
public interface IPheromone	extends ILocationObject
{
	/** The evaporation rate determines how quickly a pheromone dissolves (in strength per second). */
	public static final double	EVAPORATION_RATE	= 0.05; 
	
	/**
	 *  Get the id (or name) of this object.
	 *  @return The id.
	 */
	public String getId();

	/**
	 *  Get the location of this object.
	 *  @return The location of the object.
	 */
	public ILocation getLocation();
	
	/**
	 *  Get the type of the pheromone.
	 *  @return The pheromone type as string.
	 */
	public String getType();
	
	/**
	 *  Get the strength of the pheromone (0..1).
	 *  @return The current strength of the pheromone.
	 */
	public double getStrength();
}
