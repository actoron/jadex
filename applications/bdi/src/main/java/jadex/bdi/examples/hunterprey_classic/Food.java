package jadex.bdi.examples.hunterprey_classic;


/**
 *  Editable Java class for concept Food of hunterprey ontology.
 */
public class Food extends WorldObject
{
	//-------- constructors --------

	/**
	 *  Create a new Food.
	 */
	public Food()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new Food.
	 */
	public Food(Location location)
	{
		// Constructor using required slots (change if desired).
		setLocation(location);
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Food.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Food(" + "location=" + getLocation() + ")";
	}
}
