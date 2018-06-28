package jadex.bdi.examples.hunterprey_classic;


/**
 *  Editable Java class for concept Obstacle of hunterprey ontology.
 */
public class Obstacle extends WorldObject
{
	//-------- constructors --------

	/**
	 *  Create a new Obstacle.
	 */
	public Obstacle()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new Obstacle.
	 */
	public Obstacle(Location location)
	{
		// Constructor using required slots (change if desired).
		setLocation(location);
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Obstacle.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Obstacle(" + "location=" + getLocation() + ")";
	}
}
