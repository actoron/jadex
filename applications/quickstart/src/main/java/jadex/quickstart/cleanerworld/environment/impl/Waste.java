package jadex.quickstart.cleanerworld.environment.impl;

import jadex.quickstart.cleanerworld.environment.IWaste;

/**
 *  (Knowledge about) a piece of waste.
 */
public class Waste	extends LocationObject	implements IWaste
{
	//-------- static attributes --------

	private static int wastecnt;
	
	/**
	 *  Get an instance number.
	 */
	private static synchronized int	getNumber()
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
		super("Waste_#"+getNumber(), location);
	}

	//-------- methods --------
	
	/**
	 *  Get a string representation of this Waste.
	 *  @return The string representation.
	 */
	public String toString() {
		return "Waste(" + "id="+getId() + ", location="+getLocation() + ")";
	}
	
	/**
	 *  Copy the object.
	 */
	@Override
	public Waste clone()
	{
		return (Waste)super.clone();
	}
}

