package jadex.bdi.examples.cleanerworld_classic;


/**
 *  Editable Java class for concept Waste of cleaner-generated ontology.
 */
public class Waste	extends LocationObject
{
	//-------- static attributes --------

	protected static int wastecnt;

	//-------- constructors --------

	/**
	 *  Create a new Waste.
	 */
	public Waste()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new waste.
	 *  @param location	The location.
	 */
	public Waste(Location location)
	{
		this("Waste_#"+wastecnt++, location);
	}

	/**
	 *  Create a new Waste.
	 */
	public Waste(String id, Location location)
	{
		setId(id);
		setLocation(location);
	}

	//-------- methods --------
	
	/**
	 *  Get a string representation of this Waste.
	 *  @return The string representation.
	 */
	public String toString() {
		return "Waste("
		+ "id="+getId()
		+ ", location="+getLocation()
           + ")";
	}
	
	//-------- custom code --------
}

