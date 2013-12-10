package jadex.bdiv3.examples.cleanerworld.world;


/**
 *  Editable Java class for concept Waste of cleaner-generated ontology.
 */
public class Waste	extends LocationObject
{
	//-------- static attributes --------

	protected static int wastecnt;
	
	/**
	 *  Get an instance number.
	 */
	protected static synchronized int	getNumber()
	{
		return ++wastecnt;
	}

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
		this("Waste_#"+getNumber(), location);
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

