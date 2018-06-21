package jadex.bdiv3.examples.cleanerworld.world;


/**
 *  Editable Java class for concept Chargingstation of cleaner-generated ontology.
 */
public class Chargingstation extends LocationObject
{
	/** The instance counter. */
	protected static int instancecnt = 0;

	/**
	 *  Get an instance number.
	 */
	protected static synchronized int	getNumber()
	{
		return ++instancecnt;
	}

	//-------- attributes ----------

	/** Attribute for slot name. */
	protected String name;

	//-------- constructors --------

	/**
	 *  Create a new Chargingstation.
	 */
	public Chargingstation()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new charging station.
	 */
	public Chargingstation(Location location)
	{
		this("Chargingstation #" + getNumber(), location);
	}

	/**
	 *  Create a new Chargingstation.
	 */
	public Chargingstation(String name, Location location)
	{
		setId(name);
		setName(name);
		setLocation(location);
	}

	/**
	 *  Get the name of this Chargingstation.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this Chargingstation.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Update this wastebin.
	 */
	public void update(Chargingstation st)
	{
		assert this.getId().equals(st.getId());
	}
	
	//-------- object methods --------

	/**
	 *  Get a string representation of this Chargingstation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Chargingstation(" + "id=" + getId() + ", location=" + getLocation() + ", name=" + getName() + ")";
	}
}
