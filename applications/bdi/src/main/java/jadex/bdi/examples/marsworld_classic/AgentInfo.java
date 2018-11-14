package jadex.bdi.examples.marsworld_classic;

/**
 *  The agent info class containing info about the robots.
 */
public class AgentInfo
{
	//-------- attributes --------

	/** The agents name. */
	protected String name;

	/** The type. */
	protected String type;

	/** The location. */
	protected Location location;

	/** The vision. */
	protected double vision;

	//-------- constructors --------

	/**
	 *  Create a new agent info.
	 */
	public AgentInfo(String name, String type, Location location, double vision)
	{
		this.name = name;
		this.type = type;
		this.location = location;
		this.vision = vision;
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Get the location.
	 *  @return The location.
	 */
	public Location getLocation()
	{
		return this.location;
	}

	/**
	 *  Get the vision.
	 *  @return The vision.
	 */
	public double getVision()
	{
		return this.vision;
	}


	/**
	 *  Set the location.
	 *  @param location The location.
	 */
	public void setLocation(Location location)
	{
		this.location = location;
	}

	/** Setter for name.
	 * @param name The AgentInfo.java value to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/** Setter for type.
	 * @param type The AgentInfo.java value to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/** Setter for vision.
	 * @param vision The AgentInfo.java value to set
	 */
	public void setVision(double vision)
	{
		this.vision = vision;
	}

	/**
	 *  Two agent infos are the same
	 *  when their name is equal.
	 */
	public boolean equals(Object o)
	{
		return (o instanceof AgentInfo && ((AgentInfo)o).getName().equals(name));
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + name.hashCode();
	}
}