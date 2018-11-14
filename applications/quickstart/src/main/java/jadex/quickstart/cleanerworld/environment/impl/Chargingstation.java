package jadex.quickstart.cleanerworld.environment.impl;

import jadex.quickstart.cleanerworld.environment.IChargingstation;

/**
 *  (Knowledge about) a charging station.
 */
public class Chargingstation extends LocationObject	implements IChargingstation
{
	/** The instance counter. */
	private static int instancecnt = 0;

	/**
	 *  Get an instance number.
	 */
	private static synchronized int	getNumber()
	{
		return ++instancecnt;
	}

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
		super("Chargingstation #" + getNumber(), location);
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Chargingstation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Chargingstation(" + "id=" + getId() + ", location=" + getLocation() + ")";
	}

	
	/**
	 *  Copy the object.
	 */
	@Override
	public Chargingstation clone()
	{
		return (Chargingstation)super.clone();
	}
}
